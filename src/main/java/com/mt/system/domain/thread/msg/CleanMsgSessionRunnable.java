package com.mt.system.domain.thread.msg;

import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.constant.ConnectTimeConstant;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.MtSession;
import com.mt.system.domain.entity.msg.PushMessage;
import com.mt.system.websocket.msg.MtMsgContainerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: chenpan
 * Date:2020/5/28
 * Time:10:16
 */
public class CleanMsgSessionRunnable implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(CleanMsgSessionRunnable.class);
    @Override
    public void run() {
        while (true) {
            try{
                ConcurrentHashMap<String,ConcurrentHashMap<String,MtSession>> mtSessionMap = MtMsgContainerUtil.getMtMsgSessionMap();
                //遍历所有公司
                Iterator<String> companyIter = mtSessionMap.keySet().iterator();
                while(companyIter.hasNext()) {
                    String companyId = companyIter.next();//公司id
                    ConcurrentHashMap<String,MtSession> companySession=mtSessionMap.get(companyId);
                    //遍历该公司所有连接
                    Iterator<String> sessionIter = companySession.keySet().iterator();
                    while(sessionIter.hasNext()) {
                        String token = sessionIter.next();//token
                        MtSession mtSession=companySession.get(token);
                        if (DateUtils.currentCompare(mtSession.getConnectTime()) > ConnectTimeConstant.EFFECTIVE_TIME_CODE) {
                            if (!mtSession.getSession().isOpen()) {//连接是否打开
                                MtMsgContainerUtil.removeSession(companyId, token);
                                logger.info("清除消息连接：" + token);
                            }
                        }
                    }
                }
                Thread.sleep(ConnectTimeConstant.CLOSE_SESSION_TIME_CODE);//睡10分钟
            }catch (Exception e) {
                e.printStackTrace();
                logger.error("清除消息连接异常:"+e);
            }
        }
    }
}
