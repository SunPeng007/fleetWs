package com.mt.system.common.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
* @Author: Fhk
* @Description:
* @create    2017/11/25 13:16
* @Modified By:
*/
public class BeanToMapUtil {

    /**
     * 将一个 Map 对象转化为一个 JavaBean
     * @param type 要转化的类型
     * @param map 包含属性值的 map
     * @return 转化出来的 JavaBean 对象
     * @throws IntrospectionException
     *             如果分析类属性失败
     * @throws IllegalAccessException
     *             如果实例化 JavaBean 失败
     * @throws InstantiationException
     *             如果实例化 JavaBean 失败
     * @throws InvocationTargetException
     *             如果调用属性的 setter 方法失败
     */
    public final static SimpleDateFormat SDF_LONG = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat SDF_SHORT = new SimpleDateFormat("yyyy-MM-dd");
    protected static Integer lock = 0;

    public static Object convertMap(Class type, Map map) throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException {
        if(map == null) { return null; }
        // 获取类属性
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        // 创建 JavaBean 对象
        Object obj = type.newInstance();

        // 给 JavaBean 对象的属性赋值
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (map.containsKey(propertyName)) {
                // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                Object value = map.get(propertyName);
                Object[] args = new Object[1];
                String propertyType = descriptor.getPropertyType().getTypeName();

                switch (propertyType) {
                    case "java.lang.Integer":
                        args[0] = value == null || value.toString().isEmpty() ? null : (int)Double.parseDouble(value.toString());
                        break;
                    case "java.lang.Long":
                        args[0] = value == null || value.toString().isEmpty() ? null : Long.parseLong(value.toString());
                        break;
                    case "java.lang.Byte":
                        args[0] = value == null || value.toString().isEmpty() ? null : Byte.parseByte(value.toString());
                        break;
                    case "java.math.BigDecimal":
                        args[0] = value == null || value.toString().isEmpty() ? null : new BigDecimal(value.toString());
                        break;
                    case "java.math.BigInteger":
                        args[0] = value == null || value.toString().isEmpty() ? null : new BigInteger(value.toString());
                        break;
                    case "java.lang.String":
                        args[0] = value == null ? null : value.toString().trim();
                        break;
                    case "java.lang.Boolean":
                        args[0] = value == null ? null : "1".equals(value.toString().trim());
                        break;
                    case "java.util.Date":
                        if (value == null) {
                            args[0] = null;
                        } else if (value.toString().indexOf(":") >= 0) {
                            try {
                                synchronized (lock) {
                                    args[0] = SDF_LONG.parse(value.toString());
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                args[0] = null;
                            }
                        } else if (value.toString().indexOf("-") >= 0) {
                            try {
                                synchronized (lock) {
                                    args[0] = SDF_SHORT.parse(value.toString());
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                args[0] = null;
                            }
                        } else {
                            args[0] = null;
                        }
                        break;
                    default:
                        args[0] = value;
                }
                descriptor.getWriteMethod().invoke(obj, args);
            }
        }
        return obj;
    }

    public static Object convertMapList(Class type, List<?> mapList) throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException {
        if (mapList == null || mapList.size() < 1) { return  null; }
        List<Object> retList = new ArrayList<>();
        for (Object o1: mapList) {
            Map map = (Map) o1;
            retList.add(convertMap(type, map));
        }
        return retList;
    }

    /**
     * 将一个 JavaBean 对象转化为一个  Map
     * @param bean 要转化的JavaBean 对象
     * @return 转化出来的  Map 对象
     * @throws IntrospectionException 如果分析类属性失败
     * @throws IllegalAccessException 如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    public static Map convertBean(Object bean)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        if(bean == null) { return null; }
        Class type = bean.getClass();
        Map returnMap = new HashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);

        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            String propertyType = descriptor.getPropertyType().getTypeName();
            if (!"class".equals(propertyName)) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    if ("java.util.Date".equals(propertyType)) {
                        synchronized (lock) {
                            returnMap.put(propertyName, result == null ? null : SDF_LONG.format(result));
                        }
                    } else {
                        returnMap.put(propertyName, result);
                    }
                } else {
                    returnMap.put(propertyName, null);
                }
            }
        }
        return returnMap;
    }

    public static ArrayList<Map<String, Object>> convertBeanList(List<?> beanList)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        if(beanList == null || beanList.size() < 1) { return null; }
        ArrayList<Map<String, Object>> retMapList = new ArrayList<Map<String, Object>>();
        for (Object bean: beanList) {
            retMapList.add(convertBean(bean));
            /*Class type = bean.getClass();
            Map returnMap = new HashMap();
            BeanInfo beanInfo = Introspector.getBeanInfo(type);

            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                String propertyType = descriptor.getPropertyType().getTypeName();
                if (!"class".equals(propertyName)) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = readMethod.invoke(bean, new Object[0]);
                    if (result != null) {
                        if ("java.util.Date".equals(propertyType)) {
                            synchronized (lock) {
                                returnMap.put(propertyName, result == null ? null : SDF_LONG.format(result));
                            }
                        } else {
                            returnMap.put(propertyName, result);
                        }
                    } else {
                        returnMap.put(propertyName, null);
                    }
                }
            }
            retMapList.add(returnMap);*/
        }
        return retMapList;
    }

    public static Date convertStrToDate(String dataStr) {
        if (dataStr == null) {
            return null;
        } else if (dataStr.indexOf(":") >= 0) {
            try {
                synchronized (lock) {
                    return SDF_LONG.parse(dataStr);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else if (dataStr.indexOf("-") >= 0) {
            try {
                synchronized (lock) {
                    return SDF_SHORT.parse(dataStr);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
}