package com.scut.framework.protocol.dubbo.client;


import com.scut.framework.protocol.Invocation;
import com.scut.framework.protocol.dubbo.Json.JSONDecoder;
import com.scut.framework.protocol.dubbo.Json.JSONEncoder;
import com.scut.framework.protocol.dubbo.protostuff.ProtostuffDecoder;
import com.scut.framework.protocol.dubbo.protostuff.ProtostuffEncoder;
import com.scut.framework.protocol.dubbo.vo.MyMessage;
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
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

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

                        /*连接写空闲检测*/
                        pipeline.addLast(new IdleStateHandler(0,8,0));

                        /*粘包半包问题*/
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(65535,
                                0,2,0,
                                2));
                        pipeline.addLast(new LengthFieldPrepender(2));
                        pipeline.addLast(new ProtostuffDecoder(MyMessage.class));
                        pipeline.addLast(new ProtostuffEncoder());

                        /*序列化相关*/
//                        pipeline.addLast(new JSONDecoder(MyMessage.class));
//                        pipeline.addLast(new JSONEncoder());
//                        pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers
//                                .weakCachingConcurrentResolver(this.getClass()
//                                        .getClassLoader())));
//                        pipeline.addLast("encoder", new ObjectEncoder());

                        /*连接读空闲检测*/
                        pipeline.addLast(new ReadTimeoutHandler(15));
                        /*向服务器发出心跳请求*/
                        pipeline.addLast(new HearBeatReqHandler());

                        /*业务处理*/
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
