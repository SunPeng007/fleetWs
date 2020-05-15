package com.mt.system.websocket;

import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.MtSession;
import javax.websocket.Session;
import java.util.Hashtable;
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
    private static Hashtable<String,Hashtable<String,Hashtable<String,MtSession>>> mtSessionMap=new Hashtable();
    //记录服务器发送消息
    private static Hashtable<String,Hashtable<String,Hashtable<String,BaseBuilder>>> mtPushMap = new Hashtable();
    //记录接收消息
    private static Hashtable<String,Hashtable<String,Hashtable<String,BaseBuilder>>> mtReceiveMap = new Hashtable();

    public static Hashtable<String,Hashtable<String,Hashtable<String,MtSession>>> getMtSessionMap(){
        return mtSessionMap;
    }
    public static Hashtable<String,Hashtable<String,Hashtable<String,BaseBuilder>>> getMtPushMap(){
        return mtPushMap;
    }
    public static Hashtable<String,Hashtable<String,Hashtable<String,BaseBuilder>>> getMtReceiveMap(){
        return mtReceiveMap;
    }

    /*以下工具方法*/
    /*==================接收对象===============*/
    /**
     *  添加接收数据对象
     * @param companyId
     * @param groupId
     * @param token
     * @param baseBuilder
     */
    public static void mtReceiveMapPut(String companyId,String groupId,String token,BaseBuilder baseBuilder){
        if(mtReceiveMap.get(companyId)!=null){
            if(mtReceiveMap.get(companyId).get(groupId)!=null){
                mtReceiveMap.get(companyId).get(groupId).put(token,baseBuilder);
            }else{
                Hashtable<String,BaseBuilder> msgPush=new Hashtable();
                msgPush.put(token,baseBuilder);
                mtReceiveMap.get(companyId).put(groupId,msgPush);
            }
        }else{
            //消息对象
            Hashtable<String,BaseBuilder> msgPush=new Hashtable();
            msgPush.put(token,baseBuilder);
            //群对象
            Hashtable<String,Hashtable<String,BaseBuilder>> groupPush = new Hashtable<>();
            groupPush.put(groupId,msgPush);
            //连接成功-加人
            mtReceiveMap.put(companyId,groupPush);
        }
    }
    /**
     * 获取接收数据对象
     * @param companyId
     * @param groupId
     * @param token
     * @return
     */
    public static BaseBuilder getMtReceiveMap(String companyId,String groupId,String token){
        if(mtReceiveMap.get(companyId)!=null){
            if(mtReceiveMap.get(companyId).get(groupId)!=null){
                if(mtReceiveMap.get(companyId).get(groupId).get(token)!=null){
                    return mtReceiveMap.get(companyId).get(groupId).get(token);
                }
            }
        }
        return null;
    }
    /**
     * 移除接收数据对象
     * @param companyId
     * @param groupId
     * @param token
     */
    public static void mtReceiveMapRemove(String companyId,String groupId,String token){
        if(mtReceiveMap.get(companyId)!=null){
            if(mtReceiveMap.get(companyId).get(groupId)!=null){
                mtReceiveMap.get(companyId).get(groupId).remove(token);
            }
        }
    }


    /*========================操作发送对象================*/
    /**
     *  添加发送数据对象
     * @param companyId
     * @param groupId
     * @param token
     * @param baseBuilder
     */
    public static void mtPushMapPut(String companyId,String groupId,String token,BaseBuilder baseBuilder){
        if(mtPushMap.get(companyId)!=null){
            if(mtPushMap.get(companyId).get(groupId)!=null){
                mtPushMap.get(companyId).get(groupId).put(token,baseBuilder);
            }else{
                Hashtable<String,BaseBuilder> connectPush=new Hashtable();
                connectPush.put(token,baseBuilder);
                mtPushMap.get(companyId).put(groupId,connectPush);
            }
        }else{
            //连接对象
            Hashtable<String,BaseBuilder> connectPush=new Hashtable();
            connectPush.put(token,baseBuilder);
            //群对象
            Hashtable<String,Hashtable<String,BaseBuilder>> groupPush = new Hashtable<>();
            groupPush.put(groupId,connectPush);
            //连接成功-加人
            mtPushMap.put(companyId,groupPush);
        }
    }
    /**
     * 获取接收数据对象
     * @param companyId
     * @param groupId
     * @param token
     * @return
     */
    public static BaseBuilder getMtPushMap(String companyId,String groupId,String token){
        if(mtPushMap.get(companyId)!=null){
            if(mtPushMap.get(companyId).get(groupId)!=null){
                if(mtPushMap.get(companyId).get(groupId).get(token)!=null){
                    return mtPushMap.get(companyId).get(groupId).get(token);
                }
            }
        }
        return null;
    }
    /**
     *  移除发送数据对象
     * @param companyId
     * @param groupId
     * @param token
     */
    public static void mtPushRemove(String companyId,String groupId,String token){
        if(mtPushMap.get(companyId)!=null){
            if(mtPushMap.get(companyId).get(groupId)!=null){
                mtPushMap.get(companyId).get(groupId).remove(token);
            }
        }
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
                Hashtable<String,MtSession> connectSession=new Hashtable();
                connectSession.put(token,new MtSession(session,token,DateUtils.currentTimeMilli()));
                mtSessionMap.get(companyId).put(groupId,connectSession);
            }
        }else{
            //连接对象
            Hashtable<String,MtSession> connectSession=new Hashtable();
            connectSession.put(token,new MtSession(session,token,DateUtils.currentTimeMilli()));
            //群对象
            Hashtable<String,Hashtable<String,MtSession>> groupSession = new Hashtable<>();
            groupSession.put(groupId,connectSession);
            //连接成功-加人
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
    public static Hashtable<String,MtSession> getMtSessionMap(String companyId,String groupId){
        if(mtSessionMap.get(companyId)!=null){
            if(mtSessionMap.get(companyId).get(groupId)!=null){
                return mtSessionMap.get(companyId).get(groupId);
            }
        }
        return null;
    }
}
