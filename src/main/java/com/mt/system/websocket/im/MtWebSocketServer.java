package com.mt.system.websocket.im;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import com.mt.system.common.util.BeanToMapUtil;
import com.mt.system.common.util.DateUtils;
import com.mt.system.common.util.HttpRequestUtils;
import com.mt.system.domain.constant.AsyncUrlConstant;
import com.mt.system.domain.constant.TypeConstant;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.MtSession;
import com.mt.system.domain.entity.im.SynergyGroupRecord;

/**
 * Created with IDEA author: chenpan Date:2019/4/10
 * 
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端, 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 *                 使用websocket的核心，就是一系列的websocket注解，@ServerEndpoint是注册在类上面开启。
 */
@Component
@ServerEndpoint(value = "/mtwebsocket/{companyId}/{groupId}/{token}/{webUrl}")
public class MtWebSocketServer {
    /**
     * 连接建立成功调用的方法
     * 
     * @param session
     * @param token
     * @throws Exception
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("companyId") String companyId, @PathParam("token") String token,
        @PathParam("groupId") String groupId) {
        MtContainerUtil.mtSessionMapPut(companyId, groupId, token, session);
    }

    /**
     * 连接关闭调用的方法
     * 
     * @param session
     * @param token
     * @throws Exception
     */
    @OnClose
    public void onClose(Session session, @PathParam("companyId") String companyId, @PathParam("groupId") String groupId,
        @PathParam("token") String token) {
        // 移除当前连接
        MtContainerUtil.mtSessionMapRemove(companyId, groupId, token);
    }

    /**
     * 发生错误时调用
     * 
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error, @PathParam("companyId") String companyId,
        @PathParam("groupId") String groupId, @PathParam("token") String token) {
        // 移除当前连接
        MtContainerUtil.mtSessionMapRemove(companyId, groupId, token);
    }

    /**
     * 收到客户端消息后调用的方法
     * 
     * @param message
     * @param companyId
     * @param groupId
     * @param token
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("companyId") String companyId,
        @PathParam("groupId") String groupId, @PathParam("token") String token, @PathParam("webUrl") String webUrl)
        throws Exception {
        // 更新当前连接时间
        MtSession mtSession = MtContainerUtil.getMtSessionMap(companyId, groupId, token);
        if (mtSession != null) {
            mtSession.setConnectTime(DateUtils.currentTimeMilli());
        } else {
            MtContainerUtil.mtSessionMapPut(companyId, groupId, token, session);
        }
        // 接收数据，-- 调用企业站点接口添加记录
        BaseBuilder<SynergyGroupRecord> reqEntity =
            JSON.parseObject(message, new TypeReference<BaseBuilder<SynergyGroupRecord>>() {});
        String keyStr = token + reqEntity.getSerialNumber();
        // 判断回应类型
        if (TypeConstant.REQUEST_PING_TYPE.equals(reqEntity.getRequestType())) {
            // ping-客户端心跳，不做任何操作
        } else if (TypeConstant.REQUEST_RESPONSE_TYPE.equals(reqEntity.getRequestType())) {
            // 服务器发送消息，客户端回应
            // 移除-服务器发送消息
            MtContainerUtil.mtPushRemove(companyId, groupId, keyStr);
        } else {
            // 判断该消息是否接收过
            BaseBuilder<SynergyGroupRecord> builder = MtContainerUtil.getMtReceiveMap(companyId, groupId, keyStr);
            if (builder == null) {
                reqEntity.getData().setSendTime(DateUtils.getDateTime());
                reqEntity.setPushTime(DateUtils.currentTimeMilli());
                reqEntity.setPustToken(token);// 发送人token
                // 添加接收消息
                MtContainerUtil.mtReceiveMapPut(companyId, groupId, keyStr, reqEntity);
                /*接收到客户端信息-服务端推消息给用户*/
                servicePushUser(webUrl, reqEntity, companyId, token, groupId);
            }
        }
    }

    /**
     * 接收到客户端信息-服务端推消息给用户
     * 
     * @param reqEntity
     * @param companyId
     * @param token
     * @param groupId
     * @throws Exception
     */
    public void servicePushUser(String webUrl, BaseBuilder<SynergyGroupRecord> reqEntity, String companyId,
        String token, String groupId) throws Exception {
        reqEntity.getData().setDeviceType(reqEntity.getRequestType());
        /*访问企业站点-添加记录*/
        if (!webUrl.contains("http://")) {
            webUrl = "http://" + webUrl;
        }
        String url = webUrl.trim() + AsyncUrlConstant.ADD_GROUP_RECORD_URL;// 请求接口地址
        Map<String, Object> resMap = HttpRequestUtils.httpPost(url,
            HttpRequestUtils.getBuEncryptionParam(BeanToMapUtil.convertBean(reqEntity.getData()), token));
        // 响应结果
        Map<String, Object> resParam = HttpRequestUtils.getBuDecryptionParam(resMap);
        if (!"000000".equals(resParam.get("code").toString())) {
            throw new RuntimeException("添加聊天记录异常!");
        }
        Map<String, Object> dataMap = (Map<String, Object>)resParam.get("data");
        SynergyGroupRecord serEntity = (SynergyGroupRecord)BeanToMapUtil.convertMap(SynergyGroupRecord.class, dataMap);
        /*创建发送消息数据*/
        String uuid = "server_" + java.util.UUID.randomUUID().toString();// 生成uuid 作为流水号
        BaseBuilder<SynergyGroupRecord> pushNews = new BaseBuilder(uuid, "服务器推送消息!", serEntity);
        pushNews.setResponseType(TypeConstant.RESPONSE_PUSH_TYPE); // 设置响应类型
        pushNews.setPustNumber(1);// 发送次数
        /*群发消息*/
        ConcurrentHashMap<String, MtSession> mtSesMap = MtContainerUtil.getMtSessionMap(companyId, groupId);
        if (mtSesMap != null) {
            Iterator<String> iter = mtSesMap.keySet().iterator();
            while (iter.hasNext()) {
                String tempToken = iter.next();
                MtSession mtSes = MtContainerUtil.getMtSessionMap(companyId, groupId, tempToken);
                if (!tempToken.equals(token) && mtSes != null) {
                    // 发送消息
                    mtSendText(mtSes.getSession(), companyId, groupId, pushNews);
                    // 记录发送消息给谁
                    BaseBuilder<SynergyGroupRecord> resEntity = pushNews.clone();
                    resEntity.setPustToken(token);
                    resEntity.setReceiveToken(tempToken);
                    resEntity.setPushTime(DateUtils.currentTimeMilli());
                    String keyStr = tempToken + resEntity.getSerialNumber();
                    MtContainerUtil.mtPushMapPut(companyId, groupId, keyStr, resEntity);
                } else {
                    // 给当前连接发消息提示成功
                    BaseBuilder<SynergyGroupRecord> resultUs = reqEntity.clone();
                    resultUs.setMsg("响应客户端消息!");
                    resultUs.setResponseType(TypeConstant.RESPONSE_SUCCESS_TYPE);// 设置响应类型
                    mtSendText(mtSes.getSession(), companyId, groupId, resultUs);
                }
            }
        }
    }

    /**
     * 接收到后端信息-服务端推消息给用户
     * 
     * @param pushEntity
     * @param companyId
     * @param token
     * @param groupId
     * @throws Exception
     */
    public static void pushUser(BaseBuilder<SynergyGroupRecord> pushEntity, String companyId, String groupId,
        String token) throws Exception {
        /*群发消息*/
        ConcurrentHashMap<String, MtSession> mtSesMap = MtContainerUtil.getMtSessionMap(companyId, groupId);
        if (mtSesMap != null) {
            Iterator<String> iter = mtSesMap.keySet().iterator();
            while (iter.hasNext()) {
                String tempToken = iter.next();
                // 不发给自己
                if (!tempToken.equals(token)) {
                    MtSession mtSes = MtContainerUtil.getMtSessionMap(companyId, groupId, tempToken);
                    // 发送
                    mtSendText(mtSes.getSession(), companyId, groupId, pushEntity);
                    // 记录发送消息给谁
                    BaseBuilder<SynergyGroupRecord> resEntity = pushEntity.clone();
                    resEntity.setPustToken(token);
                    resEntity.setReceiveToken(tempToken);
                    resEntity.setPushTime(DateUtils.currentTimeMilli());
                    String keyStr = tempToken + resEntity.getSerialNumber();
                    MtContainerUtil.mtPushMapPut(companyId, groupId, keyStr, resEntity);
                }
            }
        }
    }

    /**
     * 发送消息
     * 
     * @param session
     * @param baseBuilder
     */
    public static void mtSendText(Session session, String companyId, String groupId,
        BaseBuilder<SynergyGroupRecord> baseBuilder) {
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
            MtContainerUtil.mtPushMapPut(companyId, groupId, keyStr, baseBuilder);
        }
    }
}
