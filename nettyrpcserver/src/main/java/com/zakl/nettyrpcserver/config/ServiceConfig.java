package com.zakl.nettyrpcserver.config;

import com.zakl.nettyrpc.common.util.BeanUtils;
import com.zakl.nettyrpcserver.filter.Filter;
import com.zakl.nettyrpcserver.filter.ServiceFilterBinder;
import com.zakl.nettyrpcserver.netty.MessageRecvExecutor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
    public final static String SERVICE_CONFIG_BEAN_NAME = "serviceConfig";

    public final static String filter = "simpleFilter";

    @PostConstruct
    public void init() {
        ApplicationContext ctx = BeanUtils.getApplicationContext();
        //该方法会注定去加载注入被@Service 注解的Bean(如果对应的Bean没有被注入进去)
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(Service.class);
        Map<String, Object> handlerMap = MessageRecvExecutor.getInstance().getHandlerMap();
        for (String s : serviceBeanMap.keySet()) {
            //因为远程调用面向接口编程,所以单一实现接口,取index==0即可
            Class<?> c = serviceBeanMap.get(s).getClass().getInterfaces()[0];
            ServiceFilterBinder binder = new ServiceFilterBinder();
            binder.setFilter((Filter) ctx.getBean(filter));
            binder.setObject(serviceBeanMap.get(s));
            handlerMap.put(c.getName(), binder);
        }

    }

}
