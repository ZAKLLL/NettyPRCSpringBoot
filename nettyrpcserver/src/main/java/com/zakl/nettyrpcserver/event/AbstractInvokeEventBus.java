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
package com.zakl.nettyrpcserver.event;


import com.zakl.nettyrpcserver.netty.jmx.ModuleMetricsHandler;

import javax.management.Notification;

/**
 * @author tangjie<https://github.com/tang-jie>
 * @filename:AbstractInvokeEventBus.java
 * @description:AbstractInvokeEventBus功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/10/12
 */
public abstract class AbstractInvokeEventBus {
    public enum ModuleEvent {
        INVOKE_EVENT,
        INVOKE_SUCC_EVENT,
        INVOKE_TIMESPAN_EVENT,
        INVOKE_MAX_TIMESPAN_EVENT,
        INVOKE_MIN_TIMESPAN_EVENT,
        INVOKE_FILTER_EVENT,
        INVOKE_FAIL_EVENT,
        INVOKE_FAIL_STACKTRACE_EVENT
    }

    protected String moduleName;
    protected String methodName;
    protected ModuleMetricsHandler handler;

    public AbstractInvokeEventBus() {

    }

    public AbstractInvokeEventBus(String moduleName, String methodName) {
        this.moduleName = moduleName;
        this.methodName = methodName;
    }

    public abstract Notification buildNotification(Object oldValue, Object newValue);

    public void notify(Object oldValue, Object newValue) {
        Notification notification = buildNotification(oldValue, newValue);
        handler.sendNotification(notification);
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public ModuleMetricsHandler getHandler() {
        return handler;
    }

    public void setHandler(ModuleMetricsHandler handler) {
        this.handler = handler;
    }
}

