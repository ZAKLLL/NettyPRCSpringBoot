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
package com.zakl.nettyrpcserver.listener;


import com.zakl.nettyrpc.common.model.MessageRequest;
import com.zakl.nettyrpcserver.core.DefaultModular;
import com.zakl.nettyrpcserver.core.Modular;
import com.zakl.nettyrpcserver.core.ModuleInvoker;
import com.zakl.nettyrpcserver.core.ModuleProvider;
import com.zakl.nettyrpcserver.filter.ChainFilter;
import com.zakl.nettyrpcserver.filter.support.ClassLoaderChainFilter;
import com.zakl.nettyrpcserver.filter.support.EchoChainFilter;
import com.zakl.nettyrpcserver.listener.support.ModuleListenerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Commit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:ModuleListenerChainWrapper.java
 * @description:ModuleListenerChainWrapper功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2018/2/2
 */
@Component(value = ModuleListenerChainWrapper.CHAIN_WRAPPER_BEAN_NAME)
public class ModuleListenerChainWrapper implements Modular {
    public final static String CHAIN_WRAPPER_BEAN_NAME = "moduleListenerChainWrapper";
    private Modular modular;
    private List<ModuleListener> listeners;


    @Resource(name = ModuleListenerAdapter.LISTENER_ADAPTER_BEAN_NAME)
    private ModuleListener moduleListenerAdapter;

    @PostConstruct
    public void init() {
        modular = new DefaultModular();
        listeners = new ArrayList<>();
        listeners.add(moduleListenerAdapter);
    }



    @Override
    public <T> ModuleProvider<T> invoke(ModuleInvoker<T> invoker, MessageRequest request) {
        return new ModuleProviderWrapper(modular.invoke(invoker, request), Collections.unmodifiableList(listeners), request);
    }

    public List<ModuleListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<ModuleListener> listeners) {
        this.listeners = listeners;
    }
}
