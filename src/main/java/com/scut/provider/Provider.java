package com.scut.provider;

import com.scut.framework.core.RpcApplication;

public class Provider {

    public static void main(String[] args) {

        new RpcApplication("com.scut.provider.impl","application").RpcStart();

    }
}
