package com.scut.framework.protocol.dubbo;


import com.scut.framework.protocol.Invocation;
import com.scut.framework.protocol.dubbo.Json.JSONDecoder;
import com.scut.framework.protocol.dubbo.Json.JSONEncoder;
import com.scut.framework.protocol.dubbo.protostuff.ProtostuffDecoder;
import com.scut.framework.protocol.dubbo.protostuff.ProtostuffEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient {

    NettyClientHandler client = null;

    //线程池
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public void start(String hostName, Integer port) {
        client = new NettyClientHandler();

        Bootstrap b = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(65535,
                                0,2,0,
                                2));
                        pipeline.addLast(new LengthFieldPrepender(2));
//                        pipeline.addLast(new ProtostuffDecoder(Invocation.class));
//                        pipeline.addLast(new ProtostuffEncoder());
                        pipeline.addLast(new JSONDecoder(Invocation.class));
                        pipeline.addLast(new JSONEncoder());
                        pipeline.addLast("handler", client);
                    }
                });

        try {
            b.connect(hostName, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String send(String hostName, Integer port, Invocation invocation) {
        if (client == null) {
            start(hostName, port);
        }

        client.setInvocation(invocation);

        try {
            return (String) executorService.submit(client).get();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }
}
