package com.zhtj.debug;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
// import org.springframework.stereotype.Component;

/**
 * 工厂Bean检查器，用于调试FactoryBean相关问题
 */
// @Component
public class FactoryBeanInspector implements BeanPostProcessor, PriorityOrdered {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof FactoryBean) {
            System.out.println("=========================================");
            System.out.println("发现FactoryBean初始化: " + beanName);
            System.out.println("类型: " + bean.getClass().getName());
            
            try {
                Class<?> objectType = ((FactoryBean<?>) bean).getObjectType();
                System.out.println("目标对象类型: " + (objectType != null ? objectType.getName() : "null"));
            } catch (Exception e) {
                System.out.println("获取目标对象类型时出错: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println("=========================================");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
} 