package com.scut.framework.spi.api.impl;



import com.scut.framework.spi.annocation.SPI;
import com.scut.framework.spi.api.IExtensionLoader;
import com.scut.framework.spi.exception.SpiException;
import com.scut.framework.spi.holder.Holder;
import com.scut.framework.spi.spiconst.SpiConst;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionLoader<T> implements IExtensionLoader<T> {

    /**
     * 接口定义
     */
    private final Class<T> spiClass;

    /**
     * 类加载器
     */
    private final ClassLoader classLoader;


    /**
     * 缓存的对象实例
     */
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    /**
     * 实例别名 map
     */
    private final Map<String, String> classAliasMap = new ConcurrentHashMap<>();

    public <T> ExtensionLoader(Class spiClass, ClassLoader classLoader) {
        spiClassCheck(spiClass);

        if(classLoader == null) {
            throw new SpiException("classLoader is null");
        }
        this.spiClass = spiClass;
        this.classLoader = classLoader;

        this.initSpiConfig();//对META-INF/services/下的文件进行解析
    }

    public <T> ExtensionLoader(Class<T> spiClass) {
        this(spiClass, Thread.currentThread().getContextClassLoader());
    }

    /**
     * 参数校验
     *
     * 1. 不能为null
     * 2. 必须是接口
     * 3. 必须指定SPI注解
     * @param spiClass
     */
    private void spiClassCheck(final Class<T> spiClass) {
        if(spiClass == null) {
            throw new SpiException("class is null");
        }

        if(!spiClass.isInterface()) {
            throw new SpiException("Spi class is not interface, " + spiClass);
        }

        if(!spiClass.isAnnotationPresent(SPI.class)) {
            throw new SpiException("Spi class is must be annotated with @SPI, " + spiClass);
        }
    }

    /**
     * 获取对应的拓展信息
     *
     * @param alias 别名
     * @return
     */
    @Override
    public T getExtension(String alias) {
        if(alias == null || alias.equals("")) {
            throw new SpiException("alias is null");
        }

        Holder<Object> holder = getOrCreateHolder(alias);

        Object instance = holder.get();

        //DCL
        if(instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if(instance == null) {
                    instance = createInstance(alias);
                    holder.set(instance);
                }
            }
        }
        return (T)instance;
    }

    private Holder<Object> getOrCreateHolder(String name) {
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        return holder;
    }
    /**
     * 实例
     * @param name 名称
     * @return 实例
     */
    @SuppressWarnings("unchecked")
    private T createInstance(String name) {
        String className = classAliasMap.get(name);
        if(className == null || className.equals("")) {
            throw new SpiException("SPI config not found for spi: " + spiClass.getName()
                    +" with alias: " + name);
        }

        try {
            Class clazz = Class.forName(className);
            return (T) clazz.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new SpiException(e.getMessage());
        }
    }

    /**
     * 初始化配置文件名称信息
     *
     * 只加载当前类的文件信息
     */
    private void initSpiConfig() {
        String fullName = SpiConst.JDK_DIR + this.spiClass.getName();

        try{
            Enumeration<URL> urlEnumeration = this.classLoader.getResources(fullName);

            if(!urlEnumeration.hasMoreElements()) {
                throw new SpiException("SPI config file for class not found: "
                        + spiClass.getName());
            }

            URL url = urlEnumeration.nextElement();
            List<String> allLines = readAllLines(url);

            // 构建 map
            if(allLines == null || allLines.size() == 0) {
                throw new SpiException("SPI config file for class is empty: " + spiClass.getName());
            }
            for(String line : allLines) {
                String[] lines = line.split("=");
                classAliasMap.put(lines[0], lines[1]);
            }
        }catch (Exception e) {
            throw  new SpiException(e.getMessage());
        }
    }

    /**
     * 读取每一行的内容
     * @param url url 信息
     * @return 结果
     */
    private List<String> readAllLines(final URL url) {

        List<String> resultList = new ArrayList<>();

        try(InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            // 按行读取信息
            String line;
            while ((line = br.readLine()) != null) {
                resultList.add(line);
            }
        } catch (Exception e) {
            throw new SpiException(e.getMessage());
        }
        return resultList;
    }

}
