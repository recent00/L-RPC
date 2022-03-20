package com.scut.framework.protocol.dubbo;

import com.scut.framework.protocol.Invocation;
import com.scut.framework.protocol.Protocol;
import com.scut.framework.protocol.URL;

public class DubboProtocol implements Protocol {
    @Override
    public void start(URL url) {
        new NettyServer().start(url.getHostnName(),url.getPort());
    }

    @Override
    public String send(URL url, Invocation invocation) {
        return new NettyClient().send(url.getHostnName(),url.getPort(),invocation);
    }
}
