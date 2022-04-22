package com.scut.framework.protocol.dubbo;

import com.scut.framework.protocol.Invocation;
import com.scut.framework.protocol.dubbo.protostuff.ProtostuffDecoder;
import com.scut.framework.protocol.dubbo.protostuff.ProtostuffEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyServer {

    public void start(String hostName,int port) {
        try {
            NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
            NioEventLoopGroup workGroup = new NioEventLoopGroup();

            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
//                            pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers
//                                    .weakCachingConcurrentResolver(this.getClass()
//                                            .getClassLoader())));
//                            pipeline.addLast("encoder", new ObjectEncoder());
//                            pipeline.addLast(new LengthFieldBasedFrameDecoder(65535,
//                                    0,2,0,
//                                    2));
//                            pipeline.addLast("decoder",new MsgPackDecoder());
//                            pipeline.addLast(new LengthFieldPrepender(2));
//                            pipeline.addLast(new MsgPackEncode());
                            pipeline.addLast(new ProtostuffDecoder(Invocation.class));
                            pipeline.addLast(new ProtostuffEncoder());
                            pipeline.addLast("handler", new NettyServerHandler());
                        }
                    });
            bootstrap.bind(hostName,port).sync();
        }catch (Exception e) {

        }

    }
}
