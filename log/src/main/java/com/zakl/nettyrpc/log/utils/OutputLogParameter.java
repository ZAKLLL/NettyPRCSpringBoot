package com.zakl.nettyrpc.log.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 日志参数输出操作类 利用反射机制输出
 *
 * @author TP
 * @version 1.0
 * @since 2019 -09-05 18:24:11
 */
public class OutputLogParameter {
    /**
     * 输出参数/字段真实值
     *
     * @param o    参数/字段值
     * @param name 参数/字段名
     * @return string string
     */
    public static String printFields(Object o, String name) {
        return printFields(o, name, 0);
    }

    /**
     * 输出参数/字段真实值
     *
     * @param o    参数/字段值
     * @param name 参数/字段名
     * @param ilay 当前已展开层次
     * @return string string
     */
    public static String printFields(Object o, String name, int ilay) {
        String result = "";
        Object value = null;
        Class<?> cls = (Class<?>)o.getClass();
        if (cls != null) {
            try {
                // 基本类型（int, double, float, long, short, boolean, byte,
                // char，void）
                try {
                    if (((Class<?>)cls.getField("TYPE").get(null)).isPrimitive()) {
                        return formatField(name, o, cls);
                    }
                } catch (Exception e1) {
                }
                // string,Integer,BigInteger,BigDecimal,Date,UUID
                if (cls == String.class || cls == Integer.class || cls == Long.class || cls == Short.class
                    || cls == Double.class || cls == Float.class || cls == Boolean.class || cls == Byte.class
                    || cls == Character.class || cls == BigInteger.class || cls == BigDecimal.class || cls == Date.class
                    || cls == UUID.class) {
                    return formatField(name, o, cls);
                    
                }
                // Calendar
                if (cls == Calendar.class || cls == GregorianCalendar.class) {
                    return formatField(name, o, cls);
                }
                // 数组
                if (cls.isArray()) {
                    result = result + printArray(o, name, ilay + 1);
                    return result;
                }
                // 集合
                if (o instanceof Collection) {
                    result = result + printCollection(o, name, cls, ilay + 1);
                    return result;
                }
                // 键值对
                if (o instanceof Map) {
                    result = result + printMap(o, name, cls, ilay + 1);
                    return result;
                }
                // 自定义对象
                if (name == null || name.isEmpty()) {
                    name = cls.getTypeName();
                }
                
                result = result + name + ":{";
                if (name.indexOf("com.southgis.") < 0 || ilay > 10) { //非系统内定义的对象 或 已展开10层，不再展开
                    result = result + "...}";
                    return result;
                }
                //系统内对象
                Field[] fs = cls.getDeclaredFields();
                if (fs != null) {
                    for (Field f : fs) {
                        f.setAccessible(true);
                        value = f.get(o);
                        if (value != null) {
                            if (value instanceof Collection) {
                                result = result + printCollection(value, f.getName(), null, ilay + 1);
                            } else if (value instanceof Map) {
                                result = result + printMap(value, f.getName(), null, ilay + 1);
                            }
                            //springmvc 注入对象要排除(排除jre类)
                            else if (value.getClass().getClassLoader() != null) {
                                result = result + printFields(value, f.getName(), ilay + 1);
                            }
                        } else {
                            result = result + f.getName() + ":null";
                        }
                        result = result + ",";
                    }
                }
                if (result.endsWith(",")) {
                    result = result.substring(0, result.length() - 1);
                }
                result = result + "}";
                
            } catch (Exception e1) {
                
            }
            
        }
        return result;
    }

    /**
     * 输出集合类型值
     *
     * @param value 集合值
     * @param name  参数/字段名
     * @param clazz 参数/字段class对象
     * @param ilay  当前已展开层次
     * @return string
     */
    @SuppressWarnings("unchecked")
    private static String printCollection(Object value, String name, Class<?> clazz, int ilay) {
        String result = "";
        if (name != null && !name.isEmpty()) {
            result = result + name + ":[";
        } else {
            result = result + clazz.getTypeName() + ":[";
        }
        Collection<Object> co = (Collection<Object>)value;
        if (co != null) {
            
            Object[] os = co.toArray();
            for (int n = 0; n < os.length; n++) {
                result = result + printFields(os[n], "", ilay + 1);
                if (n != os.length - 1) {
                    result = result + ",";
                }
            }
            result = result + "]";
        }
        return result;
    }

    /**
     * 输出键值对类型值
     *
     * @param value 键值对
     * @param name  参数/字段名
     * @param clazz 参数/字段class对象
     * @param ilay  当前已展开层次
     * @return string
     */
    @SuppressWarnings("unchecked")
    private static String printMap(Object value, String name, Class<?> clazz, int ilay) {
        String result = "";
        if (name != null && !name.isEmpty()) {
            result = result + name + ":[";
        } else {
            result = result + clazz.getTypeName() + ":[";
        }
        Map<Object, Object> m = ((Map<Object, Object>)value);
        if (m != null) {
            Collection<Object> keys = m.keySet();
            Object[] os = keys.toArray();
            for (int n = 0; n < os.length; n++) {
                result = result + "{";
                result = result + printFields(os[n], "key", ilay + 1);
                result = result + ",value:";
                result = result + printFields(m.get(os[n]), "", ilay + 1);
                result = result + "}";
                if (n != os.length - 1) {
                    result = result + ",";
                }
            }
            result = result + "]";
        }
        return result;
    }

    /**
     * 输出数组类型值
     *
     * @param value 数组
     * @param name  参数/字段名
     * @param ilay  当前已展开层次
     * @return string
     */
    private static String printArray(Object value, String name, int ilay) {
        String result = "";
        int length = Array.getLength(value);
        if (name != null && !name.isEmpty()) {
            result = String.format("%s:[", name);
        } else {
            result = String.format("%s:[", Array.class.getTypeName());
        }
        for (int i = 0; i < length; i++) {
            if (Array.get(value, i) == null) {
                break;
            }
            result = result + printFields(Array.get(value, i), "", ilay + 1);
            if (i != length - 1) {
                result = result + ",";
            }
        }
        result = result + "]";
        return result;
    }

    /**
     * 输出基本数据类型值
     *
     * @param name  参数/字段名
     * @param value 值
     * @param clazz 参数/字段class对象
     * @return string
     */
    private static String formatField(String name, Object value, Class<?> clazz) {
        String result = "";
        if (clazz == Calendar.class || clazz == GregorianCalendar.class) {
            Calendar cl = (Calendar)value;
            if (cl != null) {
                value = cl.getTime();
            }
        }
        if (clazz == String.class) {
            String svalue = value + "";
            if (svalue.contains("\"")) {
                value = svalue.replace("\"", "\"\"");
            }
            value = "\"" + value + "\"";
        }
        if (name != null && !name.isEmpty()) {
            result = String.format("%s:%s", name, value);
        } else {
            result = String.format("(%s):%s", clazz.getTypeName(), value);
        }
        return result;
    }
}
