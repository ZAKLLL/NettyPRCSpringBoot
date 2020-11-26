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

import com.zakl.nettyrpc.common.model.MessageRequest;
import com.zakl.nettyrpc.common.model.MessageResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:MessageRecvHandler.java
 * @description:MessageRecvHandler功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
@Slf4j
public class MessageRecvHandler extends ChannelInboundHandlerAdapter {

    private final Map<String, Object> handlerMap;

    public MessageRecvHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("【" + ctx.channel().id() + "】" + new SimpleDateFormat("yyyy/MM/dd HH/mm/ss").format(new Date()) + "==>>>"
                + "channelActive");
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageRequest request = (MessageRequest) msg;
        log.info("ctx->" + ctx.channel().id().asShortText() + "-----request->" + request);
        MessageResponse response = new MessageResponse();
        RecvInitializeTaskFacade facade = new RecvInitializeTaskFacade(request, response, handlerMap);
        Callable<Boolean> recvTask = facade.getTask();
        MessageRecvExecutor.submit(recvTask, ctx, request, response);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("【" + ctx.channel().id() + "】" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "==>>>"
                + "channelInactive");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

