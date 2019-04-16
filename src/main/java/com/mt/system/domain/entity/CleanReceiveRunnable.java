package com.mt.system.domain.entity;

import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.constant.ConnectTimeConstant;
import com.mt.system.websocket.MtWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/4/16
 * Time:8:55
 */
public class CleanReceiveRunnable implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(CleanReceiveRunnable.class);
    // 创建一个静态钥匙
    private static Object mtReceiveKey = "MoTooling";
    @Override
    public void run() {
        while (true) {
            synchronized(mtReceiveKey){
                try{
                    ConcurrentHashMap<String,BaseBuilder> mtReceiveMap = MtWebSocketServer.getMtReceiveMap();
                    /*判断是否清除*/
                    if(mtReceiveMap!=null && mtReceiveMap.size()>0) {
                        for (BaseBuilder baseBuilder : mtReceiveMap.values()) {
                            if(DateUtils.currentCompare(baseBuilder.getPushTime())>ConnectTimeConstant.CLOSE_TIME_DATA_CODE){
                                String token = baseBuilder.getPustToken()+baseBuilder.getSerialNumber();
                                mtReceiveMap.remove(token);
                            }
                        }
                    }
                    Thread.sleep(ConnectTimeConstant.CLOSE_RECEIVE_TIME_CODE);
                }catch (Exception e) {
                    e.printStackTrace();
                    logger.error("重发异常:"+e);
                }
            }
        }
    }
}
