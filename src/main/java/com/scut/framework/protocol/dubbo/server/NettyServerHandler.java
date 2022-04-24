package com.scut.framework.protocol.dubbo.server;

import com.scut.framework.protocol.Invocation;
import com.scut.framework.protocol.dubbo.protostuff.ProtostuffUtil;
import com.scut.framework.protocol.dubbo.vo.MessageType;
import com.scut.framework.protocol.dubbo.vo.MsgHeader;
import com.scut.framework.protocol.dubbo.vo.MyMessage;
import com.scut.framework.register.LocalRegister;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyMessage myMsg = (MyMessage)msg;
        Invocation invocation = myMsg.getBody();
        Class serviceImpl = LocalRegister.get(invocation.getInterfaceName());

        Method method = serviceImpl.getMethod(invocation.getMethodName(), invocation.getParamTypes());
        Object result = method.invoke(serviceImpl.newInstance(), invocation.getParams());

        invocation.setMsg("Netty:" + result);
        MyMessage message = buildMsg(invocation);
        System.out.println("Netty-------------" + result.toString());
        ctx.writeAndFlush(message);
    }

    private MyMessage buildMsg(Invocation invocation) {
        MyMessage message = new MyMessage();
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setType(MessageType.SERVICE_REQ.value());
        message.setMsgHeader(msgHeader);
        message.setBody(invocation);
        return message;
    }
}
