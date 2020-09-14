package com.mt.system.controller.u3d;

import com.alibaba.fastjson.JSONObject;
import com.mt.system.common.result.ResponseBuilder;
import com.mt.system.common.util.RSAUtils4Client;
import com.mt.system.controller.BaseController;
import com.mt.system.websocket.u3d.MtU3dMsgWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;

/**
 * Created with IDEA
 * author: chenpan
 * Date:2020/9/14
 * Time:10:25
 */
@Controller
@RequestMapping("/u3dMsg")
public class U3dMsgController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(U3dMsgController.class);
    /**
     * 发送消息
     */
    @ResponseBody
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public Object send(@RequestBody Map<String,Object> paramsMap) {
        try {
            if (paramsMap.get("paramsData") != null) {//验证参数加密字符串是否存
                Map<String, Object> paramMap = RSAUtils4Client.decryptionPostParam(paramsMap.get("paramsData").toString().replace(" ", "+"), RSAUtils4Client.REQ_PRIVATE_KEY);//解密
                if(paramMap != null){
                    if (paramMap.get("data") != null) {
                        Map<String, Object> dataMap = (Map<String, Object>) paramMap.get("data");
                        if (dataMap.get("companyId") != null && dataMap.get("msgData") != null) {
                            String companyId = dataMap.get("companyId").toString();
                            Map<String, Object> msgDataMap = (Map<String, Object>) dataMap.get("msgData");
                            //调用发送
                            MtU3dMsgWebSocketServer.servicePushUser(companyId, JSONObject.toJSONString(msgDataMap));
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
