package com.scut.provider;

import com.scut.framework.protocol.Protocol;
import com.scut.framework.protocol.ProtocolFactory;
import com.scut.framework.protocol.URL;
import com.scut.framework.protocol.http.HttpServer;
import com.scut.framework.register.LocalRegister;
import com.scut.framework.register.RemoteMapRegister;
import com.scut.framework.register.zookeeper.Register;
import com.scut.provider.api.HelloService;
import com.scut.provider.impl.HelloServiceImpl;

public class Provider {

    public static void main(String[] args) {

        //本地注册
        LocalRegister.regist(HelloService.class.getName(), HelloServiceImpl.class);

        //注册中心注册
        URL url = new URL("localhost",8082);
        //RemoteMapRegister.register(HelloService.class.getName(),url);
        Register.register(HelloService.class.getName(),url);
        //启动tomcat/netty
        Protocol protocol = ProtocolFactory.getProtocol();
        protocol.start(url);

    }
}
