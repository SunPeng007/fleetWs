package com.mt.system.fleet.util;

import java.util.ArrayList;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mt.system.fleet.common.XingeErrors;
import com.tencent.xinge.XingeApp;
import com.tencent.xinge.bean.AudienceType;
import com.tencent.xinge.bean.Environment;
import com.tencent.xinge.bean.Message;
import com.tencent.xinge.bean.MessageType;
import com.tencent.xinge.bean.Platform;
import com.tencent.xinge.push.app.PushAppRequest;
import com.tencent.xinge.push.app.PushAppResponse;

public class XingeHttpClient {

    private Logger logger = LoggerFactory.getLogger(XingeHttpClient.class);

    private XingeApp anXingeApp;
    private XingeApp iosXingeApp;

    public boolean pushAllAn(String msgContext, String msgTitle, MessageType messageType) {
        PushAppRequest pushAppRequest = new PushAppRequest();
        pushAppRequest.setAudience_type(AudienceType.all);
        Message message = new Message();
        message.setTitle(msgTitle);
        message.setContent(msgContext);
        pushAppRequest.setMessage(message);
        pushAppRequest.setPlatform(Platform.android);
        pushAppRequest.setMessage_type(messageType);
        // 完善PushAppRequest 消息
        JSONObject anRet = anXingeApp.pushApp(pushAppRequest);
        PushAppResponse an = json2Obj(anRet);
        if (an.getRet_code() != 0) {
            logger.error(XingeErrors.ERR_AN_PUT_ALL.getErrorCode() + XingeErrors.ERR_AN_PUT_ALL.getErrorMsg()
                + an.getRet_code() + an.getErr_msg());
            return false;
        }
        return true;
    }

    public boolean pushAllIos(String msgContext, String msgTitle, MessageType messageType, Environment environment) {

        PushAppRequest pushAppRequest1 = new PushAppRequest();
        pushAppRequest1.setAudience_type(AudienceType.all);
        pushAppRequest1.setPlatform(Platform.ios);
        pushAppRequest1.setEnvironment(environment);
        pushAppRequest1.setMessage_type(messageType);
        Message message1 = new Message();
        message1.setTitle(msgTitle);
        message1.setContent(msgContext);
        pushAppRequest1.setMessage(message1);
        JSONObject iosRet = iosXingeApp.pushApp(pushAppRequest1);
        PushAppResponse ios = json2Obj(iosRet);
        if (ios.getRet_code() != 0) {
            logger.error(XingeErrors.ERR_IOS_PUT_ALL.getErrorCode() + XingeErrors.ERR_IOS_PUT_ALL.getErrorMsg()
                + ios.getRet_code() + ios.getErr_msg());
            return false;
        }
        return true;

    }

    public boolean pushTokenListAn(ArrayList<String> list, String msgContext, String msgTitle,
        MessageType messageType) {
        PushAppRequest pushAppRequest = new PushAppRequest();
        pushAppRequest.setAudience_type(AudienceType.token_list);
        Message message = new Message();
        message.setTitle(msgTitle);
        message.setContent(msgContext);
        pushAppRequest.setMessage(message);
        pushAppRequest.setPlatform(Platform.android);
        pushAppRequest.setMessage_type(messageType);
        pushAppRequest.setToken_list(list);
        JSONObject anRet = anXingeApp.pushApp(pushAppRequest);
        PushAppResponse an = json2Obj(anRet);
        if (an.getRet_code() != 0) {
            logger.error(
                XingeErrors.ERR_AN_PUT_TOKEN_LIST.getErrorCode() + XingeErrors.ERR_AN_PUT_TOKEN_LIST.getErrorMsg()
                    + an.getRet_code() + an.getRet_code() + an.getErr_msg());
            return false;
        }
        return true;
    }

    public boolean pushTokenListIos(ArrayList<String> list, String msgContext, String msgTitle, MessageType messageType,
        Environment environment) {
        PushAppRequest pushAppRequest = new PushAppRequest();
        pushAppRequest.setAudience_type(AudienceType.token_list);
        Message message = new Message();
        message.setTitle(msgTitle);
        message.setContent(msgContext);
        pushAppRequest.setMessage(message);
        pushAppRequest.setPlatform(Platform.ios);
        pushAppRequest.setMessage_type(messageType);
        pushAppRequest.setEnvironment(environment);
        pushAppRequest.setToken_list(list);
        // 完善PushAppRequest 消息
        JSONObject anRet = anXingeApp.pushApp(pushAppRequest);
        PushAppResponse an = json2Obj(anRet);
        if (an.getRet_code() != 0) {
            logger.error(XingeErrors.ERR_IOS_PUT_TOKEN_LIST.getErrorCode()
                + XingeErrors.ERR_IOS_PUT_TOKEN_LIST.getErrorMsg() + an.getRet_code() + an.getErr_msg());
            return false;
        }
        return true;
    }

    public static PushAppResponse json2Obj(JSONObject ret) {
        PushAppResponse o = com.alibaba.fastjson.JSONObject.parseObject(ret.toString(), PushAppResponse.class);
        return o;
    }

    public void setAnXingeApp(XingeApp anXingeApp) {
        this.anXingeApp = anXingeApp;
    }

    public void setIosXingeApp(XingeApp iosXingeApp) {
        this.iosXingeApp = iosXingeApp;
    }
}
