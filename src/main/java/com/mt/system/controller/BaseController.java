package com.mt.system.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/4/16
 * Time:10:48
 */
public class BaseController {
    /**
     * 响应结果
     * @param data
     * @return
     */
    protected Object resData(String data) {
        Map<String,Object> retMap = new HashMap<>();
        retMap.put("resultData", data);
        return retMap;
    }
}
