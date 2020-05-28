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
                ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder<PushMessage>>> mtPushMap = MtMsgContainerUtil.getMtMsgPushMap();
                /*判断是否需要重发*/
                Iterator<String> comIter = mtPushMap.keySet().iterator();
                while(comIter.hasNext()) {
                    String companyId = comIter.next();//公司id
                    ConcurrentHashMap<String,BaseBuilder<PushMessage>> companySession=mtPushMap.get(companyId);
                    for (BaseBuilder<PushMessage> baseBuilder : companySession.values()) {
                        String token = baseBuilder.getReceiveToken();//token
                        MtSession mtSession=MtMsgContainerUtil.getSession(companyId,token);
                        if(mtSession!=null){
                            Session session=mtSession.getSession();
                            String keyStr=token+baseBuilder.getSerialNumber();
                            //先判断连接是否打开
                            if(!session.isOpen()) {
                                MtMsgContainerUtil.removePush(companyId,keyStr);
                                MtMsgContainerUtil.removeSession(companyId,token);
                                continue;
                            }
                            //判断重发次数是否达到上限
                            if(baseBuilder.getPustNumber()>=PantNumberConstant.PANT_NUMBER_CODE){
                                MtMsgContainerUtil.removePush(companyId,keyStr);
                                MtMsgContainerUtil.removeSession(companyId,token);
                                continue;
                            }
                            //判断是否需要重发
                            if(DateUtils.currentCompare(baseBuilder.getPushTime())>ConnectTimeConstant.ANSWER_TIME_CODE){
                                baseBuilder.setPustNumber((baseBuilder.getPustNumber()+1));
                                MtMsgWebSocketServer.sendMsg(session,companyId,baseBuilder);
                                logger.info(token+"消息重发!");
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
