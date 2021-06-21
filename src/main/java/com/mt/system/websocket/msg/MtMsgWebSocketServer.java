package com.mt.system.websocket.msg;

import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.mt.system.common.util.DateUtils;
import com.mt.system.domain.constant.TypeConstant;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.MtSession;
import com.mt.system.domain.entity.msg.PushMessage;
import com.mt.system.domain.entity.msg.ReceiveMessage;
import com.mt.system.domain.entity.msg.UserMessage;

/**
 * Created with IDEA author: chenpan Date:2020/5/25
 * 
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端, 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 *                 使用websocket的核心，就是一系列的websocket注解，@ServerEndpoint是注册在类上面开启。
 */
@Component
@ServerEndpoint(value = "/MtMsgWebSocket/{companyId}/{type}/{uid}")
public class MtMsgWebSocketServer {

    /**
     * 连接建立成功调用的方法
     * 
     * @param session
     * @param companyId
     * @param type
     * @param uid
     * @throws Exception
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("companyId") String companyId, @PathParam("type") String type,
        @PathParam("uid") String uid) {
        String token = type + "_" + uid;
        MtMsgContainerUtil.putSession(companyId, token, session);
    }

    /**
     * 连接关闭调用的方法
     * 
     * @param session
     * @param companyId
     * @param type
     * @param uid
     * @throws Exception
     */
    @OnClose
    public void onClose(Session session, @PathParam("companyId") String companyId, @PathParam("type") String type,
        @PathParam("uid") String uid) {
        String token = type + "_" + uid;
        MtMsgContainerUtil.removeSession(companyId, token);
    }

    /**
     * 发生错误时调用
     * 
     * @param session
     * @param error
     * @param companyId
     * @param type
     * @param uid
     * @throws Exception
     */
    @OnError
    public void onError(Session session, Throwable error, @PathParam("companyId") String companyId,
        @PathParam("type") String type, @PathParam("uid") String uid) {
        String token = type + "_" + uid;
        MtMsgContainerUtil.removeSession(companyId, token);
    }

    /**
     * 收到客户端消息后调用的方法
     * 
     * @param message
     * @param session
     * @param companyId
     * @param type
     * @param uid
     * @throws Exception
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("companyId") String companyId,
        @PathParam("type") String type, @PathParam("uid") String uid) throws Exception {
        String token = type + "_" + uid;
        // 更新当前连接时间
        MtSession mtSession = MtMsgContainerUtil.getSession(companyId, token);
        if (mtSession != null) {
            mtSession.setConnectTime(DateUtils.currentTimeMilli());
        } else {
            MtMsgContainerUtil.putSession(companyId, token, session);
        }
        // 接收数据，-- 调用企业站点接口添加记录
        BaseBuilder<ReceiveMessage> reqEntity =
            JSON.parseObject(message, new TypeReference<BaseBuilder<ReceiveMessage>>() {});
        // key
        String keyStr = token + reqEntity.getSerialNumber();
        // 判断回应类型
        if (TypeConstant.REQUEST_PING_TYPE.equals(reqEntity.getRequestType())) {
            // ping-客户端心跳，不做任何操作
        } else if (TypeConstant.REQUEST_RESPONSE_TYPE.equals(reqEntity.getRequestType())) {
            // 服务器发送消息，客户端回应
            // 移除-服务器发送消息
            MtMsgContainerUtil.removePush(companyId, keyStr);
        } else {
            // 客户端向服务端发送消息
            // 判断该消息是否接收过
            BaseBuilder<ReceiveMessage> builder = MtMsgContainerUtil.getReceive(companyId, keyStr);
            if (builder == null) {
                reqEntity.setPushTime(DateUtils.currentTimeMilli());
                reqEntity.setPustToken(token);// 发送人token
                // 添加接收消息
                MtMsgContainerUtil.putReceive(companyId, keyStr, reqEntity);
                /*接收到客户端信息-服务端推消息给用户*/
                servicePushUser(reqEntity, companyId, token);
            }
        }
    }

    /**
     * 接收到客户端信息-服务端推消息给用户
     * 
     * @param reqEntity
     * @param companyId
     * @param token
     * @throws Exception
     */
    public static void servicePushUser(BaseBuilder<ReceiveMessage> reqEntity, String companyId, String token)
        throws Exception {
        List<UserMessage> userMessageList = reqEntity.getData().getUserMsgList();
        if (userMessageList != null && userMessageList.size() > 0) {
            for (UserMessage userMessage : userMessageList) {
                // 构造发生消息对象
                PushMessage pushMessage = new PushMessage();
                pushMessage.setMsgCount(userMessage.getMsgCount());
                pushMessage.setMsgData(reqEntity.getData().getMsgData());
                pushMessage.setMsgType(reqEntity.getData().getMsgType());
                String uuid = "serverMsg_" + java.util.UUID.randomUUID().toString();// 生成uuid 作为流水号
                BaseBuilder<PushMessage> pushNews = new BaseBuilder(uuid, "服务器推送消息!", pushMessage);
                pushNews.setResponseType(TypeConstant.RESPONSE_PUSH_TYPE); // 设置响应类型
                pushNews.setPustNumber(1);// 发送次数
                List<String> typeList = MtMsgContainerUtil.getMtMsgTypeList();
                for (String type : typeList) {
                    String receiveToken = type + "_" + userMessage.getUid();
                    MtSession mtSes = MtMsgContainerUtil.getSession(companyId, receiveToken);
                    if (mtSes != null && mtSes.getSession().isOpen()) {
                        sendMsg(mtSes.getSession(), companyId, pushNews);
                        // 记录发送消息给谁
                        BaseBuilder<PushMessage> resEntity = pushNews.clone();
                        addMtEcho(resEntity, 1, token, companyId, receiveToken);
                    }
                }
            }
            // 不是后台通过http接口调用。需要告诉发送者，服务端已经收到消息。
            if (!reqEntity.getRequestType().equals(TypeConstant.REQUEST_SERVICE_TYPE)) {
                MtSession mtSesssion = MtMsgContainerUtil.getSession(companyId, token);
                // 给当前连接发消息提示成功
                BaseBuilder<PushMessage> resultUs = reqEntity.clone();
                resultUs.setMsg("响应客户端消息!");
                resultUs.setResponseType(TypeConstant.RESPONSE_SUCCESS_TYPE);// 设置响应类型
                sendMsg(mtSesssion.getSession(), companyId, resultUs);
            }
        }
    }

    /**
     * 记录发送消息给谁
     * 
     * @param resEntity
     * @param pustNumber
     * @param pustToken
     * @param receiveToken
     */
    private static void addMtEcho(BaseBuilder<PushMessage> resEntity, int pustNumber, String pustToken,
        String companyId, String receiveToken) {
        resEntity.setPustNumber(pustNumber);
        resEntity.setPustToken(pustToken);
        resEntity.setReceiveToken(receiveToken);
        resEntity.setPushTime(DateUtils.currentTimeMilli());
        String keyStr = receiveToken + resEntity.getSerialNumber();
        MtMsgContainerUtil.putPush(companyId, keyStr, resEntity);
    }

    /**
     * 发送消息
     * 
     * @param session
     * @param baseBuilder
     */
    public static void sendMsg(Session session, String companyId, BaseBuilder<PushMessage> baseBuilder) {
        try {
            if (session != null) {
                session.getBasicRemote().sendText(JSONObject.toJSONString(baseBuilder));
            }
        } catch (Exception e) {
            // 判断发送次数
            if (baseBuilder.getPustNumber() == null) {
                baseBuilder.setPustNumber(0);
            }
            String keyStr = baseBuilder.getReceiveToken() + baseBuilder.getSerialNumber();
            MtMsgContainerUtil.putPush(companyId, keyStr, baseBuilder);
        }
    }

}
