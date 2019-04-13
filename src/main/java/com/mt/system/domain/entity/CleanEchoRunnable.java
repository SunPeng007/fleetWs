package com.mt.system.domain.entity;

import com.alibaba.fastjson.JSONObject;
import com.mt.system.domain.constant.ConnectTimeConstant;
import com.mt.system.domain.constant.PantNumberConstant;
import com.mt.system.websocket.MtWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/4/13
 * Time:15:25
 */
public class CleanEchoRunnable implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(CleanEchoRunnable.class);
    // 创建一个静态钥匙
    private static Object mtKey = "MoTooling";
    @Override
    public void run() {
        while (true) {
            synchronized(mtKey){
                try{
                    ConcurrentHashMap<String,BaseBuilder> mtEchoMap = MtWebSocketServer.getMtEchoMap();
                    ConcurrentHashMap<String,MtSession> mtSessionMap = MtWebSocketServer.getMtSessionMap();
                    /*判断是否需要重发*/
                    for (BaseBuilder baseBuilder : mtEchoMap.values()) {
                        String token = baseBuilder.getReceiveToken();
                        if(baseBuilder.getPustNumber()<PantNumberConstant.PANT_NUMBER_CODE){
                            baseBuilder.setPustNumber((baseBuilder.getPustNumber()+1));
                            mtSessionMap.get(token).getSession().getBasicRemote().sendText(JSONObject.toJSONString(baseBuilder));
                            logger.info(token+"连接重发!");
                        }else{
                            //清除该未回应信息
                            mtEchoMap.remove(token);
                            //清除该未回应信息 -- 的连接
                            mtSessionMap.remove(token);
                            logger.info("清除连接："+token);
                        }
                    }
                    Thread.sleep(ConnectTimeConstant.SLEEP_TIME_CODE);
                }catch (Exception e) {
                    e.printStackTrace();
                    logger.error("重发异常:"+e);
                }
            }
        }
    }
}
