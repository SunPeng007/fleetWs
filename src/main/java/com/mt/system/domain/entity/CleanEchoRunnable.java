package com.mt.system.domain.entity;

import com.alibaba.fastjson.JSONObject;
import com.mt.system.domain.constant.ConnectTimeConstant;
import com.mt.system.domain.constant.PantNumberConstant;
import com.mt.system.websocket.MtWebSocketServer;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/4/13
 * Time:15:25
 */
public class CleanEchoRunnable implements Runnable{
    // 创建一个静态钥匙
    static Object mtKey = "MoTooling";
    @Override
    public void run() {
        while (true) {
            synchronized(mtKey){
                try{
                    ConcurrentHashMap<String,BaseBuilder> mtEchoMap = MtWebSocketServer.getMtEchoMap();
                    ConcurrentHashMap<String,MtSession> mtSessionMap = MtWebSocketServer.getMtSessionMap();
                    /*判断是否需要重发*/
                    for (BaseBuilder baseBuilder : mtEchoMap.values()) {
                        if(baseBuilder.getPustNumber()<PantNumberConstant.PANT_NUMBER_CODE){
                            baseBuilder.setPustNumber((baseBuilder.getPustNumber()+1));
                            mtSessionMap.get(baseBuilder.getReceiveToken()).getSession().getBasicRemote().sendText(JSONObject.toJSONString(baseBuilder));
                        }
                    }
                    Thread.sleep(ConnectTimeConstant.SLEEP_TIME_CODE);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
