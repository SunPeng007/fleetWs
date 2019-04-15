package com.mt.system.domain.entity;

import com.alibaba.fastjson.JSONObject;
import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.constant.ConnectTimeConstant;
import com.mt.system.domain.constant.PantNumberConstant;
import com.mt.system.websocket.MtWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: chenpan
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
                    ConcurrentHashMap<String,BaseBuilder> mtEchoMap = MtWebSocketServer.getMtPushMap();
                    ConcurrentHashMap<String,MtSession> mtSessionMap = MtWebSocketServer.getMtSessionMap();
                    /*判断是否需要重发*/
                    for (BaseBuilder baseBuilder : mtEchoMap.values()) {
                        String token = baseBuilder.getReceiveToken();
                        //先判断连接是否打开
                        if(mtSessionMap.get(token)!=null){
                            Session session=mtSessionMap.get(token).getSession();
                            if(session.isOpen()){
                                //判断是否需要重发
                                if(DateUtils.currentCompare(baseBuilder.getPushTime())>ConnectTimeConstant.ANSWER_TIME_CODE){
                                    //判断重发次数是否达到上限
                                    if(baseBuilder.getPustNumber()<PantNumberConstant.PANT_NUMBER_CODE){
                                        baseBuilder.setPustNumber((baseBuilder.getPustNumber()+1));
                                        MtWebSocketServer.mtSendText(session,baseBuilder);
                                        logger.info(token+"连接重发!");
                                    }else{
                                        //清除连接
                                        closeSession(mtEchoMap,mtSessionMap,token);
                                    }
                                }
                            }else{
                                //清除连接
                                closeSession(mtEchoMap,mtSessionMap,token);
                            }
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
    /**
     * 清除连接
     * @param mtEchoMap
     * @param mtSessionMap
     * @param token
     */
    public void closeSession(ConcurrentHashMap<String,BaseBuilder> mtEchoMap,ConcurrentHashMap<String,MtSession> mtSessionMap,String token){
        //清除该未回应信息
        mtEchoMap.remove(token);
        //清除该未回应信息 -- 的连接
        mtSessionMap.remove(token);
        logger.info("清除连接："+token);
    }

}
