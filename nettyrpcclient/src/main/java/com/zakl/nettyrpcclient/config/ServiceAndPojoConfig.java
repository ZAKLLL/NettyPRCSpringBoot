package com.zakl.nettyrpcclient.config;

import com.zakl.nettyrpc.common.serialize.RpcSerializeProtocol;
import com.zakl.nettyrpc.common.util.BeanUtils;
import com.zakl.nettyrpcclient.core.NettyClientStarter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author ZhangJiaKui
 * @classname ServiceConfig
 * @description 动态注入接口，使用动态代理的方式进行远程调用,并根据配置文件连接到指定Server端
 * @date 11/17/2020 9:57 AM
 */
@Component
@DependsOn(BeanUtils.BeanName)
public class ServiceAndPojoConfig {

    private static String localServicePackage;
    private static String remoteServicePackage;
    //默认指定的远程服务ip和port
    private static String remoteIpAddr;
    private static Integer remotePort;
    private static String protocol;
    private static String pojoMapping;

    //自定义服务对应远程的rpc服务实现
    private static Map<String, String[]> serviceRemoteAddrMap = new HashMap<>();

    //pojo 本地全限定名->远程全限定名
    public static Map<String, String> localToRemotePojoMap;

    //pojo 远程全限定名->本地全限定名
    public static Map<String, String> remoteToLocalPojoMap;


    @Value(value = "${netty.rpc.server.localServicePackage}")
    public void setLocalServicePackage(String localServicePackage) {
        ServiceAndPojoConfig.localServicePackage = localServicePackage;
    }

    @Value(value = "${netty.rpc.server.remoteServicePackage}")
    public void setRemoteServicePackage(String remoteServicePackage) {
        ServiceAndPojoConfig.remoteServicePackage = remoteServicePackage;
    }

    @Value(value = "${netty.rpc.server.ipAddr}")
    public void setRemoteIpAddr(String ipAddr) {
        ServiceAndPojoConfig.remoteIpAddr = ipAddr;
    }

    @Value(value = "${netty.rpc.server.port}")
    public void setRemotePort(int remotePort) {
        ServiceAndPojoConfig.remotePort = remotePort;
    }

    @Value(value = "${netty.rpc.server.protocol}")
    public void setProtocol(String protooal) {
        ServiceAndPojoConfig.protocol = protooal;
    }

    @Value(value = "${netty.rpc.server.pojoMapping}")
    public void setPojoMapping(String pojoMapping) {
        ServiceAndPojoConfig.pojoMapping = pojoMapping;
    }

    @Value(value = "${netty.rpc.server.service.remoteAddr}")
    public void setCustomRemoteInfo(String[] customRemoteInfoS) {
        for (String customRemoteInfo : customRemoteInfoS) {
            String[] split = customRemoteInfo.split(":");
            //serviceName:ip:port:protocol
            if (split.length != 4) {
                continue;
            }
            serviceRemoteAddrMap.put(split[0], split);
        }
    }

    //动态注入bean到context中
    @PostConstruct
    public void init() {
        ApplicationContext ctx = BeanUtils.getApplicationContext();
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) ctx.getAutowireCapableBeanFactory();
        List<String> localInterfaces = getLocalInterfaces();
        if (localInterfaces == null) return;
        //注册Bean
        for (String name : localInterfaces) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(NettyRpcReference.class);
            beanDefinitionBuilder.addPropertyValue("localInterfaceName", name);
            beanDefinitionBuilder.addPropertyValue("remoteInterfaceName", name.replace(localServicePackage, remoteServicePackage));
            if (serviceRemoteAddrMap.containsKey(name)) {
                //使用自定义的远程服务
                String[] cusRemoteServerInfo = serviceRemoteAddrMap.get(name);
                String cusIp = cusRemoteServerInfo[1];
                int cusPort = Integer.parseInt(cusRemoteServerInfo[2]);
                String cusProtocol = cusRemoteServerInfo[3];
                beanDefinitionBuilder.addPropertyValue("remoteIp", cusIp);
                beanDefinitionBuilder.addPropertyValue("remotePort", cusPort);
                NettyClientStarter.connectedToServer(cusIp, cusPort, RpcSerializeProtocol.valueOf(cusProtocol));
            } else {
                beanDefinitionBuilder.addPropertyValue("remoteIp", remoteIpAddr);
                beanDefinitionBuilder.addPropertyValue("remotePort", remotePort);
            }
            defaultListableBeanFactory.registerBeanDefinition(name, beanDefinitionBuilder.getBeanDefinition());
        }
        //使用默认的ip+port+protocol启动一次
        NettyClientStarter.connectedToServer(remoteIpAddr, remotePort, RpcSerializeProtocol.valueOf(protocol));
        //配置pojo映射
        initPojoMappingMap();
    }

    public void initPojoMappingMap() {
        String[] split = pojoMapping.split(",");
        localToRemotePojoMap = new HashMap<>();
        remoteToLocalPojoMap = new HashMap<>();
        for (String s : split) {
            s = s.trim();
            if (s.length() > 2) {
                String[] packages = s.split(":");
                String localPojoPackage = packages[0];
                String remotePojoPackage = packages[1];
                try {
                    List<String> classNames = getClassNames(localPojoPackage, true);
                    for (String className : classNames) {
                        String remotePojoName = className.replace(localPojoPackage, remotePojoPackage);
                        localToRemotePojoMap.put(className, remotePojoName);
                        remoteToLocalPojoMap.put(remotePojoName, className);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //获取对应的远程调用接口
    private static List<String> getLocalInterfaces() {
        try {
            return getClassNames(localServicePackage, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static List<String> getClassNames(String packageName, boolean childPackage) throws IOException {
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
            } else if (type.equals("jar")) {
                //todo fix 打包成jar后无法获取类名
            }

        }
        return fileNames;
    }

    private static List<String> getAllClassNameByFile(File file, boolean flag, String packageName) {
        List<String> serviceLocationList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files == null) {
            return serviceLocationList;
        }
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

//    private static List<String> getClassNameByJar(String jarPath, boolean childPackage) throws UnsupportedEncodingException {
//        List<String> myClassName = new ArrayList<String>();
//        String[] jarInfo = jarPath.split("!");
//        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
//        jarFilePath = UrlDecode.getURLDecode(jarFilePath);
//        String packagePath = jarInfo[1].substring(1);
//        try {
//            JarFile jarFile = new JarFile(jarFilePath);
//            Enumeration<JarEntry> entrys = jarFile.entries();
//            while (entrys.hasMoreElements()) {
//                JarEntry jarEntry = entrys.nextElement();
//                String entryName = jarEntry.getName();
//                if (entryName.endsWith(".class")) {
//                    if (childPackage) {
//                        if (entryName.startsWith(packagePath)) {
//                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
//                            myClassName.add(entryName);
//                        }
//                    } else {
//                        int index = entryName.lastIndexOf("/");
//                        String myPackagePath;
//                        if (index != -1) {
//                            myPackagePath = entryName.substring(0, index);
//                        } else {
//                            myPackagePath = entryName;
//                        }
//                        if (myPackagePath.equals(packagePath)) {
//                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
//                            myClassName.add(entryName);
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            //SystemLog.Log(LogType.systemInfo, e.getMessage(), e);
//        }
//        return myClassName;
//    }


    public static String getLocalPojo(String responsePojoName) {
        return remoteToLocalPojoMap.getOrDefault(responsePojoName, responsePojoName);
    }

    public static String getRemotePojo(String localPojoName) {
        return localToRemotePojoMap.getOrDefault(localPojoName, localPojoName);
    }
}
