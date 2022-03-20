package com.scut.framework.protocol.http;

import com.scut.framework.protocol.Invocation;
import com.scut.framework.protocol.Protocol;
import com.scut.framework.protocol.URL;

public class HttpProtocol implements Protocol {
    @Override
    public void start(URL url) {
        new HttpServer().start(url.getHostnName(),url.getPort());
    }

    @Override
    public String send(URL url, Invocation invocation) {
        return new HttpClient().send(url.getHostnName(),url.getPort(),invocation);
    }
}
