package com.scut.framework.protocol;

import com.scut.framework.protocol.dubbo.DubboProtocol;
import com.scut.framework.protocol.http.HttpProtocol;

public class ProtocolFactory {

    public static Protocol getProtocol() {
        String name  = System.getProperty("protocolName");
        if(name == null || name.equals("")) name = "dubbo";
        switch (name) {
            case "http": return new HttpProtocol();
            case "dubbo":return new DubboProtocol();
        }
        return new HttpProtocol();
    }
}
