package com.zakl.nettyrpcclient.config;

import com.zakl.nettyrpcclient.serialize.RpcSerializeProtocol;
import com.zakl.nettyrpcclient.util.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author ZhangJiaKui
 * @classname ServiceConfig
 * @description 动态注入接口，使用动态代理的方式进行远程调用
 * @date 11/17/2020 9:57 AM
 */
@Configuration
@DependsOn(BeanUtils.BeanName)
public class ServiceConfig {

    private static String remoteServicePackageLocation;
    private static String remoteIpAddr;
    private static int remotePort;
    private static String protocol;


    @Value(value = "${netty.rpc.server.remoteServicePackageLocation}")
    public void setRemoteServicePackageLocation(String location) {
        ServiceConfig.remoteServicePackageLocation = location;
    }

    @Value(value = "${netty.rpc.server.ipAddr}")
    public void setRemoteIpAddr(String ipAddr) {
        ServiceConfig.remoteIpAddr = ipAddr;
    }

    @Value(value = "${netty.rpc.server.port}")
    public void setRemotePort(int remotePort) {
        ServiceConfig.remotePort = remotePort;
    }

    @Value(value = "${netty.rpc.server.protocol}")
    public void setProtocol(String protooal) {
        ServiceConfig.protocol = protooal;
    }


    //动态注入bean到context中
    @PostConstruct
    public void init() {
        ApplicationContext ctx = BeanUtils.getApplicationContext();
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) ctx.getAutowireCapableBeanFactory();
        List<String> remoteInterfaces = getRemoteInterfaces();
        if (remoteInterfaces == null) return;
        for (String name : remoteInterfaces) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(NettyRpcReference.class);
            beanDefinitionBuilder.addPropertyValue("remoteInterfaceName", name);
            beanDefinitionBuilder.addPropertyValue("ipAddr", remoteIpAddr);
            beanDefinitionBuilder.addPropertyValue("port", remotePort);
            beanDefinitionBuilder.addPropertyValue("protocol", RpcSerializeProtocol.valueOf(protocol));
            defaultListableBeanFactory.registerBeanDefinition(name, beanDefinitionBuilder.getBeanDefinition());
        }
    }

    //获取对应的远程调用接口
    private static List<String> getRemoteInterfaces() {
        try {
            return getClassName(remoteServicePackageLocation, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static List<String> getClassName(String packageName, boolean childPackage) throws IOException {
        List<String> fileNames = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        Enumeration<URL> urls = loader.getResources(packagePath);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url == null)
                continue;
            String type = url.getProtocol();
            if (type.equals("file")) {
                fileNames.addAll(getAllClassNameByFile(new File(url.getPath()), childPackage, packageName));
            }
        }
        return fileNames;
    }

    private static List<String> getAllClassNameByFile(File file, boolean flag, String packageName) {
        List<String> serviceLocationList = new ArrayList<>();
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                String path = f.getAbsolutePath();
                path = path.replaceAll("\\\\", ".");
                String fullName = path.substring(path.indexOf(packageName), path.length() - 6);
                serviceLocationList.add(fullName);
            } else {
                if (flag) {
                    serviceLocationList.addAll(getAllClassNameByFile(f, flag, packageName));
                }
            }
        }
        return serviceLocationList;
    }
}
