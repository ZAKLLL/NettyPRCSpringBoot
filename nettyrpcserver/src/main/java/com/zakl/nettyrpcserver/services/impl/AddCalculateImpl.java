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
package com.zakl.nettyrpcserver.services.impl;


import com.zakl.nettyrpcserver.services.AddCalculate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author tangjie<https: / / github.com / tang-jie>
 * @filename:CalculateImpl.java
 * @description:CalculateImpl功能模块
 * @blogs http://www.cnblogs.com/jietang/
 * @since 2016/10/7
 */
@Service(value = AddCalculate.SERVICE_BEAN_NAME)
public class AddCalculateImpl implements AddCalculate {
    //两数相加
    @Override
    @Cacheable(value = "addCache", key = "targetClass + methodName +#p0+#p1")
    public int add(int a, int b) {

        System.out.println("add方法调用");
        if (a == 100) {
//            return ((AddCalculate) BeanUtils.getBean(AddCalculate.SERVICE_BEAN_NAME)).add2(a, b);
//            return add2(a, b);
            return this.add2(a, b);
        }

        return a + b;
    }

    @Override
    @Cacheable(value = "add2Cache", key = "targetClass + methodName +#p0+#p1")
    public int add2(int a, int b) {
        System.out.println("add2方法调用");
        return a + b;
    }
}

