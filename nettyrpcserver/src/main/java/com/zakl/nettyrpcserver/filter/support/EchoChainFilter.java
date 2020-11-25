/**
 * Copyright (C) 2018 Newland Group Holding Limited
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
package com.zakl.nettyrpcserver.filter.support;


import com.zakl.nettyrpc.common.model.MessageRequest;
import com.zakl.nettyrpcserver.core.ModuleInvoker;
import com.zakl.nettyrpcserver.filter.ChainFilter;
import org.springframework.stereotype.Component;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:EchoChainFilter.java
 * @description:EchoChainFilter功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2018/1/31
 */
@Component(value = EchoChainFilter.FILTER_NAME)
public class EchoChainFilter implements ChainFilter {
    public final static String FILTER_NAME = "echoChainFilter";

    @Override
    public Object invoke(ModuleInvoker<?> invoker, MessageRequest request) throws Throwable {
        Object o = null;
        try {
            System.out.println("EchoChainFilter##TRACE MESSAGE-ID:" + request.getMessageId());
            o = invoker.invoke(request);
            return o;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        }
    }
}

