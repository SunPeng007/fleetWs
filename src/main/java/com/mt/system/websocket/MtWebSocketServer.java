package com.mt.system.websocket;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/3/5
 * Time:16:12
 */
/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 * 使用websocket的核心，就是一系列的websocket注解，@ServerEndpoint是注册在类上面开启。
 */
@Component
@ServerEndpoint(value = "/mtwebsocket")
public class MtWebSocketServer {
    private static Logger logger = LoggerFactory.getLogger(MtWebSocketServer.class);
    //private static int onlineCount = 0;
    //ConcurrentHashMap是线程安全的，而HashMap是线程不安全的。
    private static ConcurrentHashMap<String,Session> mapUS = new ConcurrentHashMap<String,Session>();
    private static ConcurrentHashMap<Session,String> mapSU = new ConcurrentHashMap<Session,String>();

    //连接建立成功调用的方法
    @OnOpen
    public void onOpen(Session session,@PathParam("userId") Integer userId) {
        String jsonString="{'content':'online','id':"+userId+",'type':'onlineStatus'}";
        for (String value : mapSU.values()) {
            try {
                mapUS.get(value).getBasicRemote().sendText(jsonString);
            } catch (IOException e) {
                logger.error("发生异常："+e);
            }
        }
        mapUS.put(userId+"",session);
        mapSU.put(session,userId+"");
        //更新redis中的用户在线状态
        //RedisUtils.set(userId+"_status","online");
        logger.info("用户"+userId+"进入llws,当前在线人数为" + mapUS.size() );

    }

    //连接关闭调用的方法
    @OnClose
    public void onClose(Session session) {
        String userId=mapSU.get(session);
        if(userId!=null&&userId!=""){
            //更新redis中的用户在线状态
            //RedisUtils.set(userId+"_status","offline");
            mapUS.remove(userId);
            mapSU.remove(session);
            logger.info("用户"+userId+"退出llws,当前在线人数为" + mapUS.size());
        }
    }

    // 收到客户端消息后调用的方法
    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject jsonObject=JSONObject.fromObject(message);
        String type = jsonObject.getJSONObject("to").getString("type");
        for(Session s:session.getOpenSessions()){		//循环发给所有在线的人
            JSONObject toMessage=new JSONObject();
            toMessage.put("id", jsonObject.getJSONObject("mine").getString("id"));
            toMessage.put("content", jsonObject.getJSONObject("mine").getString("content"));
            toMessage.put("type",type);
            for (String value : mapSU.values()) {
                try {
                    mapUS.get(value).getBasicRemote().sendText(toMessage.toString());
                } catch (IOException e) {
                    logger.error("发生异常："+e);
                }
            }
        }
    }

    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        String userId=mapSU.get(session);
        if(userId!=null&&userId!=""){
            //更新redis中的用户在线状态
            //RedisUtils.set(userId+"_status","offline");
            mapUS.remove(userId);
            mapSU.remove(session);
            logger.info("用户"+userId+"退出llws！当前在线人数为" + mapUS.size());
        }
        logger.error("llws发生错误!");
        error.printStackTrace();
    }
}
