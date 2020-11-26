package com.zakl.nettyrpcclient.core;

import com.zakl.nettyrpc.common.serialize.RpcSerializeProtocol;
import lombok.Data;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ZhangJiaKui
 * @classname NettyClientStarter
 * @description 启动Netty服务
 * @date 11/26/2020 2:09 PM
 */

public class NettyClientStarter {
    private static String remoteIpAddr;
    private static Integer remotePort;
    private static RpcSerializeProtocol protocol;
    private static AtomicBoolean canConnect = new AtomicBoolean(true);
    private static Lock lock = new ReentrantLock();


    //启动Client客户端
    public static void connectedToServer() {
        if (remoteIpAddr == null || protocol == null || remotePort == null) {
            throw new NullPointerException();
        }
        //只进行一次连接操作,防止多次连接资源浪费
        //todo 后期可能更改为服务可连接到不同的rpc服务,满足分布式要求
        if (canConnect.get()) {
            lock.lock();
            if (canConnect.get()) {
                MessageSendExecutor.getInstance().setRpcServerLoader(remoteIpAddr, remotePort, protocol);
                canConnect.set(false);
                lock.unlock();
            }
        }
    }

    public static String getRemoteIpAddr() {
        return remoteIpAddr;
    }

    public static void setRemoteIpAddr(String remoteIpAddr) {
        NettyClientStarter.remoteIpAddr = remoteIpAddr;
    }

    public static Integer getRemotePort() {
        return remotePort;
    }

    public static void setRemotePort(Integer remotePort) {
        NettyClientStarter.remotePort = remotePort;
    }

    public static RpcSerializeProtocol getProtocol() {
        return protocol;
    }

    public static void setProtocol(RpcSerializeProtocol protocol) {
        NettyClientStarter.protocol = protocol;
    }

    public static AtomicBoolean getCanConnect() {
        return canConnect;
    }
}
