package com.scut.framework.protocol.dubbo;

import com.scut.framework.protocol.Invocation;
import com.scut.framework.protocol.dubbo.protostuff.ProtostuffUtil;
import com.scut.framework.register.LocalRegister;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("aaaa");

        Invocation invocation = (Invocation) msg;

        Class serviceImpl = LocalRegister.get(invocation.getInterfaceName());

        Method method = serviceImpl.getMethod(invocation.getMethodName(), invocation.getParamTypes());
        Object result = method.invoke(serviceImpl.newInstance(), invocation.getParams());

        //buf = Unpooled.copiedBuffer(ProtostuffUtil.serializer(result));
        invocation.setMsg("Netty:" + result);
        System.out.println("Netty-------------" + result.toString());
        ctx.writeAndFlush(invocation);
    }
}
