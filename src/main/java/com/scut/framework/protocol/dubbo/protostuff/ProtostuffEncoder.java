package com.scut.framework.protocol.dubbo.protostuff;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProtostuffEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {
        byte[] bytes = ProtostuffUtil.serializer(msg);
        out.writeBytes(bytes);
    }
}
