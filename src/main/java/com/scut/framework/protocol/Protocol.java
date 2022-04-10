package com.scut.framework.protocol;

import com.scut.framework.spi.annocation.SPI;

@SPI
public interface Protocol {

    void start(URL url);

    String send(URL url, Invocation invocation);
}
