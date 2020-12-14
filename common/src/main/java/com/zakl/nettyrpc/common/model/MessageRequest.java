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
package com.zakl.nettyrpc.common.model;

import lombok.Data;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:MessageRequest.java
 * @description:MessageRequest功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
@Data
public class MessageRequest implements Serializable {

    private String messageId;
    private String className;
    private String methodName;
    //String 表示的方法参数Type
    private String[] parameterTypes;

    //String 表示实际传入的参数类型
    private String[] argsTypes;

    //json 字符串
    private String[] parametersVal;

    private boolean invokeMetrics = true;


    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, "typeParameters", "parametersVal");
    }
}

