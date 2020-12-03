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
package com.zakl.nettyrpc.common.config;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:RpcSystemConfig.java
 * @description: 系统默认配置文件
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public class RpcSystemConfig {
    public static String SYSTEM_PROPERTY_THREAD_POOL_REJECTED_POLICY_ATTR = "nettyrpc.parallel.rejected.policy";
    public static String SYSTEM_PROPERTY_THREAD_POOL_QUEUE_NAME_ATTR = "nettyrpc.parallel.queue";
    public static long SYSTEM_PROPERTY_MESSAGE_CALLBACK_TIMEOUT = 30 * 1000L;
    public static long SYSTEM_PROPERTY_ASYNC_MESSAGE_CALLBACK_TIMEOUT = 60 * 1000L;
    public static int SYSTEM_PROPERTY_THREAD_POOL_THREAD_NUMS = 16;
    public static int SYSTEM_PROPERTY_THREAD_POOL_QUEUE_NUMS = -1;
    public static int SYSTEM_PROPERTY_CLIENT_RECONNECT_DELAY = 10;
    public static int SYSTEM_PROPERTY_PARALLEL = Math.max(2, Runtime.getRuntime().availableProcessors());

    //哈希分片加锁算法中，哈希分片的个数
    public static int SYSTEM_PROPERTY_JMX_METRICS_HASH_NUMS = 8;

    //是否公平锁
    public static int SYSTEM_PROPERTY_JMX_METRICS_LOCK_FAIR = 0;

    //默认开启哈希分片加锁算法
    public static boolean SYSTEM_PROPERTY_JMX_METRICS_HASH_SUPPORT = true;

    //默认开启jmx监控
    public static boolean SYSTEM_PROPERTY_JMX_METRICS_SUPPORT = true;


    public static final String RPC_COMPILER_SPI_ATTR = "com.zakl.nettyrpcserver.compiler.AccessAdaptive";
    public static final String RPC_ABILITY_DETAIL_SPI_ATTR = "com.zakl.nettyrpcserver.jmx.webmetrics.AbilityDetail";
    public static final String FILTER_RESPONSE_MSG = "Illegal request,NettyRPC server refused to respond!";
    public static final String TIMEOUT_RESPONSE_MSG = "Timeout request,NettyRPC server request timeout!";
    public static final int SERIALIZE_POOL_MAX_TOTAL = 500;
    public static final int SERIALIZE_POOL_MIN_IDLE = 10;
    public static final int SERIALIZE_POOL_MAX_WAIT_MILLIS = 5000;
    public static final int SERIALIZE_POOL_MIN_EVICTABLE_IDLE_TIME_MILLIS = 600000;

}

