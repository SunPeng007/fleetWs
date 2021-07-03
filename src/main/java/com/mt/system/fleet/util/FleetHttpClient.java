package com.mt.system.fleet.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mt.system.fleet.common.ApiResponse;
import com.mt.system.fleet.properties.FleetProperties;

@Component
public class FleetHttpClient {

    @Autowired
    private FleetProperties fleetProperties;

    private static final String API_GET_ADMIN_USER = "/getUserInfo";

    public Map<String, Object> getAdminUser(String sessionId) {
        String url = HttpClientUtil.getUrlApi(fleetProperties.getApiFleetAdmin(), API_GET_ADMIN_USER);
        Map<String, Object> map = new HashMap<>();
        map.put("sessionID", sessionId);
        String res;
        try {
            res = HttpClientUtil.postForJson(url, map);
            ApiResponse response = json2Obj(res);
            if (response != null && Boolean.TRUE.equals(response.getSuccess())) {
                Map<String, Object> m = (Map<String, Object>)response.getData();
                return m;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ApiResponse json2Obj(String res) {
        ApiResponse o = com.alibaba.fastjson.JSONObject.parseObject(res, ApiResponse.class);
        return o;
    }
}
