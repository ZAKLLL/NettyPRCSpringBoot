/**
 * Copyright (C) 2016 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zakl.nettyrpcserver.config;

import com.zakl.nettyrpc.common.config.RpcSystemConfig;
import com.zakl.nettyrpc.common.serialize.RpcSerializeProtocol;
import com.zakl.nettyrpcserver.netty.MessageRecvExecutor;
import com.zakl.nettyrpcserver.jmx.HashModuleMetricsVisitor;
import com.zakl.nettyrpcserver.jmx.ModuleMetricsHandler;
import com.zakl.nettyrpcserver.jmx.ThreadPoolMonitorProvider;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;

/**
 * @author zakl
 */
//因为需要ServiceConfig 将Service注入到handlerMap中,所有使用DependsOn强制依赖加载
@Component(value = NettyServerConfig.REGISTRY_BEAN_NAME)
@DependsOn(ServiceConfig.SERVICE_CONFIG_BEAN_NAME)
public class NettyServerConfig implements DisposableBean {
    public final static String REGISTRY_BEAN_NAME = "nettyRpcRegistry";
    @Value("${netty.rpc.server.ipAddr}")
    private String ipAddr;

    @Value("${netty.rpc.server.port}")
    private int port;

    @Value("${netty.rpc.server.protocol}")
    private String protocol;

    @Value("${netty.rpc.server.echoApiPort}")
    private String echoApiPort;

    @Value("${netty.rpc.server.jmx.enable}")
    private boolean enableJmxSupport;

    @Value("${netty.rpc.server.jmx.port}")
    private int jmxPort;

    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();


    @Override
    public void destroy() {
        MessageRecvExecutor.getInstance().stop();

        if (RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT) {
            ModuleMetricsHandler handler = ModuleMetricsHandler.getInstance();
            handler.stop();
        }
    }

    public void startNettyServer() {

        MessageRecvExecutor ref = MessageRecvExecutor.getInstance();
        ref.setServerAddress(ipAddr, port);
        ref.setEchoApiPort(Integer.parseInt(echoApiPort));
        ref.setSerializeProtocol(Enum.valueOf(RpcSerializeProtocol.class, protocol));
        ref.setEnableJmxSupport(enableJmxSupport);

//        if (RpcSystemConfig.isMonitorServerSupport()) {
//            context.register(ThreadPoolMonitorProvider.class);
//            context.refresh();
//        }

        Executors.newSingleThreadExecutor().execute(ref::start);

        if (enableJmxSupport) {
            HashModuleMetricsVisitor visitor = HashModuleMetricsVisitor.getInstance();
            visitor.signal();
            ModuleMetricsHandler.setModuleMetricsJmxPort(jmxPort);
            ModuleMetricsHandler.getInstance().start();
        }
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getEchoApiPort() {
        return echoApiPort;
    }

    public void setEchoApiPort(String echoApiPort) {
        this.echoApiPort = echoApiPort;
    }


}


