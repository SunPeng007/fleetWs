package com.mt.system.common.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * JSON转换工具类
 * Created by tangmingkun on 4/27/18.
 */
public class JsonUtil {

    /**
     * object转json字符串
     * @param obj
     */
    public static String toFastJson(Object obj) {
        return JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * json字符串转对象
     * @param json
     * @param clazz 泛型类
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    //返回一个空的json
    public static JSONObject toNullJson(){
        return JSONObject.parseObject("{}");
    }
}
