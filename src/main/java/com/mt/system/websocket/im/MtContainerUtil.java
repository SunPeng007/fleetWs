package com.mt.system.websocket.im;

import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.MtSession;
import com.mt.system.domain.entity.im.SynergyGroupRecord;

import javax.websocket.Session;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: chenpan
 * Date:2019/5/15
 * Time:15:27
 *  ConcurrentHashMap是线程安全的，而HashMap是线程不安全的
 */
public class MtContainerUtil {
    //记录连接对象(公司-群-连接对象）
    private static ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String,MtSession>>> mtSessionMap=new ConcurrentHashMap();
    //记录服务器发送消息(公司-群-消息对象）
    private static ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>>>> mtPushMap = new ConcurrentHashMap();
    //记录接收消息(公司-群-消息对象）
    private static ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>>>> mtReceiveMap = new ConcurrentHashMap();
    /*--静态get函数--*/
    public static ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String,MtSession>>> getMtSessionMap(){
        return mtSessionMap;
    }
    public static ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>>>> getMtPushMap(){
        return mtPushMap;
    }
    public static ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>>>> getMtReceiveMap(){
        return mtReceiveMap;
    }

    /*===================操作连接对象======================*/
    /**
     *  添加连接对象
     * @param companyId
     * @param groupId
     * @param token
     * @param session
     */
    public static void mtSessionMapPut(String companyId,String groupId,String token,Session session){
        if(mtSessionMap.get(companyId)!=null){
            if(mtSessionMap.get(companyId).get(groupId)!=null){
                mtSessionMap.get(companyId).get(groupId).put(token,new MtSession(session,token,DateUtils.currentTimeMilli()));
            }else{
                ConcurrentHashMap<String,MtSession> connectSession=new ConcurrentHashMap();
                connectSession.put(token,new MtSession(session,token,DateUtils.currentTimeMilli()));
                mtSessionMap.get(companyId).put(groupId,connectSession);
            }
        }else{
            ConcurrentHashMap<String,MtSession> connectSession=new ConcurrentHashMap();
            connectSession.put(token,new MtSession(session,token,DateUtils.currentTimeMilli()));
            ConcurrentHashMap<String,ConcurrentHashMap<String,MtSession>> groupSession = new ConcurrentHashMap<>();
            groupSession.put(groupId,connectSession);
            mtSessionMap.put(companyId,groupSession);
        }
    }
    /**
     * 移除连接对象
     * @param companyId
     * @param groupId
     * @param token
     */
    public static void mtSessionMapRemove(String companyId,String groupId,String token){
        if(mtSessionMap.get(companyId)!=null){
            if(mtSessionMap.get(companyId).get(groupId)!=null){
                mtSessionMap.get(companyId).get(groupId).remove(token);
            }
        }
    }
    /**
     * 获取连接对象
     * @param companyId
     * @param groupId
     * @param token
     * @return
     */
    public static MtSession getMtSessionMap(String companyId,String groupId,String token){
        if(mtSessionMap.get(companyId)!=null){
            if(mtSessionMap.get(companyId).get(groupId)!=null){
                if(mtSessionMap.get(companyId).get(groupId).get(token)!=null){
                    return mtSessionMap.get(companyId).get(groupId).get(token);
                }
            }
        }
        return null;
    }
    /**
     * 获取连接对象
     * @param companyId
     * @param groupId
     * @return
     */
    public static ConcurrentHashMap<String,MtSession> getMtSessionMap(String companyId,String groupId){
        if(mtSessionMap.get(companyId)!=null){
            if(mtSessionMap.get(companyId).get(groupId)!=null){
                return mtSessionMap.get(companyId).get(groupId);
            }
        }
        return null;
    }

    /*==================接收对象===============*/
    /**
     *  添加接收数据对象
     * @param companyId
     * @param groupId
     * @param key
     * @param baseBuilder
     */
    public static void mtReceiveMapPut(String companyId,String groupId,String key,BaseBuilder<SynergyGroupRecord> baseBuilder){
        if(mtReceiveMap.get(companyId)!=null){
            if(mtReceiveMap.get(companyId).get(groupId)!=null){
                mtReceiveMap.get(companyId).get(groupId).put(key,baseBuilder);
            }else{
                ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>> msgReceive=new ConcurrentHashMap();
                msgReceive.put(key,baseBuilder);
                mtReceiveMap.get(companyId).put(groupId,msgReceive);
            }
        }else{
            ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>> msgReceive=new ConcurrentHashMap();
            msgReceive.put(key,baseBuilder);
            ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>>> groupPush = new ConcurrentHashMap<>();
            groupPush.put(groupId,msgReceive);
            mtReceiveMap.put(companyId,groupPush);
        }
    }
    /**
     * 获取接收数据对象
     * @param companyId
     * @param groupId
     * @param key
     * @return
     */
    public static BaseBuilder<SynergyGroupRecord> getMtReceiveMap(String companyId,String groupId,String key){
        if(mtReceiveMap.get(companyId)!=null){
            if(mtReceiveMap.get(companyId).get(groupId)!=null){
                if(mtReceiveMap.get(companyId).get(groupId).get(key)!=null){
                    return mtReceiveMap.get(companyId).get(groupId).get(key);
                }
            }
        }
        return null;
    }
    /**
     * 移除接收数据对象
     * @param companyId
     * @param groupId
     * @param key
     */
    public static void mtReceiveMapRemove(String companyId,String groupId,String key){
        if(mtReceiveMap.get(companyId)!=null){
            if(mtReceiveMap.get(companyId).get(groupId)!=null){
                mtReceiveMap.get(companyId).get(groupId).remove(key);
            }
        }
    }

    /*========================操作发送对象================*/
    /**
     *  添加发送数据对象
     * @param companyId
     * @param groupId
     * @param key
     * @param baseBuilder
     */
    public static void mtPushMapPut(String companyId,String groupId,String key,BaseBuilder<SynergyGroupRecord> baseBuilder){
        if(mtPushMap.get(companyId)!=null){
            if(mtPushMap.get(companyId).get(groupId)!=null){
                mtPushMap.get(companyId).get(groupId).put(key,baseBuilder);
            }else{
                ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>> connectPush=new ConcurrentHashMap();
                connectPush.put(key,baseBuilder);
                mtPushMap.get(companyId).put(groupId,connectPush);
            }
        }else{
            ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>> connectPush=new ConcurrentHashMap();
            connectPush.put(key,baseBuilder);
            ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder<SynergyGroupRecord>>> groupPush = new ConcurrentHashMap<>();
            groupPush.put(groupId,connectPush);
            mtPushMap.put(companyId,groupPush);
        }
    }
    /**
     * 获取接收数据对象
     * @param companyId
     * @param groupId
     * @param key
     * @return
     */
    public static BaseBuilder<SynergyGroupRecord> getMtPushMap(String companyId,String groupId,String key){
        if(mtPushMap.get(companyId)!=null){
            if(mtPushMap.get(companyId).get(groupId)!=null){
                if(mtPushMap.get(companyId).get(groupId).get(key)!=null){
                    return mtPushMap.get(companyId).get(groupId).get(key);
                }
            }
        }
        return null;
    }
    /**
     *  移除发送数据对象
     * @param companyId
     * @param groupId
     * @param key
     */
    public static void mtPushRemove(String companyId,String groupId,String key){
        if(mtPushMap.get(companyId)!=null){
            if(mtPushMap.get(companyId).get(groupId)!=null){
                mtPushMap.get(companyId).get(groupId).remove(key);
            }
        }
    }

}
