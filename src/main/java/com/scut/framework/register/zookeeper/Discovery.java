package com.scut.framework.register.zookeeper;

import com.scut.framework.protocol.URL;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Discovery {

    //zk连接地址
    private String zk_address;


    private CuratorFramework curatorFramework;

    //注册根节点
    private final static String ZK_REGISTER_PATH = "/register";

    public Discovery(String address) {
        this.zk_address = address;

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(zk_address)
                .sessionTimeoutMs(15000)  // 会话超时时间
                .connectionTimeoutMs(15000) // 连接超时时间
                .retryPolicy(retryPolicy)
                .build();
        curatorFramework.start();

        //System.out.println("连接成功");
    }

    //获取指定服务节点下的子节点
    public List<URL> discover(String serviceName) {
        String path = ZK_REGISTER_PATH + "/" + serviceName;
        try {
            PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, path, true);
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            pathChildrenCache.getListenable().addListener(new NodeListener());
            List<String> repos = curatorFramework.getChildren().forPath(path);
            List<URL> urls = new ArrayList<>();
            for(String s : repos) {
                String[] strings = s.split(":");
                URL url = new URL(strings[0], Integer.parseInt(strings[1]));
                urls.add(url);
                System.out.println("获取服务信息");
            }
            return urls;
        } catch (Exception e) {
            throw new RuntimeException("获取子节点异常：" + e);
        }
    }
}
