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

import com.zakl.nettyrpc.common.model.MessageRequest;
import com.zakl.nettyrpc.common.util.JsonUtils;
import com.zakl.nettyrpcserver.filter.Filter;
import com.zakl.nettyrpcserver.filter.ServiceFilterBinder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:MethodProxyAdvisor.java
 * @description:MethodProxyAdvisor功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2017/7/27
 */
//过滤器调用
public class MethodProxyAdvisor implements MethodInterceptor {
    private Map<String, Object> handlerMap;
    private boolean returnNotNull = true;

    public boolean isReturnNotNull() {
        return returnNotNull;
    }

    public void setReturnNotNull(boolean returnNotNull) {
        this.returnNotNull = returnNotNull;
    }

    public MethodProxyAdvisor(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    private final static Map<String, String> unBoxTypeMap;

    static {
        unBoxTypeMap = new HashMap<>();
        unBoxTypeMap.put("int", "java.lang.Integer");
        unBoxTypeMap.put("byte", "java.lang.Byte");
        unBoxTypeMap.put("short", "java.lang.Short");
        unBoxTypeMap.put("long", "java.lang.Long");
        unBoxTypeMap.put("double", "java.lang.Double");
        unBoxTypeMap.put("float", "java.lang.Float");
        unBoxTypeMap.put("bool", "java.lang.Boolean");
    }


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] params = invocation.getArguments();
        if (params.length <= 0) {
            return null;
        }

        MessageRequest request = (MessageRequest) params[0];

        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        String methodName = request.getMethodName();
        String[] parametersInJson = request.getParametersVal();
        String[] parameterTypesInString = request.getParameterTypes();

        boolean existFilter = ServiceFilterBinder.class.isAssignableFrom(serviceBean.getClass());
        ((MethodInvoker) invocation.getThis()).setServiceBean(existFilter ? ((ServiceFilterBinder) serviceBean).getObject() : serviceBean);


        if (existFilter) {
            ServiceFilterBinder processors = (ServiceFilterBinder) serviceBean;
            if (processors.getFilter() != null) {
                Filter filter = processors.getFilter();
                Object[] args = JsonUtils.jsonsToObjects(parametersInJson, parameterTypesInString);
                Class<?>[] parameterTypes = ClassUtils.toClass(args);
                Method method = MethodUtils.getMatchingAccessibleMethod(processors.getObject().getClass(), methodName, parameterTypes);
                if (filter.before(method, processors.getObject(), args)) {
                    Object result = invocation.proceed();
                    filter.after(method, processors.getObject(), args);
                    setReturnNotNull(result != null);
                    return result;
                } else {
                    return null;
                }
            }
        }

        Object result = invocation.proceed();
        setReturnNotNull(result != null);
        return result;
    }
}


