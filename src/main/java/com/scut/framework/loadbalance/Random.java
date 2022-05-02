package com.scut.framework.loadbalance;

import com.scut.framework.protocol.URL;

import java.util.List;

/**
 * 随机算法
 */
public class Random implements LoadBalancer {
    @Override
    public URL instance(List<URL> serviceInstance) {
        java.util.Random random = new java.util.Random();
        int n = random.nextInt(serviceInstance.size());

        return serviceInstance.get(n);
    }
}
