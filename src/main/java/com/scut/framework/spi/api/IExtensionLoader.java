package com.scut.framework.spi.api;

public interface IExtensionLoader<T> {
    T getExtension(String alias);
}
