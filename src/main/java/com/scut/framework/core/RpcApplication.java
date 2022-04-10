package com.scut.framework.core;

import com.scut.framework.ProxyFactory;
import com.scut.framework.annotation.Component;
import com.scut.framework.annotation.Reference;
import com.scut.framework.annotation.Service;
import com.scut.framework.properties.RpcProperties;
import com.scut.framework.protocol.Protocol;
import com.scut.framework.protocol.URL;
import com.scut.framework.register.LocalRegister;
import com.scut.framework.register.zookeeper.Register;
import com.scut.framework.spi.api.impl.ExtensionLoader;
import com.scut.framework.spi.bs.SpiBs;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ResourceBundle;


public class RpcApplication {

    URL url;

    public RpcApplication(String path,String configPath) {
        config(configPath);//解析配置文件
        scan(path);//扫描路径，判断是否加了@Service注解，将加了@Service注解的类进行服务注册
    }

    private void scan(String path) {
        path = path.replace(".","/");
        ClassLoader classLoader = RpcApplication.class.getClassLoader();//拿到加载RpcApplication的类加载器

        //将拿到的URL封装成file
        File file = new File(classLoader.getResource(path).getFile());
        if(file.isDirectory()) {
            for(File f : file.listFiles()) {
                String absolutePath = f.getAbsolutePath();

                absolutePath = absolutePath.substring(absolutePath.indexOf("com"),absolutePath.indexOf(".class"));
                absolutePath = absolutePath.replace("\\",".");

                Class<?> clazz = null;
                try {
                    clazz = Class.forName(absolutePath);//通过反射拿到class
                    //判断类上有没有Service注解
                    if(clazz.isAnnotationPresent(Service.class)) {
                        LocalRegister.regist(clazz.getInterfaces()[0].getName(),clazz);
                        Register.register(clazz.getInterfaces()[0].getName(),url);
                    }
                    //判断类上有没有Component注解
                    if(clazz.isAnnotationPresent(Component.class)) {
                        Object instance = clazz.getConstructor().newInstance();//调用无参构造方法创建对象
                        for (Field field : clazz.getDeclaredFields()) {
                            if(field.isAnnotationPresent(Reference.class)) {
                                field.setAccessible(true);
                                field.set(instance,ProxyFactory.getProxy(field.getType(),RpcProperties.registerAddress));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void config(String configPath) {
        ResourceBundle conf = ResourceBundle.getBundle(configPath);
        RpcProperties.hostName = conf.getString("lys.rpc.host-name");
        RpcProperties.port = conf.getString("lys.rpc.port");
        RpcProperties.protocol = conf.getString("lys.rpc.protocol");
        RpcProperties.registerAddress = conf.getString("lys.rpc.register-address");
        url = new URL(RpcProperties.hostName,Integer.parseInt(RpcProperties.port));
    }

    public void RpcStart() {
        ExtensionLoader<Protocol> load = SpiBs.load(Protocol.class);
        Protocol protocol = load.getExtension( RpcProperties.protocol);
        System.out.println(protocol);
        protocol.start(url);
    }
}
