package com.mt.system.fleet.websocket;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.mt.system.common.util.DateUtils;
import com.mt.system.fleet.entity.socket.FleetSession;
import com.mt.system.fleet.entity.socket.Msg;
import com.mt.system.fleet.enums.RequestTypeEnum;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;

@ServerEndpoint("/msgSocket/{uid}")
@Component
public class MsgServer {

    private static Log log = LogFactory.get();

    /** 记录当前在线连接数 */
    public static AtomicInteger onlineCount = new AtomicInteger(0);

    public static ConcurrentHashMap<String, FleetSession> sessionMap = new ConcurrentHashMap<String, FleetSession>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid) {

        FleetSession fSession = new FleetSession();
        fSession.setUid(uid);
        fSession.setSession(session);
        fSession.setLastPingTime(DateUtils.currentTimeMilli());
        sessionMap.put(uid, fSession);
        onlineCount.incrementAndGet(); // 在线数加1
        log.info("有新连接加入，当前在线人数为：{}", onlineCount.get());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("uid") String uid) {
        sessionMap.remove(uid);
        onlineCount.decrementAndGet(); // 在线数减1
        log.info("有一连接关闭，当前在线人数为：{}", onlineCount.get());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     *            客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(Session session, @PathParam("uid") String uid, String message) {
        // Msg msg1 = JSON.parseObject(JSONObject.toJSONString(message), Msg.class);
        // if (RequestTypeEnum.PING.equals(msg1.getReqType())) {
        if ("PING".equals(message) && sessionMap.get(uid) != null) {
            sessionMap.get(uid).setLastPingTime(DateUtils.currentTimeMilli());
            pingMsg(sessionMap.get(uid));
        }

    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    public static void notifyAllUser(Msg msg) {
        if (RequestTypeEnum.NOTIFY.equals(msg.getReqType())) {
            log.info("服务端收到管理员[{}]的消息:{}", msg.getUid(), msg.toString());
            for (FleetSession fSession : sessionMap.values()) {
                if (fSession != null && fSession.getSession().isOpen()) {
                    sendMessage(msg, fSession);
                }
            }
        }
    }

    public static void notifyUserList(List<String> uIdList, Msg msg) {
        if (RequestTypeEnum.NOTIFY.equals(msg.getReqType())) {
            for (String uid : uIdList) {
                FleetSession fleetSession = sessionMap.get(uid);
                if (fleetSession != null) {
                    sendMessage(msg, fleetSession);
                }
            }
        }
    }

    /**
     * 服务端发送消息给客户端
     */
    private static void sendMessage(Msg msg, FleetSession fSession) {
        try {
            fSession.getSession().getBasicRemote().sendText(msg.getMsgContext());
            msg.setSendCount(msg.getSendCount() + 1);
            log.info("推送给用户{}成功", fSession.getUid());
        } catch (Exception e) {
            log.error("推送给用户{}失败：{}", fSession.getUid(), e);
        }
    }

    private static void pingMsg(FleetSession fSession) {
        try {
            fSession.getSession().getBasicRemote().sendText("PONG");
            log.info("心跳消息：用户{}", fSession.getUid());
        } catch (Exception e) {
            log.error("心跳消息失败：用户{}：{}", fSession.getUid(), e);
        }
    }
}
