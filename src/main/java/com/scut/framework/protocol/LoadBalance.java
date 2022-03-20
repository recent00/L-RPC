package com.scut.framework.protocol;

import java.util.List;
import java.util.Random;

/**
 * 负载均衡
 */
public class LoadBalance {

    static Integer pos = 0;
    //随机策略
    public static URL random(List<URL> list) {
        Random random = new Random();
        int n = random.nextInt(list.size());

        return list.get(n);
    }

    //轮询策略
    public static URL RoundRobin(List<URL> list) {
        if(pos >= list.size()) pos = 0;
        URL url = list.get(pos++);
        return url;
    }

}
