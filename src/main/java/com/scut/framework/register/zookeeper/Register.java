package com.scut.framework.register.zookeeper;

import com.scut.framework.protocol.URL;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class Register {

    //zk连接地址
    private final static String CONNNECTION_STR = "hadoop:2181";

    //注册根节点
    private final static String ZK_REGISTER_PATH = "/register";

    private static CuratorFramework curatorFramework;

    static {// 连接服务器
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(CONNNECTION_STR)
                .sessionTimeoutMs(30000)  // 会话超时时间
                .connectionTimeoutMs(30000) // 连接超时时间
                .retryPolicy(retryPolicy)
                .build();
        curatorFramework.start();
        System.out.println(curatorFramework.getState());
        //System.out.println("连接成功");
    }

    public static void register(String serviceName, URL url) {
        //注册相应的服务
        String servicePath = ZK_REGISTER_PATH + "/" + serviceName;
        String serviceAddress = url.getHostnName() + ":" + url.getPort();
        try {
            //判断 /register/product-service是否存在，不存在则创建
            if (curatorFramework.checkExists().forPath(servicePath) == null) {
                curatorFramework.create().creatingParentsIfNeeded().
                        withMode(CreateMode.PERSISTENT).forPath(servicePath, "0".getBytes());
            }
            // 组装节点地址
            String addressPath = servicePath + "/" + serviceAddress;
            String rsNode = curatorFramework.create().withMode(CreateMode.EPHEMERAL).
                    forPath(addressPath, "0".getBytes());
            System.out.println("服务注册成功：" + rsNode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
