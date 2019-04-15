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
import java.util.Iterator;
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

    //记录服务器发送消息
    private static ConcurrentHashMap<String,BaseBuilder> mtPushMap = new ConcurrentHashMap<String,BaseBuilder>();

    //记录接收消息
    private static ConcurrentHashMap<String,BaseBuilder> mtReceiveMap = new ConcurrentHashMap<String,BaseBuilder>();

    //定时任务获取session
    public static ConcurrentHashMap<String,MtSession> getMtSessionMap(){
        return mtSessionMap;
    }
    //记录服务器发送消息
    public static ConcurrentHashMap<String,BaseBuilder> getMtPushMap(){
        return mtPushMap;
    }

    /**
     * 连接建立成功调用的方法
     * @param session
     * @param token
     */
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
    /**
     * 连接关闭调用的方法
     * @param session
     * @param token
     * @throws IOException
     */
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
     * 收到客户端消息后调用的方法
     * @param message
     * @param token
     */
    @OnMessage
    public void onMessage(String message,Session session,@PathParam("token") String token) {
        try{
            //更新当前连接时间
            mtSessionMap.get(token).setConnectTime(DateUtils.currentTimeMilli());
            //判断回应类型
            //接收数据，-- 调用企业站点接口添加记录
            BaseBuilder reqEntity = JsonUtil.toObject(message,BaseBuilder.class);
            //服务器发送消息，客户端回应
            if(TypeConstant.REQUEST_RESPONSE_TYPE.equals(reqEntity.getRequestType())){
                //移除-服务器发送消息
                mtPushMap.remove(token);
            }else{
                reqEntity.getData().setSendTime(DateUtils.getDateTime());
                //添加接收消息
                mtReceiveMap.put(token,reqEntity);

                /*给当前连接发消息提示成功*/
                BaseBuilder resultUs=reqEntity.clone();
                resultUs.setResponseType(TypeConstant.RESPONSE_SUCCESS_TYPE);//设置响应类型
                mtSendText(session,resultUs);
                /*接收到客户端信息-服务端推消息给用户*/
                servicePushUser(reqEntity,token);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("发送消息发生异常:"+e);
        }
    }
    /**
     * 接收到客户端信息-服务端推消息给用户
     * @param reqEntity
     * @param token
     * @throws Exception
     */
    public void servicePushUser(BaseBuilder reqEntity,String token)throws Exception{
        reqEntity.getData().setDeviceType(reqEntity.getRequestType());
        /*访问企业站点-添加记录*/
        String url=SystemProperties.apiUrl+AsyncUrlConstant.ADD_GROUP_RECORD_URL;//请求接口地址
        Map<String,Object> dataMap =HttpClientTool.mtHttpPost(BeanToMapUtil.convertBean(reqEntity.getData()),url);
        SynergyGroupRecord serEntity=(SynergyGroupRecord)BeanToMapUtil.convertMap(SynergyGroupRecord.class,dataMap);
        /*创建发送消息数据*/
        String uuid = java.util.UUID.randomUUID().toString();//生成uuid 作为流水号
        BaseBuilder pushNews = new BaseBuilder(uuid,"服务器推送消息!",serEntity);
        pushNews.setResponseType(TypeConstant.RESPONSE_PUSH_TYPE); //设置响应类型
        pushNews.setPustNumber(1);//发送次数
        /*群发消息*/
        Iterator<String> iter = mtSessionMap.keySet().iterator();
        while(iter.hasNext()){
            String key=iter.next();
            MtSession mtSession = mtSessionMap.get(key);
            if(!key.equals(token)){
                mtSendText(mtSession.getSession(),pushNews);
                //记录发送消息给谁
                BaseBuilder resEntity =pushNews.clone();
                addMtEcho(resEntity,1,token,key);
            }
        }
    }
    /**
     * 记录发送消息给谁
     * @param resEntity
     * @param pustNumber
     * @param pustToken
     * @param receiveToken
     */
    private void addMtEcho(BaseBuilder resEntity,int pustNumber,String pustToken,String receiveToken){
        resEntity.setPustNumber(pustNumber);
        resEntity.setPustToken(pustToken);
        resEntity.setReceiveToken(receiveToken);
        resEntity.setPushTime(DateUtils.currentTimeMilli());
        mtPushMap.put(receiveToken,resEntity);
    }
    /**
     * 发送消息
     * @param session
     * @param baseBuilder
     */
    public static void mtSendText(Session session,BaseBuilder baseBuilder){
        try {
            if(session!=null){
                session.getBasicRemote().sendText(JSONObject.toJSONString(baseBuilder));
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("发送消息发生异常："+e);
        }
    }
}
