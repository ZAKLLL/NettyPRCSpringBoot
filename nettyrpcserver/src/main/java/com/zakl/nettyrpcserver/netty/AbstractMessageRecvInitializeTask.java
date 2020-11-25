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

import com.alibaba.fastjson.JSON;
import com.zakl.nettyrpc.common.config.RpcSystemConfig;
import com.zakl.nettyrpc.common.model.MessageRequest;
import com.zakl.nettyrpc.common.model.MessageResponse;
import com.zakl.nettyrpc.common.util.BeanUtils;
import com.zakl.nettyrpcserver.core.Modular;
import com.zakl.nettyrpcserver.core.ModuleInvoker;
import com.zakl.nettyrpcserver.core.ModuleProvider;
import com.zakl.nettyrpcserver.filter.ModuleFilterChainWrapper;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:AbstractMessageRecvInitializeTask.java
 * @description:AbstractMessageRecvInitializeTask功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/13
 */
public abstract class AbstractMessageRecvInitializeTask implements Callable<Boolean> {
    protected MessageRequest request;
    protected MessageResponse response;
    protected Map<String, Object> handlerMap;
    protected static final String METHOD_MAPPED_NAME = "invoke";
    protected boolean returnNotNull = true;
    protected long invokeTimespan;
//    protected Modular modular = BeanFactoryUtils.getBean(ModuleFilterChainWrapper.FILTER_CHAIN_WRAPPER_BEAN_NAME);
    protected Modular modular = BeanUtils.getBean(ModuleFilterChainWrapper.FILTER_CHAIN_WRAPPER_BEAN_NAME);

    public AbstractMessageRecvInitializeTask(MessageRequest request, MessageResponse response, Map<String, Object> handlerMap) {
        this.request = request;
        this.response = response;
        this.handlerMap = handlerMap;
    }

    @Override
    public Boolean call() {
        try {
            acquire();
            response.setMessageId(request.getMessageId());
            injectInvoke();
            Object result = reflect(request);
            boolean isInvokeSucc = (!returnNotNull || result != null);
            if (isInvokeSucc) {
                //todo 添加json返回
                response.setResponseType(result.getClass().getCanonicalName());
                response.setJsonResult(JSON.toJSON(result).toString());
                response.setError("");
                response.setReturnNotNull(returnNotNull);
                injectSuccInvoke(invokeTimespan);
            } else {
                System.err.println(RpcSystemConfig.FILTER_RESPONSE_MSG);
                response.setJsonResult(null);
                response.setResponseType(null);
                response.setError(RpcSystemConfig.FILTER_RESPONSE_MSG);
                injectFilterInvoke();
            }
            return Boolean.TRUE;
        } catch (Throwable t) {
            response.setError(getStackTrace(t));
            t.printStackTrace();
            System.err.printf("RPC Server invoke error!\n");
            injectFailInvoke(t);
            return Boolean.FALSE;
        } finally {
            release();
        }
    }

    private Object invoke(MethodInvoker mi, MessageRequest request) throws Throwable {
        if (modular != null) {
            ModuleProvider provider = modular.invoke(new ModuleInvoker() {

                @Override
                public Class getInterface() {
                    return mi.getClass().getInterfaces()[0];
                }

                @Override
                public Object invoke(MessageRequest request) throws Throwable {
                    return mi.invoke(request);
                }

                @Override
                public void destroy() {

                }
            }, request);
            return provider.getInvoker().invoke(request);
        } else {
            return mi.invoke(request);
        }
    }

    private Object reflect(MessageRequest request) throws Throwable {
        ProxyFactory weaver = new ProxyFactory(new MethodInvoker());
        NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
        advisor.setMappedName(METHOD_MAPPED_NAME);
        //设置AOP 方法
        advisor.setAdvice(new MethodProxyAdvisor(handlerMap));
        weaver.addAdvisor(advisor);
        MethodInvoker mi = (MethodInvoker) weaver.getProxy();
        Object obj = invoke(mi, request);
        invokeTimespan = mi.getInvokeTimespan();
        setReturnNotNull(((MethodProxyAdvisor) advisor.getAdvice()).isReturnNotNull());
        return obj;
    }

    public String getStackTrace(Throwable ex) {
        StringWriter buf = new StringWriter();
        ex.printStackTrace(new PrintWriter(buf));

        return buf.toString();
    }

    public boolean isReturnNotNull() {
        return returnNotNull;
    }

    public void setReturnNotNull(boolean returnNotNull) {
        this.returnNotNull = returnNotNull;
    }

    public MessageResponse getResponse() {
        return response;
    }

    public MessageRequest getRequest() {
        return request;
    }

    public void setRequest(MessageRequest request) {
        this.request = request;
    }

    protected abstract void injectInvoke() throws NoSuchMethodException;

    protected abstract void injectSuccInvoke(long invokeTimespan);

    protected abstract void injectFailInvoke(Throwable error);

    protected abstract void injectFilterInvoke();

    protected abstract void acquire();

    protected abstract void release();
}

