package com.scut.framework.spi.bs;

import com.scut.framework.spi.api.impl.ExtensionLoader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SpiBs {
    private SpiBs(){}

    /**
     * 加载类缓存
     */
    private static final Map<Class, ExtensionLoader> EX_LOADER_MAP = new ConcurrentHashMap();

    public static <T> ExtensionLoader<T> load(Class<T> clazz) {

        ExtensionLoader extensionLoader = EX_LOADER_MAP.get(clazz);

        if(extensionLoader != null) {
            return extensionLoader;
        }

        synchronized (EX_LOADER_MAP) {
            extensionLoader = EX_LOADER_MAP.get(clazz);
            if(extensionLoader == null) {
                extensionLoader = new ExtensionLoader(clazz);
            }
        }

        return extensionLoader;
    }
}
