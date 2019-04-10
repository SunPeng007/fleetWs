package com.mt.system.common.tasks;

import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.constant.ConnectTimeConstant;
import com.mt.system.domain.entity.MtSession;
import com.mt.system.websocket.MtWebSocketServer;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/4/10
 * Time:17:46
 */
@Component
@Configurable
@EnableScheduling
public class CleanWebSocketTasks {

    //每10分钟执行一次
//    @Scheduled(cron = "0 */1 *  * * * ")
    @Scheduled(cron = "0 */10 * * * * ")
    public void reportCurrentByCron(){
        ConcurrentHashMap<String,MtSession> mtSessionMap = MtWebSocketServer.getMtSessionMap();
        for (MtSession mtSession:mtSessionMap.values()){
            if(DateUtils.currentCompare(mtSession.getConnectTime())>ConnectTimeConstant.EFFECTIVE_TIME_CODE){
                mtSessionMap.remove(mtSession.getToken());
            }
        }
    }
}
