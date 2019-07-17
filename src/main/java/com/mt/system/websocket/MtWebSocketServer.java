package com.mt.system.websocket;

import com.alibaba.fastjson.JSONObject;
import com.mt.system.common.util.BeanToMapUtil;
import com.mt.system.common.util.DateUtils;
import com.mt.system.common.util.HttpRequestUtils;
import com.mt.system.common.util.JsonUtil;
import com.mt.system.domain.constant.AsyncUrlConstant;
import com.mt.system.domain.constant.TypeConstant;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.MtSession;
import com.mt.system.domain.entity.SynergyGroupRecord;
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
@ServerEndpoint(value = "/mtwebsocket/{companyId}/{groupId}/{token}/{webUrl}")
public class MtWebSocketServer {
    private static Logger logger = LoggerFactory.getLogger(MtWebSocketServer.class);
    /**
     * 连接建立成功调用的方法
     * @param session
     * @param token
     */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam("companyId")String companyId,
                       @PathParam("token") String token,
                       @PathParam("groupId") String groupId) {
        try{
            MtContainerUtil.mtSessionMapPut(companyId,groupId,token,session);
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
    public void onClose(Session session,
                        @PathParam("companyId")String companyId,
                        @PathParam("groupId") String groupId,
                        @PathParam("token") String token)throws IOException {
        try{
            //移除当前连接
            MtContainerUtil.mtSessionMapRemove(companyId,groupId,token);
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
    public void onError(Session session, Throwable error,
                        @PathParam("companyId")String companyId,
                        @PathParam("groupId") String groupId,
                        @PathParam("token") String token) {
        try{
            //移除当前连接
            MtContainerUtil.mtSessionMapRemove(companyId,groupId,token);
            logger.info("发生错误时调用!");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("连接关闭发生异常:"+e);
        }
    }
    /**
     * 收到客户端消息后调用的方法
     * @param message
     * @param companyId
     * @param groupId
     * @param token
     */
    @OnMessage
    public void onMessage(String message,Session session,
                          @PathParam("companyId")String companyId,
                          @PathParam("groupId") String groupId,
                          @PathParam("token") String token,
                          @PathParam("webUrl") String webUrl) {
        try{
            logger.info("收到客户端消息!");
            //更新当前连接时间
            MtSession  mtSession = MtContainerUtil.getMtSessionMap(companyId,groupId,token);
            if(mtSession!=null){
                mtSession.setConnectTime(DateUtils.currentTimeMilli());
            }else{
                MtContainerUtil.mtSessionMapPut(companyId,groupId,token,session);
            }
            //判断回应类型
            //接收数据，-- 调用企业站点接口添加记录
            BaseBuilder reqEntity = JsonUtil.toObject(message,BaseBuilder.class);
            //服务器发送消息，客户端回应
            if(TypeConstant.REQUEST_RESPONSE_TYPE.equals(reqEntity.getRequestType())){
                //移除-服务器发送消息
                String keyStr=token+reqEntity.getSerialNumber();
                MtContainerUtil.mtPushRemove(companyId,groupId,token);
            }else{
                //判断该消息是否发送过
                BaseBuilder builder = MtContainerUtil.getMtReceiveMap(companyId,groupId,token+reqEntity.getSerialNumber());
                if(builder==null){
                    reqEntity.getData().setSendTime(DateUtils.getDateTime());
                    reqEntity.setPushTime(DateUtils.currentTimeMilli());
                    reqEntity.setPustToken(token);//发送人token
                    //添加接收消息
                    MtContainerUtil.mtReceiveMapPut(companyId,groupId,token+reqEntity.getSerialNumber(),reqEntity);
                    /*接收到客户端信息-服务端推消息给用户*/
                    SynergyGroupRecord serEntity=servicePushUser(webUrl,reqEntity,companyId,token,groupId);
                    serEntity.setSendTime(reqEntity.getData().getSendTime());
                    /*给当前连接发消息提示成功*/
                    BaseBuilder resultUs=reqEntity.clone();
                    resultUs.setResponseType(TypeConstant.RESPONSE_SUCCESS_TYPE);//设置响应类型
                    resultUs.setData(serEntity);
                    mtSendText(session,companyId,groupId,resultUs);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("发送消息发生异常:"+e);
        }
    }
    /**
     * 接收到客户端信息-服务端推消息给用户
     * @param reqEntity
     * @param companyId
     * @param token
     * @param groupId
     * @throws Exception
     */
    public SynergyGroupRecord servicePushUser(String webUrl,BaseBuilder reqEntity,String companyId,String token,String groupId)throws Exception{
        reqEntity.getData().setDeviceType(reqEntity.getRequestType());
        /*访问企业站点-添加记录*/
        //访问企业站点
        String url="http://"+webUrl.trim()+AsyncUrlConstant.ADD_GROUP_RECORD_URL;//请求接口地址
        Map<String,Object> resMap = HttpRequestUtils.httpPost(url,HttpRequestUtils.getBuEncryptionParam(BeanToMapUtil.convertBean(reqEntity.getData())));
        //响应结果
        Map<String,Object> resParam=HttpRequestUtils.getBuDecryptionParam(resMap);
        if(!"000000".equals(resParam.get("code").toString())) {
            throw new RuntimeException("添加聊天记录异常!");
        }
        Map<String, Object> dataMap = (Map<String, Object>) resParam.get("data");
        SynergyGroupRecord serEntity=(SynergyGroupRecord)BeanToMapUtil.convertMap(SynergyGroupRecord.class,dataMap);
        /*创建发送消息数据*/
        String uuid = java.util.UUID.randomUUID().toString();//生成uuid 作为流水号
        BaseBuilder pushNews = new BaseBuilder(uuid,"服务器推送消息!",serEntity);
        pushNews.setResponseType(TypeConstant.RESPONSE_PUSH_TYPE); //设置响应类型
        pushNews.setPustNumber(1);//发送次数
        Integer timeStamp=Integer.valueOf(serEntity.getSendTime());
        serEntity.setSendTime(DateUtils.timestampToString(timeStamp,"yyyy-MM-dd HH:mm:ss"));
        /*群发消息*/
        ConcurrentHashMap<String,MtSession>  mtSesMap = MtContainerUtil.getMtSessionMap(companyId,groupId);
        if(mtSesMap!=null){
            Iterator<String> iter = mtSesMap.keySet().iterator();
            while(iter.hasNext()){
                String key=iter.next();
                MtSession mtSes =MtContainerUtil.getMtSessionMap(companyId,groupId,key);
                if(!key.equals(token) && mtSes!=null){
                    mtSendText(mtSes.getSession(),companyId,groupId,pushNews);
                    //记录发送消息给谁
                    BaseBuilder resEntity =pushNews.clone();
                    addMtEcho(resEntity,1,token,companyId,groupId,key);
                }
            }
        }
        //返回记录
        return serEntity;
    }
    /**
     * 记录发送消息给谁
     * @param resEntity
     * @param pustNumber
     * @param pustToken
     * @param receiveToken
     */
    private void addMtEcho(BaseBuilder resEntity,int pustNumber,String pustToken,String companyId,String groupId,String receiveToken){
        resEntity.setPustNumber(pustNumber);
        resEntity.setPustToken(pustToken);
        resEntity.setReceiveToken(receiveToken);
        resEntity.setPushTime(DateUtils.currentTimeMilli());
        String keyStr=receiveToken+resEntity.getSerialNumber();
        MtContainerUtil.mtPushMapPut(companyId,groupId,keyStr,resEntity);
    }
    /**
     * 发送消息
     * @param session
     * @param baseBuilder
     */
    public static void mtSendText(Session session,String companyId,String groupId,BaseBuilder baseBuilder){
        try {
            if(session!=null){
                session.getBasicRemote().sendText(JSONObject.toJSONString(baseBuilder));
            }
        } catch (IOException e) {
            //判断发送次数
            if(baseBuilder.getPustNumber()==1){
                baseBuilder.setPustNumber(0);
            }
            String keyStr=baseBuilder.getReceiveToken()+baseBuilder.getSerialNumber();
            MtContainerUtil.mtPushMapPut(companyId,groupId,keyStr,baseBuilder);
            e.printStackTrace();
            logger.error("发送消息发生异常："+e);
        }
    }
}
