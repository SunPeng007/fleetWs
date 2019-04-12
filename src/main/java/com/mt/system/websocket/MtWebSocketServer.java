package com.mt.system.websocket;

import com.alibaba.fastjson.JSONObject;
import com.mt.system.common.code.HttpClientTool;
import com.mt.system.common.util.BeanToMapUtil;
import com.mt.system.common.util.DateUtils;
import com.mt.system.common.util.JsonUtil;
import com.mt.system.domain.constant.AsyncUrlConstant;
import com.mt.system.domain.constant.TypeConstant;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.MtSession;
import com.mt.system.domain.entity.SynergyGroupRecord;
import com.mt.system.domain.properties.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
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

    //记录服务器发送消息--未回应对象
    private static ConcurrentHashMap<String,BaseBuilder> mtEchoMap = new ConcurrentHashMap<String,BaseBuilder>();

    //定时任务获取session
    public static ConcurrentHashMap<String,MtSession> getMtSessionMap(){
        return mtSessionMap;
    }

    //连接建立成功调用的方法
    @OnOpen
    public void onOpen(Session session,@PathParam("token") String token) {
        try{
            //连接成功-加人
            mtSessionMap.put(token, new MtSession(session,token,DateUtils.currentTimeMilli()));
            logger.info("连接成功调用!");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("连接发生异常:"+e);
        }
    }
    //连接关闭调用的方法
    @OnClose
    public void onClose(Session session,@PathParam("token") String token)throws IOException {
        try{
            //移除当前连接
            mtSessionMap.remove(token);
            logger.info("连接关闭调用!");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("连接关闭发生异常:"+e);
        }
    }
    // 收到客户端消息后调用的方法
    @OnMessage
    public void onMessage(String message, Session session,@PathParam("token") String token) {
        try{
            //更新当前连接时间
            mtSessionMap.get(token).setConnectTime(DateUtils.currentTimeMilli());
            //判断回应类型
            //接收数据，-- 调用企业站点接口添加记录
            BaseBuilder<SynergyGroupRecord> reqEntity = JsonUtil.toObject(message,BaseBuilder.class);

            //判断类型
            if(TypeConstant.REQUEST_RESPONSE_TYPE.equals(reqEntity.getRequestType())){//服务器发送消息，客户端回应

            }




            reqEntity.getData().setDeviceType(reqEntity.getRequestType());
            //访问企业站点-添加记录
            Map<String,Object> dataMap =HttpClientTool.mtHttpPost(BeanToMapUtil.convertBean(reqEntity),SystemProperties.apiUrl+AsyncUrlConstant.ADD_GROUP_RECORD_URL);
            BaseBuilder<Map<String,Object>> result=new BaseBuilder("","",dataMap);
            result.setResponseType(TypeConstant.RESPONSE_PUSH_TYPE);//设置响应类型
            //群发消息
            for (MtSession mtSession : mtSessionMap.values()) {
                if(mtSession.getSession()!=session){
                    mtSendText(session,JSONObject.toJSONString(result));
                }
            }
            //给当前连接发消息提示成功。
            BaseBuilder<Map<String,Object>> resultUs=new BaseBuilder(reqEntity.getSerialNumber(),"发送成功!",null);
            resultUs.setResponseType(TypeConstant.RESPONSE_SUCCESS_TYPE);//设置响应类型
            mtSendText(session,JSONObject.toJSONString(resultUs));
        }catch (Exception e){
            BaseBuilder<SynergyGroupRecord> reqEntity = JsonUtil.toObject(message,BaseBuilder.class);
            BaseBuilder<Map<String,Object>> resultUs=new BaseBuilder(reqEntity.getSerialNumber(),"发送失败!",null);
            resultUs.setResponseType(TypeConstant.RESPONSE_FAIL_TYPE);//设置响应类型
            mtSendText(session,JSONObject.toJSONString(resultUs));
            e.printStackTrace();
            logger.error("发送消息发生异常:"+e);
        }
    }
    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error,@PathParam("token") String token) {
        try{
            //移除当前连接
            mtSessionMap.remove(token);
            logger.info("发生错误时调用!");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("连接关闭发生异常:"+e);
        }
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
            logger.error("发送消息发生异常："+e);
        }
    }
}
