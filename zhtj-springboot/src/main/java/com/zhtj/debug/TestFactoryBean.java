package com.zhtj.debug;

import org.springframework.beans.factory.FactoryBean;
// import org.springframework.stereotype.Component;

/**
 * 测试用的FactoryBean实现
 */
// @Component
public class TestFactoryBean implements FactoryBean<String> {

    @Override
    public String getObject() throws Exception {
        return "测试对象";
    }

    @Override
    public Class<?> getObjectType() {
        return String.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
} 