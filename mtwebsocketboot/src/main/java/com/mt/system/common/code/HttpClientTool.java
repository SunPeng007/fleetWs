package com.mt.system.common.code;

import com.mt.system.common.util.DateUtils;
import com.mt.system.common.util.HttpUtils;
import com.mt.system.common.util.RSAUtils4Client;
import com.mt.system.domain.properties.SystemProperties;
import net.sf.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/1/24
 * Time:8:57
 */
public class HttpClientTool {

    /**
     * MoTooling -- http请求
     * @param dataMap data参数
     * @param urls 请求地址
     * @return
     */
    public static JSONObject mtHttpPost(Map<String,Object> dataMap,String urls)  throws RuntimeException {
        Map<String,String> reqParam=new HashMap<>();
        reqParam.put("paramsData",RSAUtils4Client.encryptionDataPacket(dataMap,"",Long.valueOf(DateUtils.currentTimeMillis())));
        String resultData = HttpUtils.sendPost(SystemProperties.buApiUrl+urls,reqParam);
        JSONObject jsonData=JSONObject.fromObject(resultData);
        Map<String,Object> results=RSAUtils4Client.decryptionPostParam(jsonData.get("resultData").toString(),RSAUtils4Client.RES_PRIVATE_KEY);
        if (results == null || !"000000".equals(results.get("code").toString().trim())) {
            throw new RuntimeException(results.get("msg").toString());
        }
        JSONObject resultJson=JSONObject.fromObject(results.get("data"));
        return resultJson;
    }

}
