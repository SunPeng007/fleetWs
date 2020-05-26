package com.mt.system.websocket.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * Created with IDEA
 * author: chenpan
 * Date:2020/5/25
 *  @ServerEndpoint
 *  注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 *  注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 *  使用websocket的核心，就是一系列的websocket注解，@ServerEndpoint是注册在类上面开启。
 */
@Component
@ServerEndpoint(value = "/MtMsgWebSocket/{companyId}/{type}/{uid}")
public class MtMsgWebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(MtMsgWebSocketServer.class);

    /**
     * 连接建立成功调用的方法
     * @param session
     * @param companyId
     * @param type
     * @param uid
     * @throws Exception
     */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam("companyId")String companyId,
                       @PathParam("type") String type,
                       @PathParam("uid") String uid)throws Exception {
        try{
            logger.info("连接成功调用!");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("连接发生异常:"+e);
            throw e;
        }
    }
    /**
     * 连接关闭调用的方法
     * @param session
     * @param companyId
     * @param type
     * @param uid
     * @throws Exception
     */
    @OnClose
    public void onClose(Session session,
                        @PathParam("companyId")String companyId,
                        @PathParam("type") String type,
                        @PathParam("uid") String uid)throws Exception {
        try{
            //移除当前连接
            logger.info("连接关闭调用!");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("连接关闭发生异常:"+e);
            throw e;
        }
    }
    /**
     * 发生错误时调用
     * @param session
     * @param error
     * @param companyId
     * @param type
     * @param uid
     * @throws Exception
     */
    @OnError
    public void onError(Session session, Throwable error,
                        @PathParam("companyId")String companyId,
                        @PathParam("type") String type,
                        @PathParam("uid") String uid) throws Exception{
        try{
            //移除当前连接
            logger.info("发生错误时调用!");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("发生错误时发生异常:"+e);
            throw e;
        }
    }
    /**
     * 收到客户端消息后调用的方法
     * @param message
     * @param session
     * @param companyId
     * @param type
     * @param uid
     * @throws Exception
     */
    @OnMessage
    public void onMessage(String message,Session session,
                          @PathParam("companyId")String companyId,
                          @PathParam("type") String type,
                          @PathParam("uid") String uid)throws Exception {
        try {

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("收到客户端消息后发生异常:" + e);
            throw e;
        }
    }
}
