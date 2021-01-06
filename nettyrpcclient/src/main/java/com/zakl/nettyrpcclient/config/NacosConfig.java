package com.zakl.nettyrpcclient.config;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;
import com.zakl.nettyrpcclient.core.MessageSendProxy;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author ZhangJiaKui
 * @classname NacosConfig
 * @description 配置注入nacos
 * @date 1/6/2021 3:34 PM
 */
@Configuration
public class NacosConfig {
    @NacosInjected
    private NamingService namingService;

    @PostConstruct
    public void injectNamingService() {
        MessageSendProxy.setNamingService(namingService);
    }
}
