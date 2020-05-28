package com.mt.system.domain.thread.msg;

import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.constant.ConnectTimeConstant;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.im.SynergyGroupRecord;
import com.mt.system.domain.entity.msg.ReceiveMessage;
import com.mt.system.websocket.im.MtContainerUtil;
import com.mt.system.websocket.msg.MtMsgContainerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: chenpan
 * Date:2020/5/28
 * Time:8:55
 */
public class CleanMsgReceiveRunnable implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(CleanMsgReceiveRunnable.class);
    @Override
    public void run() {
        while (true) {
            try{
                ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder<ReceiveMessage>>> mtReceiveMap = MtMsgContainerUtil.getMtMsgReceiveMap();
                for (ConcurrentHashMap<String,BaseBuilder<ReceiveMessage>> companyMap : mtReceiveMap.values()){
                    for (BaseBuilder<ReceiveMessage> baseBuilder : companyMap.values()) {
                        if(DateUtils.currentCompare(baseBuilder.getPushTime())>ConnectTimeConstant.CLOSE_TIME_DATA_CODE){
                            String token = baseBuilder.getPustToken()+baseBuilder.getSerialNumber();
                            mtReceiveMap.remove(token);
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
