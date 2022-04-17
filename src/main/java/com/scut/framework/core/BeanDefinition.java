package com.scut.framework.core;

public class BeanDefinition {
    private Class type;//bean的类型
    private String scope;//bean的作用域
    private boolean isLazy;//bean是否是懒加载

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isLazy() {
        return isLazy;
    }

    public void setLazy(boolean lazy) {
        isLazy = lazy;
    }
}
