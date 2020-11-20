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

import com.google.common.reflect.Reflection;
import com.zakl.nettyrpc.common.serialize.RpcSerializeProtocol;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:MessageSendExecutor.java
 * @description:MessageSendExecutor功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public class MessageSendExecutor {
    private static class MessageSendExecutorHolder {
        private static final MessageSendExecutor INSTANCE = new MessageSendExecutor();
    }

    public static MessageSendExecutor getInstance() {
        return MessageSendExecutorHolder.INSTANCE;
    }

    private RpcServerLoader loader = RpcServerLoader.getInstance();

    private MessageSendExecutor() {

    }

    public MessageSendExecutor(String serverAddress, int port, RpcSerializeProtocol serializeProtocol) {
        loader.load(serverAddress, port, serializeProtocol);
    }

    public void setRpcServerLoader(String serverAddress, int port, RpcSerializeProtocol serializeProtocol) {
        loader.load(serverAddress, port, serializeProtocol);
    }

    public void stop() {
        loader.unLoad();
    }

    public <T> T execute(Class<T> rpcInterface, String remoteInterFaceName) {
        return Reflection.newProxy(rpcInterface, new MessageSendProxy<T>(remoteInterFaceName));
    }
}

