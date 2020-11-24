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
package com.zakl.nettyrpcserver.netty;

import com.google.common.util.concurrent.*;
import com.zakl.nettyrpc.common.config.RpcSystemConfig;
import com.zakl.nettyrpc.common.model.MessageKeyVal;
import com.zakl.nettyrpc.common.model.MessageRequest;
import com.zakl.nettyrpc.common.model.MessageResponse;
import com.zakl.nettyrpc.common.parallel.NamedThreadFactory;
import com.zakl.nettyrpc.common.serialize.RpcSerializeProtocol;
import com.zakl.nettyrpcserver.compiler.AccessAdaptiveProvider;
import com.zakl.nettyrpcserver.core.AbilityDetailProvider;
import com.zakl.nettyrpcserver.netty.jmx.ModuleMetricsHandler;
import com.zakl.nettyrpcserver.netty.resolver.ApiEchoResolver;
import com.zakl.nettyrpcserver.parallel.RpcThreadPool;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:MessageRecvExecutor.java
 * @description:MessageRecvExecutor功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
public class MessageRecvExecutor implements ApplicationContextAware {

    private String serverAddress;
    private int serverPort;
    private int echoApiPort;
    private RpcSerializeProtocol serializeProtocol = RpcSerializeProtocol.JDKSERIALIZE;
    private static final String DELIMITER = RpcSystemConfig.DELIMITER;
    private static final int PARALLEL = RpcSystemConfig.SYSTEM_PROPERTY_PARALLEL * 2;
    private static int threadNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS;
    private static int queueNums = RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS;
    private static volatile ListeningExecutorService threadPoolExecutor;
    private Map<String, Object> handlerMap = new ConcurrentHashMap<>();
    private int numberOfEchoThreadsPool = 1;

    ThreadFactory threadRpcFactory = new NamedThreadFactory("NettyRPC ThreadFactory");
    EventLoopGroup boss = new NioEventLoopGroup();
    EventLoopGroup worker = new NioEventLoopGroup(PARALLEL, threadRpcFactory, SelectorProvider.provider());

    private MessageRecvExecutor() {
        handlerMap.clear();
        register();
    }

    private static class MessageRecvExecutorHolder {
        static final MessageRecvExecutor INSTANCE = new MessageRecvExecutor();
    }

    public static MessageRecvExecutor getInstance() {
        return MessageRecvExecutorHolder.INSTANCE;
    }

    public static void submit(Callable<Boolean> recvTask, final ChannelHandlerContext ctx, final MessageRequest request, final MessageResponse response) {
        if (threadPoolExecutor == null) {
            synchronized (MessageRecvExecutor.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = MoreExecutors.listeningDecorator((ThreadPoolExecutor) (RpcSystemConfig.isMonitorServerSupport() ? RpcThreadPool.getExecutorWithJmx(threadNums, queueNums) : RpcThreadPool.getExecutor(threadNums, queueNums)));
                }
            }
        }

        ListenableFuture<Boolean> listenableFuture = threadPoolExecutor.submit(recvTask);
        Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                ctx.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture -> System.out.println("RPC Server Send message-id respone:" + request.getMessageId()));
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, threadPoolExecutor);
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        try {
            MessageKeyVal keyVal = (MessageKeyVal) ctx.getBean(Class.forName("com.zakl.nettyrpc.common.model.MessageKeyVal"));
            Map<String, Object> rpcServiceObject = keyVal.getMessageKeyVal();

            Set s = rpcServiceObject.entrySet();
            Iterator<Map.Entry<String, Object>> it = s.iterator();
            Map.Entry<String, Object> entry;

            while (it.hasNext()) {
                entry = it.next();
                handlerMap.put(entry.getKey(), entry.getValue());
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MessageRecvExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new MessageRecvChannelInitializer(handlerMap).buildRpcSerializeProtocol(serializeProtocol))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);


            ChannelFuture future;
            future = bootstrap.bind(serverAddress, serverPort).sync();

            if (future.isSuccess()) {
                ExecutorService executor = Executors.newFixedThreadPool(numberOfEchoThreadsPool);
                future.addListener((ChannelFutureListener) future1 -> {
                    ExecutorCompletionService<Boolean> completionService = new ExecutorCompletionService<>(executor);
                    completionService.submit(new ApiEchoResolver(serverAddress, echoApiPort));
                    System.out.printf("Netty RPC Server start success!\nip:%s\nport:%d\nprotocol:%s\nstart-time:%s\njmx-invoke-metrics:%s\n\n", serverAddress, serverPort, serializeProtocol, ModuleMetricsHandler.getStartTime(), (RpcSystemConfig.SYSTEM_PROPERTY_JMX_METRICS_SUPPORT ? "open" : "close"));
                });
                future.channel().closeFuture().sync().addListener(i -> executor.shutdown());
            }
        } catch (InterruptedException e) {
            System.out.println("Netty RPC Server start fail!");
            e.printStackTrace();
        }
    }

    public void stop() {
        worker.shutdownGracefully();
        boss.shutdownGracefully();
    }

    private void register() {
        handlerMap.put(RpcSystemConfig.RPC_COMPILER_SPI_ATTR, new AccessAdaptiveProvider());
        handlerMap.put(RpcSystemConfig.RPC_ABILITY_DETAIL_SPI_ATTR, new AbilityDetailProvider());
    }

    public Map<String, Object> getHandlerMap() {
        return handlerMap;
    }

    public void setHandlerMap(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.serverPort = port;
    }

    public RpcSerializeProtocol getSerializeProtocol() {
        return serializeProtocol;
    }

    public void setSerializeProtocol(RpcSerializeProtocol serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }

    public int getEchoApiPort() {
        return echoApiPort;
    }

    public void setEchoApiPort(int echoApiPort) {
        this.echoApiPort = echoApiPort;
    }
}
