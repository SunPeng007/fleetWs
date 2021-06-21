package com.mt.system.fleet.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.mt.system.fleet.common.ApiResponse;
import com.mt.system.fleet.common.CommonErrors;
import com.mt.system.fleet.common.XingeErrors;
import com.mt.system.fleet.entity.socket.Msg;
import com.mt.system.fleet.enums.RequestTypeEnum;
import com.mt.system.fleet.util.WebCheckUtil;
import com.mt.system.fleet.util.XingeHttpClient;
import com.mt.system.fleet.websocket.MsgServer;
import com.tencent.xinge.bean.Environment;
import com.tencent.xinge.bean.MessageType;
import com.tencent.xinge.bean.Platform;

@RestController
@RequestMapping("/fleetMsg")
public class FleetMsgController {

    private static Logger logger = LoggerFactory.getLogger(FleetMsgController.class);

    @Autowired
    private XingeHttpClient xingeHttpClient;

    @PostMapping("/pushAll")
    public ApiResponse pushAll(@RequestBody Map<String, String> map) {
        // Map<String, Object> map = WebUtils.getParametersStartingWith(request, "");
        String msgTitle = map.get("msgTitle");
        String msgContext = map.get("msgContext");
        String uid = map.get("uid");
        if (WebCheckUtil.illParams(msgContext, msgTitle, uid)) {
            logger.error(CommonErrors.ERR_WEB_PARAM_IS_NULL.getErrorMsg());
            return new ApiResponse(CommonErrors.ERR_WEB_PARAM_IS_NULL);
        }
        MessageType msgType;
        Environment environment;
        try {
            msgType = MessageType.valueOf(map.get("msgType"));
            environment = Environment.valueOf(map.get("environment"));
        } catch (Exception e) {
            logger.error(CommonErrors.ERR_WEB_PARAM_INVALID.getErrorMsg());
            return new ApiResponse(CommonErrors.ERR_WEB_PARAM_INVALID);
        }
        Msg msg = new Msg();
        msg.setUid(uid);
        msg.setMsgContext(msgContext);
        msg.setReqType(RequestTypeEnum.NOTIFY);
        // PC web socket
        MsgServer.notifyAllUser(msg);
        // Android ios xinge
        boolean an = xingeHttpClient.pushAllAn(msgContext, msgTitle, msgType);
        boolean ios = xingeHttpClient.pushAllIos(msgContext, msgTitle, msgType, environment);
        if (an && ios) {
            return new ApiResponse(msg.getSendCount());
        }
        if (!an && !ios) {
            return new ApiResponse(XingeErrors.ERR_AN_IOS_PUT_ALL);
        }
        if (an) {
            return new ApiResponse(XingeErrors.ERR_AN_PUT_ALL);
        }
        return new ApiResponse(XingeErrors.ERR_IOS_PUT_ALL);
    }

    /***
     * 批量推送 安卓或者ios uid：推送者id uidList：被推送者uid tokenList：被推送者token
     * 
     * @param map
     * @return pc消息成功数
     */
    @PostMapping("/pushToken")
    public ApiResponse pushToken(@RequestBody Map<String, String> map) {
        String msgTitle = map.get("msgTitle");
        String msgContext = map.get("msgContext");
        String uid = map.get("uid");
        List<String> uidList = JSON.parseArray(JSON.toJSONString(map.get("uidList")), String.class);
        List<String> tokenList = JSON.parseArray(JSON.toJSONString(map.get("tokenList")), String.class);
        if (WebCheckUtil.illParams(msgContext, msgTitle, uid, tokenList, uidList)) {
            logger.error(CommonErrors.ERR_WEB_PARAM_IS_NULL.getErrorMsg());
            return new ApiResponse(CommonErrors.ERR_WEB_PARAM_IS_NULL);
        }
        MessageType msgType;
        Platform tokenType;
        try {
            msgType = MessageType.valueOf(map.get("msgType"));
            tokenType = Platform.valueOf(map.get("tokenType"));
        } catch (Exception e) {
            logger.error(CommonErrors.ERR_WEB_PARAM_IS_NULL.getErrorMsg());
            return new ApiResponse(CommonErrors.ERR_WEB_PARAM_IS_NULL);
        }
        Environment environment;
        // Android ios xinge
        if (tokenType.equals(Platform.ios)) {
            try {
                environment = Environment.valueOf(map.get("environment"));
            } catch (Exception e) {
                logger.error(CommonErrors.ERR_WEB_PARAM_IS_NULL.getErrorMsg());
                return new ApiResponse(CommonErrors.ERR_WEB_PARAM_IS_NULL);
            }
            xingeHttpClient.pushTokenListIos((ArrayList<String>)tokenList, msgContext, msgTitle, msgType, environment);
        }
        if (tokenType.equals(Platform.android)) {
            xingeHttpClient.pushTokenListAn((ArrayList<String>)tokenList, msgContext, msgTitle, msgType);
        }

        Msg msg = new Msg();
        msg.setUid(uid);
        msg.setMsgContext(msgContext);
        msg.setReqType(RequestTypeEnum.NOTIFY);

        // PC web socket
        MsgServer.notifyUserList(uidList, msg);

        return new ApiResponse(msg.getSendCount());
    }

}
