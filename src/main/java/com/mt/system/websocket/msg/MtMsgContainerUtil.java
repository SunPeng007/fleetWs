package com.mt.system.websocket.msg;

import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.MtSession;

import javax.websocket.Session;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: chenpan
 * Date:2020/5/26
 * Time:15:27
 *  ConcurrentHashMap是线程安全的，而HashMap是线程不安全的
 */
public class MtMsgContainerUtil {
    //记录连接对象(公司-连接对象）
    private static ConcurrentHashMap<String,ConcurrentHashMap<String,MtSession>> mtMsgSessionMap=new ConcurrentHashMap();
    //记录服务器发送消息(公司-消息对象）
    private static ConcurrentHashMap<String,ConcurrentHashMap<String, BaseBuilder>> mtMsgPushMap = new ConcurrentHashMap();
    //记录接收消息(公司-消息对象）
    private static ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder>> mtMsgReceiveMap = new ConcurrentHashMap();
    /*--静态get函数--*/
    public static ConcurrentHashMap<String,ConcurrentHashMap<String,MtSession>> getMtMsgSessionMap(){
        return mtMsgSessionMap;
    }
    public static ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder>> getMtMsgPushMap(){
        return mtMsgPushMap;
    }
    public static ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder>> getMtMsgReceiveMap(){
        return mtMsgReceiveMap;
    }

    /*===================操作连接对象======================*/
    /**
     *  添加连接对象
     * @param companyId
     * @param token
     * @param session
     */
    public static void putSession(String companyId, String token, Session session){
        if(mtMsgSessionMap.get(companyId)!=null){
            mtMsgSessionMap.get(companyId).put(token,new MtSession(session,token, DateUtils.currentTimeMilli()));
        }else{
            ConcurrentHashMap<String,MtSession> mySession = new ConcurrentHashMap<>();
            mySession.put(token,new MtSession(session,token, DateUtils.currentTimeMilli()));
            mtMsgSessionMap.put(companyId,mySession);
        }
    }
    /**
     * 移除连接对象
     * @param companyId
     * @param token
     */
    public static void removeSession(String companyId,String token){
        if(mtMsgSessionMap.get(companyId)!=null){
            mtMsgSessionMap.get(companyId).get(token);
        }
    }
    /**
     * 获取连接对象
     * @param companyId
     * @param token
     * @return
     */
    public static MtSession getSession(String companyId,String token){
        if(mtMsgSessionMap.get(companyId)!=null){
            if(mtMsgSessionMap.get(companyId).get(token)!=null){
                return mtMsgSessionMap.get(companyId).get(token);
            }
        }
        return null;
    }
    /**
     * 获取该公司所有连接对象
     * @param companyId
     * @return
     */
    public static ConcurrentHashMap<String,MtSession> getSession(String companyId){
        if(mtMsgSessionMap.get(companyId)!=null){
            return mtMsgSessionMap.get(companyId);
        }
        return null;
    }


    /*==================接收对象===============*/
    /**
     *  添加接收数据对象
     * @param companyId
     * @param token
     * @param baseBuilder
     */
    public static void putReceive(String companyId,String token,BaseBuilder baseBuilder){
        if(mtMsgReceiveMap.get(companyId)!=null){
                mtMsgReceiveMap.get(companyId).put(token,baseBuilder);
        }else{
            ConcurrentHashMap<String,BaseBuilder> msgReceive=new ConcurrentHashMap();
            msgReceive.put(token,baseBuilder);
            mtMsgReceiveMap.put(companyId,msgReceive);
        }
    }
    /**
     * 获取接收数据对象
     * @param companyId
     * @param token
     * @return
     */
    public static BaseBuilder getReceive(String companyId,String token){
        if(mtMsgReceiveMap.get(companyId)!=null){
            if(mtMsgReceiveMap.get(companyId).get(token)!=null){
                return mtMsgReceiveMap.get(companyId).get(token);
            }
        }
        return null;
    }
    /**
     * 移除接收数据对象
     * @param companyId
     * @param token
     */
    public static void removeReceive(String companyId,String token){
        if(mtMsgReceiveMap.get(companyId)!=null){
            mtMsgReceiveMap.get(companyId).remove(token);
        }
    }

    /*========================操作发送对象================*/
    /**
     *  添加发送数据对象
     * @param companyId
     * @param token
     * @param baseBuilder
     */
    public static void putPush(String companyId,String token,BaseBuilder baseBuilder){
        if(mtMsgPushMap.get(companyId)!=null){
                mtMsgPushMap.get(companyId).put(token,baseBuilder);
        }else{
            ConcurrentHashMap<String,BaseBuilder> connectPush=new ConcurrentHashMap();
            connectPush.put(token,baseBuilder);
            mtMsgPushMap.put(companyId,connectPush);
        }
    }
    /**
     * 获取接收数据对象
     * @param companyId
     * @param token
     * @return
     */
    public static BaseBuilder getPush(String companyId,String token){
        if(mtMsgPushMap.get(companyId)!=null){
            if(mtMsgPushMap.get(companyId).get(token)!=null){
                return mtMsgPushMap.get(companyId).get(token);
            }
        }
        return null;
    }
    /**
     *  移除发送数据对象
     * @param companyId
     * @param token
     */
    public static void removePush(String companyId,String token){
        if(mtMsgPushMap.get(companyId)!=null){
            mtMsgPushMap.get(companyId).remove(token);
        }
    }

}
