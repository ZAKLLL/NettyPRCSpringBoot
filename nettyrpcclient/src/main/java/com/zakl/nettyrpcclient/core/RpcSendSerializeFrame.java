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

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.zakl.nettyrpc.common.handler.NettyRpcSendHandler;
import com.zakl.nettyrpc.common.handler.serializehandler.HessianSendHandler;
import com.zakl.nettyrpc.common.handler.serializehandler.JdkNativeSendHandler;
import com.zakl.nettyrpc.common.handler.serializehandler.KryoSendHandler;
import com.zakl.nettyrpc.common.handler.serializehandler.ProtostuffSendHandler;
import com.zakl.nettyrpc.common.serialize.RpcSerializeFrame;
import com.zakl.nettyrpc.common.serialize.RpcSerializeProtocol;
import io.netty.channel.ChannelPipeline;


/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:RpcSendSerializeFrame.java
 * @description:RpcSendSerializeFrame功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public class RpcSendSerializeFrame implements RpcSerializeFrame {
    private static ClassToInstanceMap<NettyRpcSendHandler> handler = MutableClassToInstanceMap.create();

    static {
        handler.putInstance(JdkNativeSendHandler.class, new JdkNativeSendHandler());
        handler.putInstance(KryoSendHandler.class, new KryoSendHandler());
        handler.putInstance(HessianSendHandler.class, new HessianSendHandler());
        handler.putInstance(ProtostuffSendHandler.class, new ProtostuffSendHandler());
    }

    @Override
    public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {
        switch (protocol) {
            case JDKSERIALIZE: {
                handler.getInstance(JdkNativeSendHandler.class).handle(pipeline);
                break;
            }
            case KRYOSERIALIZE: {
                handler.getInstance(KryoSendHandler.class).handle(pipeline);
                break;
            }
            case HESSIANSERIALIZE: {
                handler.getInstance(HessianSendHandler.class).handle(pipeline);
                break;
            }
            case PROTOSTUFFSERIALIZE: {
                handler.getInstance(ProtostuffSendHandler.class).handle(pipeline);
                break;
            }
            default: {
                break;
            }
        }
    }
}

