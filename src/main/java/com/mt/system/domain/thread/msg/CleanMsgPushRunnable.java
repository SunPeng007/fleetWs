package com.mt.system.domain.thread.msg;

import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.constant.ConnectTimeConstant;
import com.mt.system.domain.constant.PantNumberConstant;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.MtSession;
import com.mt.system.domain.entity.msg.PushMessage;
import com.mt.system.websocket.msg.MtMsgContainerUtil;
import com.mt.system.websocket.msg.MtMsgWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.websocket.Session;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: chenpan
 * Date:2020/5/28
 * Time:15:25
 */
public class CleanMsgPushRunnable implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(CleanMsgPushRunnable.class);
    @Override
    public void run() {
        while (true) {
            try{
                /*判断是否需要重发*/
                ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder<PushMessage>>> mtPushMap = MtMsgContainerUtil.getMtMsgPushMap();
                //遍历所有公司
                Iterator<String> companyIter = mtPushMap.keySet().iterator();
                while(companyIter.hasNext()) {
                    String companyId = companyIter.next();//公司id
                    ConcurrentHashMap<String,BaseBuilder<PushMessage>> companySession=mtPushMap.get(companyId);
                    //遍历该公司所有-发送消息
                    Iterator<String> pushIter = companySession.keySet().iterator();
                    while(pushIter.hasNext()) {
                        String key = pushIter.next();//key
                        BaseBuilder<PushMessage> baseBuilder=companySession.get(key);
                        String token = baseBuilder.getReceiveToken();//token
                        MtSession mtSession=MtMsgContainerUtil.getSession(companyId,token);
                        if(mtSession!=null){
                            Session session=mtSession.getSession();
                            //先判断连接是否打开
                            if(!session.isOpen()) {
                                MtMsgContainerUtil.removePush(companyId,key);
                                MtMsgContainerUtil.removeSession(companyId,token);
                                continue;
                            }
                            //判断重发次数是否达到上限
                            if(baseBuilder.getPustNumber()>=PantNumberConstant.PANT_NUMBER_CODE){
                                MtMsgContainerUtil.removePush(companyId,key);
                                MtMsgContainerUtil.removeSession(companyId,token);
                                continue;
                            }
                            //判断是否需要重发
                            if(DateUtils.currentCompare(baseBuilder.getPushTime())>ConnectTimeConstant.ANSWER_TIME_CODE){
                                baseBuilder.setPustNumber((baseBuilder.getPustNumber()+1));
                                MtMsgWebSocketServer.sendMsg(session,companyId,baseBuilder);
                                logger.info("消息重发：" + key);
                            }
                        }
                    }
                }
                Thread.sleep(ConnectTimeConstant.CLOSE_PUSH_TIME_CODE);
            }catch (Exception e) {
                e.printStackTrace();
                logger.error("消息重发异常:"+e);
            }
        }
    }
}
