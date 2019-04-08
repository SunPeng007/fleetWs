package com.mt.system.websocket;

import com.mt.system.common.code.HttpClientTool;
import com.mt.system.common.util.JsonUtil;
import com.mt.system.domain.constant.AsyncUrlConstant;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArraySet;

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
@ServerEndpoint(value = "/synergy/websocket")
public class SynergyWebSocketServer {
    private static Logger logger = LoggerFactory.getLogger(SynergyWebSocketServer.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<SynergyWebSocketServer> webSocketSet = new CopyOnWriteArraySet<SynergyWebSocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        logger.info("有新连接加入！当前在线人数为" + getOnlineCount());
        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            logger.error("websocket IO异常");
        }
    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        logger.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }
    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("来自客户端的消息:" + message);
        //客户端传过来数据
        HashMap<String,Object> jsonMap = JsonUtil.toObject(message,new HashMap<String,Object>().getClass());
        //站点添加聊天记录-URL
        String webUrl=jsonMap.get("webUrl").toString();
        jsonMap.remove("webUrl");
        JSONObject resultJson = HttpClientTool.mtHttpPost(jsonMap,webUrl+AsyncUrlConstant.SYNERGY_GROUP_RECORD_URL);
        if (resultJson == null) {
            throw new IllegalArgumentException("同步数据失败!");
        }
        //群发消息
        for (SynergyWebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 发送异常调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("发生错误");
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
    /**
     * 群发自定义消息
     * */
    public static void sendInfo(String message) throws IOException {
        logger.info(message);
        for (SynergyWebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        SynergyWebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        SynergyWebSocketServer.onlineCount--;
    }
}
