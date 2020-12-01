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
package com.zakl.nettyrpcclient.async;


import com.zakl.nettyrpc.common.config.RpcSystemConfig;
import com.zakl.nettyrpc.common.exception.AsyncCallException;
import com.zakl.nettyrpcclient.parallel.RpcThreadPool;
import com.zakl.nettyrpcclient.pojo.CostTime;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:AsyncInvoker.java
 * @description:AsyncInvoker功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/3/22
 */
public class AsyncInvoker {
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) RpcThreadPool.getExecutor(RpcSystemConfig.SYSTEM_PROPERTY_THREAD_POOL_THREAD_NUMS, RpcSystemConfig.SYSTEM_PROPERTY_THREAD_POOL_QUEUE_NUMS);

    private <R> AsyncFuture<R> submit(Callable<R> task) {
        AsyncFuture<R> future = new AsyncFuture<>(task);
        executor.submit(future);
        return future;
    }

    private <R> R intercept(final AsyncCallback<R> callback, Class<?> returnClass) {
        if (!Modifier.isPublic(returnClass.getModifiers())) {
            return callback.call();
        } else if (Modifier.isFinal(returnClass.getModifiers())) {
            return callback.call();
        } else if (Void.TYPE.isAssignableFrom(returnClass)) {
            return callback.call();
        } else if (returnClass.isPrimitive() || returnClass.isArray()) {
            return callback.call();
        } else if (returnClass == Object.class) {
            return callback.call();
        } else return null;
    }

    public <R> R submit(final AsyncCallback<R> callback, Class<?> returnClass) {
        R ret = intercept(callback, returnClass);
        if (ret != null) {
            return ret;
        }
        Future<R> future = submit(callback::call);
        AsyncCallResult result = new AsyncCallResult(returnClass, future, RpcSystemConfig.SYSTEM_PROPERTY_ASYNC_MESSAGE_CALLBACK_TIMEOUT);
        return (R) result.getResult();
    }
}

