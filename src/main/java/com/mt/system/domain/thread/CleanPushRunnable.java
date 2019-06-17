package com.mt.system.domain.thread;

import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.constant.ConnectTimeConstant;
import com.mt.system.domain.constant.PantNumberConstant;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.MtSession;
import com.mt.system.websocket.MtContainerUtil;
import com.mt.system.websocket.MtWebSocketServer;
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
    // 创建一个静态钥匙
    private static Object mtPushKey = "MoTooling";
    @Override
    public void run() {
        while (true) {
            synchronized(mtPushKey){
                try{
                    ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder>>> mtPushMap = MtContainerUtil.getMtPushMap();
                    ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String,MtSession>>> mtSessionMap = MtContainerUtil.getMtSessionMap();
                    /*判断是否需要重发*/
                    Iterator<String> comIter = mtPushMap.keySet().iterator();
                    while(comIter.hasNext()) {
                        String companyId = comIter.next();//公司id
                        ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder>> groupSession=mtPushMap.get(companyId);
                        Iterator<String> groupIter = groupSession.keySet().iterator();
                        while(comIter.hasNext()) {
                            String groupId = comIter.next();//群id
                            ConcurrentHashMap<String,BaseBuilder> contSession =groupSession.get(groupId);
                            for (BaseBuilder baseBuilder : contSession.values()) {
                                String token = baseBuilder.getReceiveToken();//token
                                Session session=MtContainerUtil.getMtSessionMap(companyId,groupId,token).getSession();
                                String keyStr=token+baseBuilder.getSerialNumber();
                                //先判断连接是否打开
                                if(!session.isOpen()) {
                                    MtContainerUtil.mtPushRemove(companyId,groupId,keyStr);
                                    MtContainerUtil.mtSessionMapRemove(companyId,groupId,token);
                                    continue;
                                }
                                //判断重发次数是否达到上限
                                if(baseBuilder.getPustNumber()>=PantNumberConstant.PANT_NUMBER_CODE){
                                    MtContainerUtil.mtPushRemove(companyId,groupId,keyStr);
                                    MtContainerUtil.mtSessionMapRemove(companyId,groupId,token);
                                    continue;
                                }
                                //判断是否需要重发
                                if(DateUtils.currentCompare(baseBuilder.getPushTime())>ConnectTimeConstant.ANSWER_TIME_CODE){
                                    baseBuilder.setPustNumber((baseBuilder.getPustNumber()+1));
                                    MtWebSocketServer.mtSendText(session,companyId,groupId,baseBuilder);
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
}