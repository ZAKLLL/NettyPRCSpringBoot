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
package com.zakl.nettyrpcclient.spring;

import com.google.common.eventbus.EventBus;
import com.zakl.nettyrpcclient.client.MessageSendExecutor;
import com.zakl.nettyrpcclient.event.ClientStopEvent;
import com.zakl.nettyrpcclient.event.ClientStopEventListener;
import com.zakl.nettyrpcclient.serialize.RpcSerializeProtocol;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:NettyRpcReference.java
 * @description:NettyRpcReference功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
@Component(value = "addc")
public class NettyRpcReference implements FactoryBean, DisposableBean {

    private String remoteinterfaceName = "com.newlandframework.rpc.services.AddCalculate";
//    private String localInterfaceName = "com.zakl.nettyrpcclient.services.AddCalculate";
    private String ipAddr = "127.0.0.1:18887";
    private String protocol = "PROTOSTUFFSERIALIZE";
    private EventBus eventBus = new EventBus();

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getRemoteinterfaceName() {
        return remoteinterfaceName;
    }

    public void setRemoteinterfaceName(String remoteinterfaceName) {
        this.remoteinterfaceName = remoteinterfaceName;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public void destroy() throws Exception {
        eventBus.post(new ClientStopEvent(0));
    }


    @PostConstruct
    public void init() {
        MessageSendExecutor.getInstance().setRpcServerLoader(ipAddr, RpcSerializeProtocol.valueOf(protocol));
        ClientStopEventListener listener = new ClientStopEventListener();
        eventBus.register(listener);
    }


    @Override
    public Object getObject() {
        return MessageSendExecutor.getInstance().execute(getObjectType());
    }

    @Override
    public Class<?> getObjectType() {
        try {
            return this.getClass().getClassLoader().loadClass(remoteinterfaceName);
        } catch (ClassNotFoundException e) {
            System.err.println("spring analyze fail!");
        }
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
