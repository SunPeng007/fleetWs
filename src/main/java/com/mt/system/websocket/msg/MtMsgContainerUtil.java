package com.mt.system.websocket.msg;

import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.MtSession;
import java.util.concurrent.ConcurrentHashMap;

public class MtMsgContainerUtil {
    //记录连接对象(公司-连接对象）
    private static ConcurrentHashMap<String,ConcurrentHashMap<String,MtSession>> mtMsgSessionMap=new ConcurrentHashMap();
    //记录服务器发送消息(公司-消息对象）
    private static ConcurrentHashMap<String,ConcurrentHashMap<String, BaseBuilder>> mtMsgPushMap = new ConcurrentHashMap();
    //记录接收消息(公司-消息对象）
    private static ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder>> mtMsgReceiveMap = new ConcurrentHashMap();

}
