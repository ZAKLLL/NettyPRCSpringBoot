/**
 * Copyright (C) 2017 Newland Group Holding Limited
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
package com.zakl.nettyrpcserver.netty;

import com.zakl.nettyrpc.common.model.MessageRequest;
import com.zakl.nettyrpc.common.model.MessageResponse;

import java.util.Map;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:MessageRecvInitializeTaskAdapter.java
 * @description:MessageRecvInitializeTaskAdapter功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/13
 */
public class MessageRecvInitializeTaskAdapter extends AbstractMessageRecvInitializeTask {
    public MessageRecvInitializeTaskAdapter(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
        super(request, response, handlerMap);
    }

    @Override
    protected void injectInvoke() {

    }

    @Override
    protected void injectSuccInvoke(long invokeTimespan) {

    }

    @Override
    protected void injectFailInvoke(Throwable error) {

    }

    @Override
    protected void injectFilterInvoke() {

    }

    @Override
    protected void acquire() {

    }

    @Override
    protected void release() {

    }
}

