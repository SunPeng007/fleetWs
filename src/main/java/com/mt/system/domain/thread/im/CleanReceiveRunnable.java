package com.mt.system.domain.thread.im;

import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.constant.ConnectTimeConstant;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.im.SynergyGroupRecord;
import com.mt.system.websocket.im.MtContainerUtil;
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
    @Override
    public void run() {
        while (true) {
            try{
                ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>>>> mtReceiveMap = MtContainerUtil.getMtReceiveMap();
                for (ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>>> groupSession : mtReceiveMap.values()){
                    for (ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>> contSession : groupSession.values()){
                        for (BaseBuilder<SynergyGroupRecord> baseBuilder : contSession.values()) {
                            if(DateUtils.currentCompare(baseBuilder.getPushTime())>ConnectTimeConstant.CLOSE_TIME_DATA_CODE){
                                String token = baseBuilder.getPustToken()+baseBuilder.getSerialNumber();
                                mtReceiveMap.remove(token);
                            }
                        }
                    }
                }
                Thread.sleep(ConnectTimeConstant.CLOSE_RECEIVE_TIME_CODE);
            }catch (Exception e) {
                e.printStackTrace();
                logger.error("清除接受数据异常:"+e);
            }
        }
    }
}
