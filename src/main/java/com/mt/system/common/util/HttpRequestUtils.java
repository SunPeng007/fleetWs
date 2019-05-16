package com.mt.system.common.util;

import com.alibaba.fastjson.JSON;
import com.xiaoleilu.hutool.http.HttpRequest;
import com.xiaoleilu.hutool.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

/**
 *  http请求工具类（常用调用第三方接口）
 * Created with IDEA
 * author: chenpan
 * Date:2019/5/9
 * Time:14:58
 */
public class HttpRequestUtils {


    /**
     * bu参数加密
     * @param dataMap
     * @return
     */
    public static Map<String,Object> getBuEncryptionParam(Map<String,Object> dataMap){
        Map<String, Object> reqParam = new HashMap<>();
        reqParam.put("paramsData", RSAUtils4Client.encryptionDataPacket(dataMap, "", Long.valueOf(DateUtils.currentTimeMillis())));
        return reqParam;
    }

    /**
     * bu参数解密
     * @param dataMap
     * @return
     */
    public static Map<String,Object> getBuDecryptionParam(Map<String,Object> dataMap){
        return RSAUtils4Client.decryptionPostParam(dataMap.get("resultData").toString(), RSAUtils4Client.RES_PRIVATE_KEY);
    }

    /**
     * 请求第三方接口
     * （默认JSON格式请求）
     * @param url 请求地址
     * @param paramMap 请求参数
     * @return
     */
    public static Map<String,Object> httpPost(String url,Map<String,Object> paramMap){
        //设置请求信息
        HttpRequest http = HttpRequest.post(url).contentType("application/json");
        if(paramMap!=null){
            http.body(JsonUtil.toFastJson(paramMap));
        }
        //执行-调用
        HttpResponse res =http.execute();
        return JSON.parseObject(res.body(), Map.class);
    }

    /**
     * 请求第三方接口
     * @param url 请求地址
     * @param paramMap 请求参数
     * @param contentType 请求数据格式
     * @return
     */
    public static Map<String,Object> httpPost(String url,Map<String,Object> paramMap,String contentType){
        //设置请求信息
        HttpRequest http = HttpRequest.post(url).contentType(contentType);
        if(paramMap!=null){
            http.body(JsonUtil.toFastJson(paramMap));
        }
        //执行-调用
        HttpResponse res =http.execute();
        return JSON.parseObject(res.body(), Map.class);
    }
}
