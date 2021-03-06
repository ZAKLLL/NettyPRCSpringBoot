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

import com.google.common.util.concurrent.*;
import com.zakl.nettyrpc.common.config.RpcSystemConfig;
import com.zakl.nettyrpcclient.core.sendtask.MessageSendInitializeTask;
import com.zakl.nettyrpcclient.handler.MessageSendHandler;
import com.zakl.nettyrpc.common.serialize.RpcSerializeProtocol;
import com.zakl.nettyrpcclient.parallel.RpcThreadPool;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:RpcServerLoader.java
 * @description:RpcServerLoader功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
@Slf4j
public class RpcServerLoader {

    private static final int PARALLEL = RpcSystemConfig.SYSTEM_PROPERTY_PARALLEL * 2;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(PARALLEL);
    private static int threadNums = RpcSystemConfig.SYSTEM_PROPERTY_THREAD_POOL_THREAD_NUMS;
    private static int queueNums = RpcSystemConfig.SYSTEM_PROPERTY_THREAD_POOL_QUEUE_NUMS;
    private static ListeningExecutorService threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor) RpcThreadPool.getExecutor(threadNums, queueNums));
    private MessageSendHandler messageSendHandler = null;
    private Lock lock = new ReentrantLock();
    private Condition connectStatus = lock.newCondition();
    private Condition handlerStatus = lock.newCondition();
    private MessageSendInitializeTask messageSendInitializeTask;
    private static ConcurrentHashMap<String, RpcServerLoader> addressRpcServerLoaderMap = new ConcurrentHashMap<>();

    private RpcServerLoader() {
    }


    public static RpcServerLoader getInstance(String address) {
        if (!addressRpcServerLoaderMap.containsKey(address)) {
            addressRpcServerLoaderMap.put(address, new RpcServerLoader());
        }
        return addressRpcServerLoaderMap.get(address);
    }

    public static void removeRpcServerLoader(String address) {
        addressRpcServerLoaderMap.remove(address);
    }


    public void load(String host, int port, RpcSerializeProtocol serializeProtocol) {
        if (StringUtils.isEmpty(host) || port <= 0) {
            return;
        }
        messageSendInitializeTask = new MessageSendInitializeTask(eventLoopGroup, host, port, serializeProtocol);
        //不为空的情况,说明是重连,不用再new 一次,浪费资源
        ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(messageSendInitializeTask);

        Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                try {
                    lock.lock();
                    if (messageSendHandler == null) {
                        //防止重连过久,等待60秒即可
                        boolean await = handlerStatus.await(60, TimeUnit.SECONDS);
                        if (!await) {
                            log.info(String.format("\n Filed to connected to NettyRPCServer!\nip:%s\nport:%d\nprotocol:%s\n\n", host, port, serializeProtocol));
                            return;
                        }
                    }
                    if (result.equals(Boolean.TRUE) && messageSendHandler != null) {
                        connectStatus.signalAll();
                        System.out.printf("Netty RPC Client start success!\nip:%s\nport:%d\nprotocol:%s\n\n", host, port, serializeProtocol);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(RpcServerLoader.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    lock.unlock();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, threadPoolExecutor);
    }

    public void setMessageSendHandler(MessageSendHandler messageInHandler) {
        try {
            lock.lock();
            this.messageSendHandler = messageInHandler;
            handlerStatus.signal();
        } finally {
            lock.unlock();
        }
    }

    public void removeMessageSendHandler() {
        this.messageSendHandler = null;
    }

    public MessageSendHandler getMessageSendHandler() throws InterruptedException {
        try {
            lock.lock();
            if (messageSendHandler == null) {
                connectStatus.await();
            }
            return messageSendHandler;
        } finally {
            lock.unlock();
        }
    }

    public void unLoad() {
        messageSendHandler.close();
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }

    public MessageSendInitializeTask getMessageSendInitializeTask() {
        return messageSendInitializeTask;
    }
}
