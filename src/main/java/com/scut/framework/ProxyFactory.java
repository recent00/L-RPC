package com.scut.framework;

import com.scut.framework.properties.RpcProperties;
import com.scut.framework.protocol.*;
import com.scut.framework.protocol.http.HttpClient;
import com.scut.framework.register.RemoteMapRegister;
import com.scut.framework.register.zookeeper.Discovery;
import com.scut.framework.spi.api.impl.ExtensionLoader;
import com.scut.framework.spi.bs.SpiBs;
import com.scut.provider.api.HelloService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProxyFactory {

    public static Map<String,List<URL>> map = new HashMap<>();//本地服务列表
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(final Class interfaceClass,String address) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                try {

                    Invocation invocation = new Invocation(interfaceClass.getName(), method.getName(), method.getParameterTypes(), args);

                    //List<URL> urls = RemoteMapRegister.get(interfaceClass.getName());
                    List<URL> urls;
                    if(map.size() == 0 || map.get(interfaceClass.getName()).size() == 0) {
                        Discovery discovery = new Discovery(address);
                        urls = discovery.discover(interfaceClass.getName());
                        map.put(interfaceClass.getName(),urls);
                    }

                    URL url = LoadBalance.random(map.get(interfaceClass.getName()));
                    System.out.println("------" + map.get(interfaceClass.getName()).size());
                    ExtensionLoader<Protocol> load = SpiBs.load(Protocol.class);
                    Protocol protocol = load.getExtension(RpcProperties.protocol);
                    String result = protocol.send(url, invocation);
                    return result + url.getPort();
                }catch (Exception e) {
                    return "不好意思，执行出错了";
                }
            }
        });
    }
}
