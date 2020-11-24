package com.zakl.nettyrpc.log.constant;

/**
 * MDC中存储的字段名常量
 *
 * @author thz
 * @since 2020 /4/27
 */
public class LogItemNames {

    /**
     * 日志类型
     */
    public static final String TYPE = "type";

    /**
     * 日志类型
     */
    public interface LogType {
        String GLOBAL = "global";

        String BUSINESS = "business";

        String ERROR = "error";
    }

    /**
     * 日志级别
     */
    public static final String LEVEL = "level";

    /**
     * 日志内容
     */
    public static final String MESSAGE = "text ";

    /**
     * 调用的类名
     */
    public static final String CLASS = "class";

    /**
     * 调用的类名方法名
     */
    public static final String METHOD = "method";

    /**
     * 操作类型,主要是select,insert,update,delete
     */
    public static final String OPERATIONTYPE = "operationtype";

    /**
     * 打印日志的行数
     */
    public static final String LINENUMBER = "linenumber";

    /**
     * 方法参数值
     */
    public static final String ARGS = "args";

    /**
     * 请求的url
     */
    public static final String REQUEST_URL = "requesturl";

    /**
     * 请求服务的ip地址
     */
    public static final String REQUEST_IP = "requestip";

    /**
     * 打印日期
     */
    public static final String DATE = "date";

    /**
     * 打印时间
     */
    public static final String TIME = "time";

    /**
     * 打接口耗时
     */
    public static final String DIFFTIME = "diffTime";
}
