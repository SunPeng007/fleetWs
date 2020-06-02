package com.mt.system.domain.thread.im;

import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.constant.ConnectTimeConstant;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.im.SynergyGroupRecord;
import com.mt.system.websocket.im.MtContainerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
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
                //遍历所有公司
                Iterator<String> companyIter = mtReceiveMap.keySet().iterator();
                while(companyIter.hasNext()) {
                    String companyId = companyIter.next();//公司id
                    ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>>> groupSession=mtReceiveMap.get(companyId);
                    //遍历所有群
                    Iterator<String>  groupIter = groupSession.keySet().iterator();
                    while(groupIter.hasNext()) {
                        String groupId = groupIter.next();//群id
                        ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>> contSession=groupSession.get(groupId);
                        //遍历所有接受消息
                        Iterator<String>  receiveIter = contSession.keySet().iterator();
                        while(receiveIter.hasNext()) {
                            String key = receiveIter.next();//key
                            BaseBuilder<SynergyGroupRecord> baseBuilder = contSession.get(key);
                            if(DateUtils.currentCompare(baseBuilder.getPushTime())>ConnectTimeConstant.CLOSE_TIME_DATA_CODE){
                                MtContainerUtil.mtReceiveMapRemove(companyId,groupId,key);
                                logger.info("协同清除接受数据：" + key);
                            }
                        }
                    }
                }
                Thread.sleep(ConnectTimeConstant.CLOSE_RECEIVE_TIME_CODE);
            }catch (Exception e) {
                e.printStackTrace();
                logger.error("协同清除接受数据异常:"+e);
            }
        }
    }
}
