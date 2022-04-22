package com.scut.framework.protocol.dubbo.protostuff;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class ProtostuffDecoder extends ByteToMessageDecoder {

    // 需要反序列对象所属的类型
    private Class<?> genericClass;

    // 构造方法，传入需要反序列化对象的类型
    public ProtostuffDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> list) throws Exception {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        list.add(ProtostuffUtil.deserializer(bytes,genericClass));
    }
}
