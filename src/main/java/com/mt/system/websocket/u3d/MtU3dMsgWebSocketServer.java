package com.mt.system.websocket.u3d;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.List;

/**
 *  Created with IDEA
 *  author: chenpan
 *  Date:2020/9/14
 *  @ServerEndpoint
 *  注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 *  注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 *  使用websocket的核心，就是一系列的websocket注解，@ServerEndpoint是注册在类上面开启。
 */
@Component
@ServerEndpoint(value = "/MtU3dMsgWebSocket/{companyId}/{type}")
public class MtU3dMsgWebSocketServer {
    /**
     * 连接建立成功调用的方法
     * @param session
     * @param companyId
     * @throws Exception
     */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam("companyId")String companyId,
                       @PathParam("type")String type){
        String key=companyId+"_"+type;
        MtU3dMsgContainerUtil.getMtU3dMsgSessionMap().put(key,session);
    }

    /**
     * 连接关闭调用的方法
     * @param session
     * @param companyId
     * @throws Exception
     */
    @OnClose
    public void onClose(Session session,
                        @PathParam("companyId")String companyId,
                        @PathParam("type")String type){
        String key=companyId+"_"+type;
        MtU3dMsgContainerUtil.getMtU3dMsgSessionMap().remove(key);
    }
    /**
     * 发生错误时调用
     * @param session
     * @param error
     * @param companyId
     * @throws Exception
     */
    @OnError
    public void onError(Session session, Throwable error,
                        @PathParam("companyId")String companyId,
                        @PathParam("type")String type){
        String key=companyId+"_"+type;
        MtU3dMsgContainerUtil.getMtU3dMsgSessionMap().remove(key);
    }
    /**
     * 收到客户端消息后调用的方法
     * @param message
     * @param session
     * @param companyId
     * @throws Exception
     */
    @OnMessage
    public void onMessage(String message,Session session,
                          @PathParam("companyId")String companyId,
                          @PathParam("type")String type){
        //不做任何处理
    }


    /**
     * 接收到客户端信息-服务端推消息给用户
     * @param companyId
     * @throws Exception
     */
    public static void servicePushUser(String companyId,String msg)throws Exception{
        List<String> typeList = MtU3dMsgContainerUtil.getMtU3dMsgTypeList();
        for (String type:typeList){
            String key=companyId+"_"+type;
            Session session=MtU3dMsgContainerUtil.getMtU3dMsgSessionMap().get(key);
            if(session!=null){
                session.getBasicRemote().sendText(JSONObject.toJSONString(msg));
            }
        }
    }
}
