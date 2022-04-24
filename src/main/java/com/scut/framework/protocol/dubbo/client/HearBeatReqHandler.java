package com.scut.framework.protocol.dubbo.client;

import com.scut.framework.protocol.dubbo.vo.MessageType;
import com.scut.framework.protocol.dubbo.vo.MsgHeader;
import com.scut.framework.protocol.dubbo.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端在长久未向服务器业务请求时，发出心跳请求报文
 */
public class HearBeatReqHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(HearBeatReqHandler.class);

    /**
     * 写空闲时触发
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT){
            MyMessage heartBeat = buildHeatBeat();
            log.info("写空闲，发出心跳报文维持连接： "+ heartBeat);
            //LOG.debug("写空闲，发出心跳报文维持连接： "+ heartBeat);
            ctx.writeAndFlush(heartBeat);
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 构造心跳包
     * @return
     */
    private MyMessage buildHeatBeat() {
        MyMessage message = new MyMessage();
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setType(MessageType.HEARTBEAT_REQ.value());
        message.setMsgHeader(msgHeader);
        return message;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyMessage message = (MyMessage) msg;
        /*是不是心跳的应答*/
        if(message.getMsgHeader() != null && message.getMsgHeader().getType() == MessageType.HEARTBEAT_RESP.value()){
            log.info("收到服务器心跳应答，服务器正常");
            ReferenceCountUtil.release(msg);
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof ReadTimeoutException){
            log.warn("服务器长时间未应答，关闭链路");
            //ctx.close();
        }
        super.exceptionCaught(ctx, cause);
    }
}
