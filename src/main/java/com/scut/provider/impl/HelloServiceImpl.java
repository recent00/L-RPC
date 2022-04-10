package com.scut.provider.impl;

import com.scut.framework.annotation.Service;
import com.scut.provider.api.HelloService;

@Service
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String userName) {
        return "Hello: " + userName;
    }
}
