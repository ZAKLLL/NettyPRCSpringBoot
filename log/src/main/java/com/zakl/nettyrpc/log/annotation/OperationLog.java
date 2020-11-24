package com.zakl.nettyrpc.log.annotation;

import com.zakl.nettyrpc.log.enums.OperationType;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /**
     * 方法描述,可使用占位符获取参数:{{name}}
     */
    String detail() default "";

    /**
     * 操作类型(enum):主要是select,insert,update,delete
     */
    OperationType operationType() default OperationType.UNKNOWN;
}
