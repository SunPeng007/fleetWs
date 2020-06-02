package com.mt.system.domain.thread.im;

import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.constant.ConnectTimeConstant;
import com.mt.system.domain.entity.MtSession;
import com.mt.system.websocket.im.MtContainerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/4/16
 * Time:10:16
 */
public class CleanSessionRunnable implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(CleanSessionRunnable.class);
    @Override
    public void run() {
        while (true) {
            try{
                ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String,MtSession>>> mtSessionMap = MtContainerUtil.getMtSessionMap();
                //遍历所有公司
                Iterator<String> companyIter = mtSessionMap.keySet().iterator();
                while(companyIter.hasNext()) {
                    String companyId = companyIter.next();//公司id
                    ConcurrentHashMap<String, ConcurrentHashMap<String, MtSession>> groupSession = mtSessionMap.get(companyId);
                    //遍历所有群
                    Iterator<String> groupIter = groupSession.keySet().iterator();
                    while(groupIter.hasNext()) {
                        String groupId = groupIter.next();//群id
                        ConcurrentHashMap<String,MtSession> contSession=groupSession.get(groupId);
                        //遍历所有连接
                        Iterator<String> contIter = contSession.keySet().iterator();
                        while(contIter.hasNext()) {
                            String key = contIter.next();//key
                            MtSession mtSession=contSession.get(key);
                            if (DateUtils.currentCompare(mtSession.getConnectTime()) > ConnectTimeConstant.EFFECTIVE_TIME_CODE) {
                                if (!mtSession.getSession().isOpen()) {//连接是否打开
                                    MtContainerUtil.mtSessionMapRemove(companyId,groupId,key);
                                    logger.info("协同清除连接：" + key);
                                }
                            }
                        }
                    }
                }
                Thread.sleep(ConnectTimeConstant.CLOSE_SESSION_TIME_CODE);//睡10分钟
            }catch (Exception e) {
                e.printStackTrace();
                logger.error("协同清除连接异常:"+e);
            }
        }
    }
}
