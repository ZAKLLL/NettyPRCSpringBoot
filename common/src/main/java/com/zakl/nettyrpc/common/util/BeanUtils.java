package com.zakl.nettyrpc.common.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component(value = BeanUtils.BeanName)
public class BeanUtils implements ApplicationContextAware, BeanFactoryAware {
    public final static String BeanName = "beanUtils";

    private static ApplicationContext applicationContext;

    private static BeanFactory beanFactory;


    public static <T> T getBean(String name) {
        if (beanFactory == null) {
            return null;
        }
        try {
            return (T) beanFactory.getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        BeanUtils.beanFactory = beanFactory;
    }
}
