package com.scut.comsumer;

import com.scut.framework.ProxyFactory;
import com.scut.framework.protocol.Invocation;
import com.scut.framework.protocol.http.HttpClient;
import com.scut.provider.api.HelloService;

import java.util.HashMap;

public class Consumer {

    public static void main(String[] args) {

        HelloService helloService = ProxyFactory.getProxy(HelloService.class,"hadoop:2181");

        String result = helloService.sayHello("lys");
        System.out.println(result);

        result = helloService.sayHello("lys");
        System.out.println(result);

    }
}
