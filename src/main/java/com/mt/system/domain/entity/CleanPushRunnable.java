package com.mt.system.domain.entity;

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
public class CleanPushRunnable implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(CleanPushRunnable.class);
    // 创建一个静态钥匙
    private static Object mtPushKey = "MoTooling";
    @Override
    public void run() {
        while (true) {
            synchronized(mtPushKey){
                try{
                    ConcurrentHashMap<String,BaseBuilder> mtPushMap = MtWebSocketServer.getMtPushMap();
                    ConcurrentHashMap<String,MtSession> mtSessionMap = MtWebSocketServer.getMtSessionMap();
                    /*判断是否需要重发*/
                    if(mtPushMap!=null && mtPushMap.size()>0){
                        for (BaseBuilder baseBuilder : mtPushMap.values()) {
                            logger.info("开始检测是否需要重发!");
                            String token = baseBuilder.getReceiveToken();
                            Session session=mtSessionMap.get(token).getSession();
                            //先判断连接是否打开
                            if(!session.isOpen()) {
                                //清除连接
                                closeSession(mtPushMap,mtSessionMap,token);
                                continue;
                            }
                            //判断是否需要重发
                            if(DateUtils.currentCompare(baseBuilder.getPushTime())>ConnectTimeConstant.ANSWER_TIME_CODE){
                                //判断重发次数是否达到上限
                                if(baseBuilder.getPustNumber()<PantNumberConstant.PANT_NUMBER_CODE){
                                    baseBuilder.setPustNumber((baseBuilder.getPustNumber()+1));
                                    MtWebSocketServer.mtSendText(session,baseBuilder);
                                    logger.info(token+"消息重发!");
                                }else{
                                    //清除连接
                                    closeSession(mtPushMap,mtSessionMap,token);
                                }
                            }
                        }
                    }
                    Thread.sleep(ConnectTimeConstant.CLOSE_PUSH_TIME_CODE);
                }catch (Exception e) {
                    e.printStackTrace();
                    logger.error("重发异常:"+e);
                }
            }
        }
    }
    /**
     * 清除连接
     * @param mtPushMap
     * @param mtSessionMap
     * @param token
     */
    public void closeSession(ConcurrentHashMap<String,BaseBuilder> mtPushMap,ConcurrentHashMap<String,MtSession> mtSessionMap,String token){
        //清除该未回应信息
        mtPushMap.remove(token);
        //清除该未回应信息 -- 的连接
        mtSessionMap.remove(token);
        logger.info("清除连接："+token);
    }

}
