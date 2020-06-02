package com.mt.system.controller.msg;

import com.mt.system.common.result.ResponseBuilder;
import com.mt.system.common.util.BeanToMapUtil;
import com.mt.system.common.util.RSAUtils4Client;
import com.mt.system.controller.BaseController;
import com.mt.system.domain.constant.TypeConstant;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.msg.PushMessage;
import com.mt.system.domain.entity.msg.ReceiveMessage;
import com.mt.system.domain.entity.msg.UserMessage;
import com.mt.system.websocket.msg.MtMsgWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created with IDEA
 * author: chenpan
 * Date:2020/5/28
 * Time:10:25
 */
@Controller
@RequestMapping("/msg")
public class MsgController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(MsgController.class);

    /**
     * 发送消息
     */
    @ResponseBody
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public Object send(@RequestBody Map<String,Object> paramsMap) {
        try {
            if (paramsMap.get("paramsData") != null) {//验证参数加密字符串是否存
                Map<String, Object> paramMap = RSAUtils4Client.decryptionPostParam(paramsMap.get("paramsData").toString().replace(" ", "+"), RSAUtils4Client.REQ_PRIVATE_KEY);//解密
                if(paramMap.get("token")!=null){
                    if (paramMap != null && paramMap.get("data") != null) {
                        Map<String, Object> dataMap = (Map<String, Object>) paramMap.get("data");
                        if (dataMap.get("companyId")!=null && dataMap.get("type") != null && dataMap.get("uid") != null && dataMap.get("msgData")!=null) {
                            String companyId=dataMap.get("companyId").toString();
                            String token=dataMap.get("type").toString()+"_"+dataMap.get("uid").toString();
                            Map<String,Object> receiveMap=(Map<String, Object>)dataMap.get("msgData");
                            receiveMap.put("userMsgList",BeanToMapUtil.convertMapList(UserMessage.class,(List<Map<String, Object>>)receiveMap.get("userMsgList")));
                            ReceiveMessage receiveMessage = (ReceiveMessage)BeanToMapUtil.convertMap(ReceiveMessage.class,receiveMap);
                            //调用发送
                            BaseBuilder<ReceiveMessage> baseBuilder=new BaseBuilder<>();
                            baseBuilder.setRequestType(TypeConstant.REQUEST_SERVICE_TYPE);
                            baseBuilder.setData(receiveMessage);
                            MtMsgWebSocketServer.servicePushUser(baseBuilder,companyId,token);
                            return resData(new ResponseBuilder().setSuccessResult(null, "发送消息成功!").encryptionResult());
                        }
                    }
                }
            }
            return resData(new ResponseBuilder().setParamFailResult("参数异常,请检查参数!").encryptionResult());
        }catch (Exception e) {
            e.printStackTrace();
            logger.error("发送消息发生异常：", e);
            return resData(new ResponseBuilder().setFailResult("发送消息发生异常!" + e.getMessage()).encryptionResult());
        }
    }
}
