package com.scut.framework.protocol.dubbo.Json;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class JSONDecoder extends MessageToMessageDecoder<ByteBuf> {

    // 需要反序列对象所属的类型
    private Class<?> genericClass;

    public JSONDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> out) throws Exception {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        out.add(JSON.parseObject(bytes,genericClass));
    }
}
