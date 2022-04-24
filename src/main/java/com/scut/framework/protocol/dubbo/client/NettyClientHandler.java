package com.scut.framework.protocol.dubbo.client;

import com.scut.framework.protocol.Invocation;
import com.scut.framework.protocol.dubbo.vo.MessageType;
import com.scut.framework.protocol.dubbo.vo.MsgHeader;
import com.scut.framework.protocol.dubbo.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {


    private ChannelHandlerContext context;
    private Invocation invocation;
    private String result;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyMessage myMsg = (MyMessage)msg;
        invocation = myMsg.getBody();
        result = invocation.getMsg().toString();
        notify();
    }

    @Override
    public synchronized Object call() throws Exception {
        MyMessage msg = buildMsg();
        context.writeAndFlush(msg);
        wait();
        return result;
    }


    private MyMessage buildMsg() {
        MyMessage message = new MyMessage();
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setType(MessageType.SERVICE_REQ.value());
        message.setMsgHeader(msgHeader);
        message.setBody(invocation);
        return message;
    }

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }
}
