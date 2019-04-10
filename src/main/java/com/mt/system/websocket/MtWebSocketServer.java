package com.mt.system.websocket;

import com.alibaba.fastjson.JSONObject;
import com.mt.system.common.util.DateUtils;
import com.mt.system.common.util.JsonUtil;
import com.mt.system.domain.constant.TypeConstant;
import com.mt.system.domain.entity.BaseBuilder;
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
 * author: chenpan
 * Date:2019/4/10
 *  @ServerEndpoint
 *  注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 *  注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 *  使用websocket的核心，就是一系列的websocket注解，@ServerEndpoint是注册在类上面开启。
 */
@Component
@ServerEndpoint(value = "/mtwebsocket/{token}")
public class MtWebSocketServer {
    private static Logger logger = LoggerFactory.getLogger(MtWebSocketServer.class);
    //ConcurrentHashMap是线程安全的，而HashMap是线程不安全的。
    //记录连接对象
    private static ConcurrentHashMap<String,MtSession> mtSessionMap = new ConcurrentHashMap<String,MtSession>();

    //定时任务获取session
    public static ConcurrentHashMap<String,MtSession> getMtSessionMap(){
        return mtSessionMap;
    }

    //连接建立成功调用的方法
    @OnOpen
    public void onOpen(Session session,@PathParam("token") String token) {
        //连接成功-加人
        mtSessionMap.put(token, new MtSession(session,DateUtils.currentTimeMillis()));
        logger.info("连接成功！");
    }

    //连接关闭调用的方法
    @OnClose
    public void onClose(Session session,@PathParam("token") String token)throws IOException {
        //移除当前连接
        mtSessionMap.remove(token);
        logger.info("关闭连接成功！");
    }

    // 收到客户端消息后调用的方法
    @OnMessage
    public void onMessage(String message, Session session,@PathParam("token") String token) {
        //更新当前连接时间
        mtSessionMap.get(token).setConnectTime(DateUtils.currentTimeMillis());
        //接收数据，-- 调用企业站点接口添加记录
        BaseBuilder reqEntity = JsonUtil.toObject(message,BaseBuilder.class);



        //群发消息
        for (MtSession mtSession : mtSessionMap.values()) {
            if(mtSession.getSession()!=session){
                mtSendText(session,JSONObject.toJSONString(reqEntity));
            }
        }
        //给当前连接发消息提示成功。
        reqEntity.setType(TypeConstant.RESPONSE_SUCCESS_TYPE);
        mtSendText(session,JSONObject.toJSONString(reqEntity));
    }
    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error,@PathParam("token") String token) {
        //移除当前连接
        mtSessionMap.remove(token);
        logger.error("发生错误!");
    }
    /**
     * 发送消息
     * @param session
     * @param msg
     */
    private void mtSendText(Session session,String msg){
        try {
            session.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("发生异常："+e);
        }
    }
}
