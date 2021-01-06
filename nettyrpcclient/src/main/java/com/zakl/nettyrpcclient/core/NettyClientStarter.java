package com.zakl.nettyrpcclient.core;

import com.zakl.nettyrpc.common.serialize.RpcSerializeProtocol;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * @author ZhangJiaKui
 * @classname NettyClientStarter
 * @description 启动Netty服务
 * @date 11/26/2020 2:09 PM
 */

public class NettyClientStarter {
    private final static ConcurrentHashMap<String, Boolean> connectStatusMap = new ConcurrentHashMap<>();
    private final static Lock lock = new ReentrantLock();


    //启动Client客户端
    public static Future<Boolean> connectedToServer(String ip, int port, RpcSerializeProtocol protocol) {
        String remoteAddr = ip + ":" + port;
        Future<Boolean> ret = null;
        if (!connectStatusMap.getOrDefault(remoteAddr, false)) {
            lock.lock();
            try {
                if (!connectStatusMap.getOrDefault(remoteAddr, false)) {
                    ret = RpcServerLoader.getInstance(ip + ":" + port).load(ip, port, protocol);
                    connectStatusMap.put(remoteAddr, true);
                }
            } finally {
                lock.unlock();
            }
        }
        return ret;
    }

    public static ConcurrentHashMap<String, Boolean> getConnectStatusMap() {
        return connectStatusMap;
    }
}
