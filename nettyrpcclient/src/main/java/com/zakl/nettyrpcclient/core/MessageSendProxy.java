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
package com.zakl.nettyrpcclient.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.annotations.Beta;
import com.google.common.reflect.AbstractInvocationHandler;
import com.zakl.nettyrpc.common.serialize.RpcSerializeProtocol;
import com.zakl.nettyrpcclient.config.ServiceAndPojoConfig;
import com.zakl.nettyrpcclient.core.sendtask.MessageSendInitializeTask;
import com.zakl.nettyrpcclient.handler.MessageSendHandler;
import com.zakl.nettyrpc.common.model.MessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:MessageSendProxy.java
 * @description:MessageSendProxy功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
@Slf4j
public class MessageSendProxy extends AbstractInvocationHandler {
    private String remoteInterFaceName;

    //由配置文件注入
    private static NamingService namingService;

    public MessageSendProxy(String remoteInterFaceName) {
        this.remoteInterFaceName = remoteInterFaceName;
    }

    private MessageSendProxy() {

    }

    @Override
    public Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        MessageRequest request = constructMessageRequest(method, args);

        //先从nacos获取配置文件,然后在本地获取连接,进行通信
        Instance serverInstance = namingService.selectOneHealthyInstance("nettyrpcserver");
        if (serverInstance==null){
            log.info("no available server instance");
            throw new RuntimeException("no available server instance");
        }
        String rpcServerLoaderKey = serverInstance.getIp() + ":" + serverInstance.getPort();
        RpcServerLoader loader = RpcServerLoader.getInstance(rpcServerLoaderKey);
        MessageSendInitializeTask msgSendTask = loader.getMessageSendInitializeTask();
        if (msgSendTask == null || !msgSendTask.getConnected().get()) {
            log.info("Not connected NettyRPCServer yet");
            log.info("begin to connect to server");
            String protocol = serverInstance.getMetadata().get("protocol");
            Future<Boolean> future = NettyClientStarter.connectedToServer(serverInstance.getIp(), serverInstance.getPort(), RpcSerializeProtocol.valueOf(protocol));
            if (!future.get()) {
                throw new RuntimeException("connect to Netty Server error");
            }
        }
        MessageSendHandler handler = loader.getMessageSendHandler();
        MessageCallBack callBack = handler.sendRequest(request);
        return callBack.start();
    }

    public MessageRequest constructMessageRequest(Method method, Object[] args) {
        MessageRequest request = new MessageRequest();
        request.setMessageId(UUID.randomUUID().toString());
        request.setClassName(StringUtils.isEmpty(remoteInterFaceName) ? method.getDeclaringClass().getName() : remoteInterFaceName);
        request.setMethodName(method.getName());
        //将参数类型以全限定名的String传入
        String[] parameterTypes = new String[method.getParameterTypes().length];
        String[] argsTypes = new String[args.length];
        String[] parametersValInJson = new String[args.length];
        request.setParameterTypes(parameterTypes);
        request.setArgsTypes(argsTypes);
        request.setParametersVal(parametersValInJson);

        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = ServiceAndPojoConfig.getRemotePojo(method.getParameterTypes()[i].getCanonicalName());
            String genericTypeName = args[i].getClass().getGenericSuperclass().getTypeName();
            //匿名内部类的时候canonicalName为null,说明参数带泛型
            String canonicalName = args[i].getClass().getCanonicalName();
            String typeName = canonicalName == null ? genericTypeName : canonicalName;
            argsTypes[i] = ServiceAndPojoConfig.getRemotePojo(typeName);
            parametersValInJson[i] = JSONObject.toJSONString(args[i]);
        }
        return request;
    }


    public static void setNamingService(NamingService namingService) {
        MessageSendProxy.namingService = namingService;
    }
}

