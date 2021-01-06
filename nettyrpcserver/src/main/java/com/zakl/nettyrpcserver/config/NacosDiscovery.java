package com.zakl.nettyrpcserver.config;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.spring.context.annotation.discovery.EnableNacosDiscovery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author ZhangJiaKui
 * @classname NacosDiscovery
 * @description 注册中心配置
 * @date 1/6/2021 2:27 PM
 */
@Configuration
@EnableNacosDiscovery(globalProperties = @NacosProperties(serverAddr = "127.0.0.1:8848"))
public class NacosDiscovery {


    @NacosInjected
    private NamingService namingService;

    @Value("${netty.rpc.server.port}")
    private int serverPort;


    @Value("${netty.rpc.server.application.name}")
    private String applicationName;

    @Value("${netty.rpc.server.protocol}")
    private String protocol;

    @PostConstruct
    public void registerInstance() throws NacosException, UnknownHostException {
        Instance instance = new Instance();
        String ipv4 = Inet4Address.getLocalHost().getHostAddress();
        instance.setInstanceId(generateInstanceId(applicationName, ipv4, serverPort));
        instance.setIp(ipv4);
        instance.setPort(serverPort);
        Map<String, String> metData = new HashMap<>();
        instance.setMetadata(metData);
        metData.put("protocol", protocol);
        namingService.registerInstance(applicationName, instance);
    }

    public static String generateInstanceId(String serverName, String Ip, int port) {
        return serverName + "#" + Ip + "#" + port + "#" + UUID.randomUUID().toString();
    }
}
