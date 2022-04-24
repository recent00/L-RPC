package com.scut.framework.protocol.dubbo.server;

import com.scut.framework.protocol.dubbo.Json.JSONDecoder;
import com.scut.framework.protocol.dubbo.Json.JSONEncoder;
import com.scut.framework.protocol.dubbo.protostuff.ProtostuffDecoder;
import com.scut.framework.protocol.dubbo.protostuff.ProtostuffEncoder;
import com.scut.framework.protocol.dubbo.vo.MyMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;

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
                            /*粘包半包问题*/
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(65535,
                                    0,2,0,
                                    2));
                            pipeline.addLast(new LengthFieldPrepender(2));
                            pipeline.addLast(new ProtostuffDecoder(MyMessage.class));
                            pipeline.addLast(new ProtostuffEncoder());

                            /*序列化相关*/
//                            pipeline.addLast(new JSONDecoder(MyMessage.class));
//                            pipeline.addLast(new JSONEncoder());

//                            pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers
//                                    .weakCachingConcurrentResolver(this.getClass()
//                                            .getClassLoader())));
//                            pipeline.addLast("encoder", new ObjectEncoder());

                            /*处理心跳超时*/
                            pipeline.addLast(new ReadTimeoutHandler(15));

                            /*心跳包处理*/
                            pipeline.addLast(new HeartBeatRespHandler());

                            /*业务处理相关*/
                            pipeline.addLast("handler", new NettyServerHandler());
                        }
                    });
            bootstrap.bind(hostName,port).sync();
        }catch (Exception e) {

        }

    }
}
