package com.zakl.nettyrpcclient.core;

import com.zakl.nettyrpc.common.serialize.RpcSerializeProtocol;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ZhangJiaKui
 * @classname NettyClientStarter
 * @description 启动Netty服务
 * @date 11/26/2020 2:09 PM
 */

public class NettyClientStarter {
    private static ConcurrentHashMap<String, Boolean> connectStatusMap = new ConcurrentHashMap<>();
    private static Lock lock = new ReentrantLock();


    //启动Client客户端
    public static void connectedToServer(String ip, int port, RpcSerializeProtocol protocol) {
        String remoteAddr = ip + ":" + port;
        if (!connectStatusMap.getOrDefault(remoteAddr, false)) {
            lock.lock();
            if (!connectStatusMap.getOrDefault(remoteAddr, false)) {
                RpcServerLoader.getInstance(ip + ":" + port).load(ip, port, protocol);
                connectStatusMap.put(remoteAddr, true);
                lock.unlock();
            }
        }
    }

    public static ConcurrentHashMap<String, Boolean> getConnectStatusMap() {
        return connectStatusMap;
    }
}
