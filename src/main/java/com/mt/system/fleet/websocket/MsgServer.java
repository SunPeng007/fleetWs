package com.mt.system.fleet.websocket;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mt.system.common.util.DateUtils;
import com.mt.system.fleet.common.ApplicationContextHolder;
import com.mt.system.fleet.entity.socket.FleetSession;
import com.mt.system.fleet.entity.socket.Msg;
import com.mt.system.fleet.enums.RequestTypeEnum;
import com.mt.system.fleet.util.FleetHttpClient;

@ServerEndpoint(value = "/msgSocket/{siteCode}/{verifyCode}")
@Component
public class MsgServer {

    private static Logger log = LoggerFactory.getLogger(MsgServer.class);

    public static final String SITE_ADMIN_CODE = "admin";
    public static final String KEY_CONNECTOR = "_";

    private FleetHttpClient fleetHttpClient = (FleetHttpClient)ApplicationContextHolder.getBean("fleetHttpClient");

    public static ConcurrentHashMap<String, FleetSession> sessionMap = new ConcurrentHashMap<String, FleetSession>();
    public static ConcurrentHashMap<String, String> sessionIdMap = new ConcurrentHashMap<String, String>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("siteCode") String siteCode,
        @PathParam("verifyCode") String verifyCode) {

        String uid = getKey(siteCode, verifyCode);
        if (uid == null) {
            // onClose(session);
            return;
        }

        FleetSession fSession = new FleetSession();
        fSession.setUid(uid);
        fSession.setSession(session);
        fSession.setLastPingTime(DateUtils.currentTimeMilli());
        sessionMap.put(uid, fSession);
        sessionIdMap.put(session.getId(), uid);
        log.info("有新连接加入，key：{}", uid);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        String key = sessionIdMap.get(session.getId());
        if (key == null) {
            // session = null;
            return;
        }
        sessionMap.remove(key);
        sessionIdMap.remove(session.getId());
        // log.info("有一连接关闭，当前在线人数为：{}", sessionMap.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     *            客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(Session session, @PathParam("siteCode") String siteCode,
        @PathParam("verifyCode") String verifyCode, String message) {

        String uid = getKey(siteCode, verifyCode);
        if (uid == null) {
            // onClose(session);
            return;
        }
        if ("PING".equals(message) && sessionMap.get(uid) != null) {
            sessionMap.get(uid).setLastPingTime(DateUtils.currentTimeMilli());
            pingMsg(sessionMap.get(uid));
        }

    }

    @OnError
    public void onError(Session session, Throwable error) {
        // session = null;
        log.error(error.toString());
        error.printStackTrace();
    }

    public static void notifyAllUser(Msg msg) {
        if (RequestTypeEnum.NOTIFY.equals(msg.getReqType())) {
            // log.info("服务端收到管理员[{}]的消息:{}", msg.getUid(), msg.toString());
            for (FleetSession fSession : sessionMap.values()) {
                if (fSession != null && fSession.getSession().isOpen()) {
                    sendMessage(msg, fSession);
                }
            }
        }
    }

    public static void notifyUserList(List<String> keyList, Msg msg) {
        if (RequestTypeEnum.NOTIFY.equals(msg.getReqType())) {
            for (String key : keyList) {
                FleetSession fleetSession = sessionMap.get(key);
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
            fSession.getSession().getBasicRemote().sendText(msg.getMsgTitle());
            msg.setSendCount(msg.getSendCount() + 1);
            // log.info("推送给用户{}成功", fSession.getUid());
        } catch (Exception e) {
            log.error("推送给用户{}失败：{}", fSession.getUid(), e);
        }
    }

    private static void pingMsg(FleetSession fSession) {
        try {
            fSession.getSession().getBasicRemote().sendText("PONG");
            // log.info("心跳消息：用户{}", fSession.getUid());
        } catch (Exception e) {
            log.error("心跳消息失败：用户{}：{}", fSession.getUid(), e);
        }
    }

    private String getKey(String siteCode, String verifyCode) throws IllegalArgumentException {
        String uid = null;
        if (Objects.equals(siteCode, SITE_ADMIN_CODE)) {
            Map<?, ?> map = fleetHttpClient.getAdminUser(verifyCode);
            if (map == null) {
                // throw new IllegalArgumentException("非法参数");
                log.error("非法参数：{}", verifyCode);
                return null;
            }
            uid = SITE_ADMIN_CODE + KEY_CONNECTOR + map.get("id");
        } else {
            // TODO 验证token 获取站点地址
            uid = siteCode + KEY_CONNECTOR + verifyCode;
        }
        if (uid == null) {
            log.error("非法参数：{}", verifyCode);
            // throw new IllegalArgumentException("非法参数:");
        }
        return uid;
    }
}
