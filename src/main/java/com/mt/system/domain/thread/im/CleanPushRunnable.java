package com.mt.system.domain.thread.im;

import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.constant.ConnectTimeConstant;
import com.mt.system.domain.constant.PantNumberConstant;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.MtSession;
import com.mt.system.domain.entity.im.SynergyGroupRecord;
import com.mt.system.websocket.im.MtContainerUtil;
import com.mt.system.websocket.im.MtWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.websocket.Session;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: chenpan
 * Date:2019/4/13
 * Time:15:25
 */
public class CleanPushRunnable implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(CleanPushRunnable.class);
    @Override
    public void run() {
        while (true) {
            try{
                /*判断是否需要重发*/
                ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>>>> mtPushMap = MtContainerUtil.getMtPushMap();
                //遍历所有-公司
                Iterator<String> companyIter = mtPushMap.keySet().iterator();
                while(companyIter.hasNext()) {
                    String companyId = companyIter.next();//公司id
                    ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>>> groupSession=mtPushMap.get(companyId);
                    //遍历所有-群
                    Iterator<String> groupIter = groupSession.keySet().iterator();
                    while(groupIter.hasNext()) {
                        String groupId = groupIter.next();//群id
                        ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>> contSession =groupSession.get(groupId);
                        //遍历所有-发送消息
                        Iterator<String> pushIter = contSession.keySet().iterator();
                        while(pushIter.hasNext()) {
                            String key = pushIter.next();//key
                            BaseBuilder<SynergyGroupRecord> baseBuilder =contSession.get(key);
                            String token = baseBuilder.getReceiveToken();//token
                            MtSession mtSession=MtContainerUtil.getMtSessionMap(companyId,groupId,token);
                            if(mtSession!=null){
                                Session session=mtSession.getSession();
                                //先判断连接是否打开
                                if(!session.isOpen()) {
                                    MtContainerUtil.mtPushRemove(companyId,groupId,key);
                                    MtContainerUtil.mtSessionMapRemove(companyId,groupId,token);
                                    continue;
                                }
                                //判断重发次数是否达到上限
                                if(baseBuilder.getPustNumber()>=PantNumberConstant.PANT_NUMBER_CODE){
                                    MtContainerUtil.mtPushRemove(companyId,groupId,key);
                                    MtContainerUtil.mtSessionMapRemove(companyId,groupId,token);
                                    continue;
                                }
                                //判断是否需要重发
                                if(DateUtils.currentCompare(baseBuilder.getPushTime())>ConnectTimeConstant.ANSWER_TIME_CODE){
                                    baseBuilder.setPustNumber((baseBuilder.getPustNumber()+1));
                                    MtWebSocketServer.mtSendText(session,companyId,groupId,baseBuilder);
                                }
                            }
                        }
                    }
                }
                Thread.sleep(ConnectTimeConstant.CLOSE_PUSH_TIME_CODE);
            }catch (Exception e) {
                e.printStackTrace();
                logger.error("协同消息重发异常:"+e);
            }
        }
    }
}
