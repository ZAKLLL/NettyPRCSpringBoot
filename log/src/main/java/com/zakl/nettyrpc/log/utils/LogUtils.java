package com.zakl.nettyrpc.log.utils;

import com.zakl.nettyrpc.log.constant.LogItemNames;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.spi.LocationAwareLogger;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * The type Log utils.
 *
 * @author thz
 * @since 2020 /4/26
 */
public class LogUtils {
    /**
     * 空数组
     */
    private static final Object[] EMPTY_ARRAY = new Object[]{};
    /**
     * 全类名
     */
    private static final String FQCN = LogUtils.class.getName();

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);

    /**
     * 获取栈中类信息
     *
     * @param stackDepth 栈深（下标） 2：调用者类信息
     * @return org.slf4j.spi.LocationAwareLogger
     */
    private static LocationAwareLogger getLocationAwareLogger(final int stackDepth) {
        /**通过堆栈信息获取调用当前方法的类名和方法名*/
        JavaLangAccess access = SharedSecrets.getJavaLangAccess();
        Throwable throwable = new Throwable();
        StackTraceElement frame = access.getStackTraceElement(throwable, stackDepth);
        MDC.put(LogItemNames.TYPE, LogItemNames.LogType.BUSINESS);
        MDC.put(LogItemNames.CLASS, frame.getClassName());
        MDC.put(LogItemNames.METHOD, frame.getMethodName());
        MDC.put(LogItemNames.LINENUMBER, java.lang.String.valueOf(frame.getLineNumber()));
        MDC.put(LogItemNames.DATE, String.valueOf(LocalDate.now()));
        MDC.put(LogItemNames.TIME, LocalTime.now().format(DATE_TIME_FORMAT));
        return (LocationAwareLogger) LoggerFactory.getLogger(frame.getClassName());
    }

    /**
     * 封装Debug级别日志
     *
     * @param msg       the msg
     * @param arguments the arguments
     */
    public static void debug(String msg, Object... arguments) {
        if (arguments != null && arguments.length > 0) {
            MessageFormat temp = new MessageFormat(msg);
            msg = temp.format(arguments);
        }
        MDC.put(LogItemNames.MESSAGE, msg);
        getLocationAwareLogger(2).log(null, FQCN, LocationAwareLogger.DEBUG_INT, msg, EMPTY_ARRAY, null);
    }

    /**
     * 封装Info级别日志
     *
     * @param msg       the msg
     * @param arguments the arguments
     */
    public static void info(String msg, Object... arguments) {
        if (arguments != null && arguments.length > 0) {
            MessageFormat temp = new MessageFormat(msg);
            msg = temp.format(arguments);
        }
        MDC.put(LogItemNames.MESSAGE, msg);
        getLocationAwareLogger(2).log(null, FQCN, LocationAwareLogger.INFO_INT, msg, EMPTY_ARRAY, null);
    }

    /**
     * 封装Warn级别日志
     *
     * @param msg       the msg
     * @param arguments the arguments
     */
    public static void warn(String msg, Object... arguments) {
        if (arguments != null && arguments.length > 0) {
            MessageFormat temp = new MessageFormat(msg);
            msg = temp.format(arguments);
        }
        MDC.put(LogItemNames.MESSAGE, msg);
        getLocationAwareLogger(2).log(null, FQCN, LocationAwareLogger.WARN_INT, msg, EMPTY_ARRAY, null);
    }

    /**
     * 封装Error级别日志
     *
     * @param msg       the msg
     * @param arguments the arguments
     */
    public static void error(String msg, Object... arguments) {
        if (arguments != null && arguments.length > 0) {
            MessageFormat temp = new MessageFormat(msg);
            msg = temp.format(arguments);
        }
        MDC.put(LogItemNames.MESSAGE, msg);
        LocationAwareLogger locationAwareLogger = getLocationAwareLogger(2);
        MDC.put(LogItemNames.TYPE, LogItemNames.LogType.ERROR);
        locationAwareLogger.log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, EMPTY_ARRAY, null);
    }
}
