package com.scut.framework.register.zookeeper;

import com.scut.framework.ProxyFactory;
import com.scut.framework.protocol.URL;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.util.List;

public class NodeListener implements PathChildrenCacheListener {
    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
        String path;
        String[] str;
        String s;
        String[] substr;
        URL url;
        List<URL> urls;
        switch(event.getType()) {
            case CHILD_ADDED :
                path = event.getData().getPath();
                System.out.println("point add! " +  path); // 新增节点
                str = path.split("/");
                s = str[str.length - 1];
                substr = s.split(":");
                url = new URL(substr[0],Integer.parseInt(substr[1]));
                if(ProxyFactory.map.get(str[str.length - 2]).contains(url)) {
                    break;
                }
                urls = ProxyFactory.map.get(str[str.length - 2]);
                urls.add(url);
                ProxyFactory.map.put(str[str.length - 2],urls);
                System.out.println("本地缓存：" + ProxyFactory.map.get(str[str.length - 2]).size());
                break;
            case CHILD_UPDATED :
                System.out.println("point update! " +  event.getData()); // 节点修改
                break;
            case CHILD_REMOVED :
                path = event.getData().getPath();
                System.out.println("point remove! " +  event.getData()); // 节点删除
                str = path.split("/");
                s = str[str.length - 1];
                substr = s.split(":");
                url = new URL(substr[0],Integer.parseInt(substr[1]));
                urls = ProxyFactory.map.get(str[str.length - 2]);
                urls.remove(url);
                ProxyFactory.map.put(str[str.length - 2],urls);
                System.out.println("本地缓存：" + ProxyFactory.map.get(str[str.length - 2]).size());
                break;
        }
    }
}
