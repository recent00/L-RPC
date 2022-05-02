package com.scut.framework.loadbalance;

import com.scut.framework.protocol.URL;
import com.scut.framework.spi.annocation.SPI;

import java.util.List;

@SPI
public interface LoadBalancer {

    URL instance(List<URL> serviceInstance);
}
