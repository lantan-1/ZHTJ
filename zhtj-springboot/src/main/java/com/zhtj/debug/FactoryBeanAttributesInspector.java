package com.zhtj.debug;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
// import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 工厂Bean属性检查器，用于调试FactoryBeanObjectType相关问题
 */
// @Component
public class FactoryBeanAttributesInspector implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        System.out.println("================================================");
        System.out.println("开始检查FactoryBean属性");
        System.out.println("================================================");
        
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;
        
        // 遍历所有BeanDefinition
        for (String beanName : factory.getBeanDefinitionNames()) {
            BeanDefinition bd = factory.getBeanDefinition(beanName);
            
            // 尝试获取属性值
            Object factoryBeanObjectType = bd.getAttribute("factoryBeanObjectType");
            if (factoryBeanObjectType != null) {
                System.out.println("Bean: " + beanName);
                System.out.println("  factoryBeanObjectType: " + factoryBeanObjectType);
                System.out.println("  factoryBeanObjectType类型: " + factoryBeanObjectType.getClass().getName());
                System.out.println("  factoryBeanObjectType值: " + factoryBeanObjectType);
                System.out.println("--------------------------------------------");
            }
        }
        
        // 尝试使用反射查看内部状态
        try {
            System.out.println("尝试通过反射查看FactoryBean注册表");
            Field field = DefaultListableBeanFactory.class.getDeclaredField("factoryBeanObjectCache");
            field.setAccessible(true);
            Map<?, ?> cache = (Map<?, ?>) field.get(factory);
            
            System.out.println("FactoryBean缓存大小: " + (cache != null ? cache.size() : "null"));
            if (cache != null && !cache.isEmpty()) {
                for (Map.Entry<?, ?> entry : cache.entrySet()) {
                    System.out.println("  键: " + entry.getKey());
                    System.out.println("  值: " + (entry.getValue() != null ? entry.getValue().getClass().getName() : "null"));
                }
            }
        } catch (Exception e) {
            System.out.println("通过反射查看FactoryBean信息时出错: " + e.getMessage());
        }
        
        System.out.println("================================================");
    }
} 