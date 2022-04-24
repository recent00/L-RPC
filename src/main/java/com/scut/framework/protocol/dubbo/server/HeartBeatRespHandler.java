package com.scut.framework.protocol.dubbo.server;

import com.scut.framework.protocol.dubbo.client.HearBeatReqHandler;
import com.scut.framework.protocol.dubbo.vo.MessageType;
import com.scut.framework.protocol.dubbo.vo.MsgHeader;
import com.scut.framework.protocol.dubbo.vo.MyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 心跳包处理
 */

public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(HeartBeatRespHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyMessage message = (MyMessage) msg;
        /*是不是心跳请求*/
        if(message.getMsgHeader() != null && message.getMsgHeader().getType() == MessageType.HEARTBEAT_REQ.value()){
            /*心跳应答报文*/
            MyMessage heartBeatResp = buildHeatBeat();
            log.info("心跳应答： "+ heartBeatResp);
            ctx.writeAndFlush(heartBeatResp);
            ReferenceCountUtil.release(msg);
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 构造心跳包
     * @return
     */
    private MyMessage buildHeatBeat() {
        MyMessage message = new MyMessage();
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setType(MessageType.HEARTBEAT_RESP.value());
        message.setMsgHeader(msgHeader);
        return message;
    }

    /**
     * ReadTimeoutHandler设定的超时时间满足时触发
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof ReadTimeoutException){
            log.warn("客户端长时间未通信，可能已经宕机，关闭链路");
            ctx.close();
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("客户端已关闭连接");
        super.channelInactive(ctx);
    }
}
