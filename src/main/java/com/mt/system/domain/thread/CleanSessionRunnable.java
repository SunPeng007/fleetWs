package com.mt.system.domain.thread;

import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.constant.ConnectTimeConstant;
import com.mt.system.domain.entity.MtSession;
import com.mt.system.websocket.MtWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/4/16
 * Time:10:16
 */
public class CleanSessionRunnable implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(CleanSessionRunnable.class);
    // 创建一个静态钥匙
    private static Object mtSessionKey = "MoTooling";
    @Override
    public void run() {
        while (true) {
            synchronized(mtSessionKey){
                try{
                    ConcurrentHashMap<String,MtSession> mtSessionMap = MtWebSocketServer.getMtSessionMap();
                    for (MtSession mtSession:mtSessionMap.values()){
                        if(DateUtils.currentCompare(mtSession.getConnectTime())>ConnectTimeConstant.EFFECTIVE_TIME_CODE){
                            if(!mtSession.getSession().isOpen()){//连接是否打开
                                mtSessionMap.remove(mtSession.getToken());
                                logger.info("清除连接："+mtSession.getToken());
                            }
                        }
                    }
                    Thread.sleep(ConnectTimeConstant.CLOSE_SESSION_TIME_CODE);//睡10分钟
                }catch (Exception e) {
                    e.printStackTrace();
                    logger.error("重发异常:"+e);
                }
            }
        }
    }
}
