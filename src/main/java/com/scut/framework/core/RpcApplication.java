package com.scut.framework.core;

import com.scut.framework.ProxyFactory;
import com.scut.framework.annotation.Component;
import com.scut.framework.annotation.Reference;
import com.scut.framework.annotation.Service;
import com.scut.framework.loadbalance.LoadBalancer;
import com.scut.framework.properties.RpcProperties;
import com.scut.framework.protocol.Protocol;
import com.scut.framework.protocol.URL;
import com.scut.framework.register.LocalRegister;
import com.scut.framework.register.zookeeper.Register;
import com.scut.framework.spi.api.impl.ExtensionLoader;
import com.scut.framework.spi.bs.SpiBs;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class RpcApplication {

    URL url;
    private Map<String,BeanDefinition> beanDefinitionMap = new HashMap<String,BeanDefinition>();
    private Map<String,Object> singletonObjects = new HashMap<String, Object>();//单例池

    public RpcApplication() {}

    public RpcApplication(String path,String configPath) {
        config(configPath);//解析配置文件
        scan(path);//扫描路径，判断是否加了@Service注解，将加了@Service注解的类进行服务注册

        //扫描完要创建单例bean
        //遍历beanDefinitionMap，拿到单例bean，并创建
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();

            Object bean = createBean(beanName, beanDefinition);
            singletonObjects.put(beanName,bean);
        }
    }

    //获取bean
    public Object getBean(String beanName) {
        if(!beanDefinitionMap.containsKey(beanName)) {
            //没有这个bean
            throw new NullPointerException();
        }
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

        Object singletonBean = singletonObjects.get(beanName);

        //依赖注入的时候可能要注入的bean还没创建，因此需要创建
        if(singletonBean == null) {
            singletonBean = createBean(beanName,beanDefinition);
            singletonObjects.put(beanName,singletonBean);
        }
        //单例bean
        return singletonBean;
    }

    private static Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();

        Object instance = null;
        try {
            instance = clazz.getConstructor().newInstance();//调用无参构造方法创建对象

            //依赖注入
            //遍历每个属性
            for (Field field : clazz.getDeclaredFields()) {
                //检查属性是否需要依赖注入
                if(field.isAnnotationPresent(Reference.class)) {
                    field.setAccessible(true);
                    field.set(instance,ProxyFactory.getProxy(field.getType(),RpcProperties.registerAddress));
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return instance;
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
                        //这是一个bean
                        //创建bean的定义
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setType(clazz);
                        beanDefinitionMap.put(Introspector.decapitalize(clazz.getSimpleName()),beanDefinition);//把扫描出来的bean存起来
                    }
                    //判断类上有没有Component注解
                    if(clazz.isAnnotationPresent(Component.class)) {
                        //这是一个bean
                        //创建bean的定义
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setType(clazz);
                        beanDefinitionMap.put(Introspector.decapitalize(clazz.getSimpleName()),beanDefinition);//把扫描出来的bean存起来
//                        Object instance = clazz.getConstructor().newInstance();//调用无参构造方法创建对象
//                        for (Field field : clazz.getDeclaredFields()) {
//                            if(field.isAnnotationPresent(Reference.class)) {
//                                field.setAccessible(true);
//                                field.set(instance,ProxyFactory.getProxy(field.getType(),RpcProperties.registerAddress));
//                            }
//                        }
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
        RpcProperties.loadbalancer = conf.getString("lys.rpc.loadbalancer");
        url = new URL(RpcProperties.hostName,Integer.parseInt(RpcProperties.port));
    }

    public void RpcStart() {
        ExtensionLoader<Protocol> load = SpiBs.load(Protocol.class);
        Protocol protocol = load.getExtension( RpcProperties.protocol);
        System.out.println(protocol);
        protocol.start(url);
    }
}
