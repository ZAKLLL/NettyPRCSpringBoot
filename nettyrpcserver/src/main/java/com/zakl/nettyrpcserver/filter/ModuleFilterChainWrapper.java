/**
 * Copyright (C) 2018 Newland Group Holding Limited
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
package com.zakl.nettyrpcserver.filter;

import com.zakl.nettyrpc.common.model.MessageRequest;
import com.zakl.nettyrpcserver.core.DefaultModular;
import com.zakl.nettyrpcserver.core.Modular;
import com.zakl.nettyrpcserver.core.ModuleInvoker;
import com.zakl.nettyrpcserver.core.ModuleProvider;
import com.zakl.nettyrpcserver.filter.support.ClassLoaderChainFilter;
import com.zakl.nettyrpcserver.filter.support.EchoChainFilter;
import com.zakl.nettyrpcserver.listener.ModuleListenerChainWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:ModuleFilterChainWrapper.java
 * @description:ModuleFilterChainWrapper功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2018/2/2
 */
@Component(value = ModuleFilterChainWrapper.FILTER_CHAIN_WRAPPER_BEAN_NAME)
public class ModuleFilterChainWrapper implements Modular {
    public final static String FILTER_CHAIN_WRAPPER_BEAN_NAME = "ModuleFilterChainWrapper";

    @Resource(name = ModuleListenerChainWrapper.CHAIN_WRAPPER_BEAN_NAME)
    private Modular moduleListenerChainWrapper;

    private List<ChainFilter> filters;


    @Resource(name = ClassLoaderChainFilter.FILTER_NAME)
    private ChainFilter classLoaderChanFilter;

    @Resource(name = EchoChainFilter.FILTER_NAME)
    private ChainFilter echoChainFilter;


    @PostConstruct
    public void init() {
        filters = new ArrayList<>();
        filters.add(classLoaderChanFilter);
        filters.add(echoChainFilter);
    }


    @Override
    public <T> ModuleProvider<T> invoke(ModuleInvoker<T> invoker, MessageRequest request) {
        return moduleListenerChainWrapper.invoke(buildChain(invoker), request);
    }

    private <T> ModuleInvoker<T> buildChain(ModuleInvoker<T> invoker) {
        ModuleInvoker last = invoker;

        if (filters.size() > 0) {
            for (int i = filters.size() - 1; i >= 0; i--) {
                ChainFilter filter = filters.get(i);
                ModuleInvoker<T> next = last;
                last = new ModuleInvoker<T>() {
                    @Override
                    public Object invoke(MessageRequest request) throws Throwable {
                        return filter.invoke(next, request);
                    }

                    @Override
                    public Class<T> getInterface() {
                        return invoker.getInterface();
                    }

                    @Override
                    public String toString() {
                        return invoker.toString();
                    }

                    @Override
                    public void destroy() {
                        invoker.destroy();
                    }
                };
            }
        }
        return last;
    }

    public List<ChainFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<ChainFilter> filters) {
        this.filters = filters;
    }
}

