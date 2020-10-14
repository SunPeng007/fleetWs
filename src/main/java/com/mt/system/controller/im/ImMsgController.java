package com.mt.system.controller.im;

import com.mt.system.common.result.ResponseBuilder;
import com.mt.system.common.util.BeanToMapUtil;
import com.mt.system.common.util.RSAUtils4Client;
import com.mt.system.controller.BaseController;
import com.mt.system.domain.constant.TypeConstant;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.domain.entity.im.SynergyGroupRecord;
import com.mt.system.websocket.im.MtWebSocketServer;
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
 * author: Alnwick
 * Date:2020/10/14
 * Time:10:25
 */
@Controller
@RequestMapping("/imMsg")
public class ImMsgController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(ImMsgController.class);
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
                        if(dataMap.get("companyId")!=null && dataMap.get("groupId")!=null && dataMap.get("record")!=null){
                            Map<String,Object> record=(Map<String,Object>)dataMap.get("record");
                            String token=paramMap.get("token").toString();
                            String companyId=dataMap.get("companyId").toString();
                            String groupId=dataMap.get("groupId").toString();
                            SynergyGroupRecord serEntity=(SynergyGroupRecord) BeanToMapUtil.convertMap(SynergyGroupRecord.class,record);
                            //生成uuid 作为流水号
                            String uuid = "server_"+java.util.UUID.randomUUID().toString();
                            BaseBuilder<SynergyGroupRecord> pushEntity = new BaseBuilder(uuid,"服务器推送消息!",serEntity);
                            //设置响应类型
                            pushEntity.setResponseType(TypeConstant.RESPONSE_PUSH_TYPE);
                            //发送次数
                            pushEntity.setPustNumber(1);
                            //群发消息
                            MtWebSocketServer.pushUser(pushEntity,companyId,groupId,token);
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
