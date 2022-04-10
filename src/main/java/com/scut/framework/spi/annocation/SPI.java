package com.scut.framework.spi.annocation;

import java.lang.annotation.*;

/**
 * @SPI 类似于 dubbo 的注解，标识一个接口为 SPI 接口。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface SPI {
}
