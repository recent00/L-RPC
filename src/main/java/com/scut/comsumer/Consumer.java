package com.scut.comsumer;

import com.scut.framework.annotation.Component;
import com.scut.framework.annotation.Reference;
import com.scut.framework.core.RpcApplication;
import com.scut.provider.api.HelloService;


@Component
public class Consumer {

    @Reference
    static HelloService helloService;

    public static void main(String[] args) {
        new RpcApplication("com.scut.comsumer","application");
        String result = helloService.sayHello("lys");
        System.out.println(result);
    }
}
