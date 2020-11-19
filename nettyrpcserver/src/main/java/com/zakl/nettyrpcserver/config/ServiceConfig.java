package com.zakl.nettyrpcserver.config;

import com.zakl.nettyrpc.common.serialize.RpcSerializeProtocol;
import com.zakl.nettyrpc.common.util.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * @author ZhangJiaKui
 * @classname ServiceConfig
 * @description 动态为各个Service 绑定ServiceBean.Filter
 * @date 11/17/2020 9:57 AM
 */
@Component
@DependsOn(BeanUtils.BeanName)
public class ServiceConfig {


    @PostConstruct
    public void init() {
        ApplicationContext applicationContext = BeanUtils.getApplicationContext();
        //该方法会注定去加载注入被@Service 注解的Bean(如果对应的Bean没有被注入进去)
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Service.class);
        //todo 将对应的类注入
        System.out.println();
    }

}
