package com.mt.system.controller;

import com.mt.system.common.result.ResponseBuilder;
import com.mt.system.common.util.RSAUtils4Client;
import com.mt.system.domain.entity.BaseBuilder;
import com.mt.system.websocket.MtContainerUtil;
import com.mt.system.websocket.MtWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/4/16
 * Time:10:25
 */
@Controller
@RequestMapping("/batch")
public class BatchController extends BaseController{
    private static Logger logger = LoggerFactory.getLogger(BatchController.class);
    //发送锁
    private static String pustKey="MoTooling";
    //接收锁
    private static String receiveKey="MoTooling";
    /**
     * 批量响应
     */
    @ResponseBody
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    public Object receive(HttpServletRequest requset) {
        try {
            if (requset.getParameter("paramsData") != null) {//验证参数加密字符串是否存
                Map<String, Object> paramMap = RSAUtils4Client.decryptionPostParam(requset.getParameter("paramsData").replace(" ", "+"), RSAUtils4Client.REQ_PRIVATE_KEY);//解密
                if(paramMap.get("token")!=null){
                    if (paramMap != null && paramMap.get("data") != null) {
                        Map<String, Object> dataMap = (Map<String, Object>) paramMap.get("data");
                        if (dataMap.get("pushList") != null) {//清除发送
                            List<Map<String,String>> pushList=(List<Map<String,String>>)dataMap.get("pushList");
                            String companyId=paramMap.get("companyId").toString();
                            String groupId=paramMap.get("groupId").toString();
                            String token=paramMap.get("token").toString();
                            cleanPushData(companyId,groupId,token,pushList);
                        }
                        if (dataMap.get("receiveList") != null) {//清除接收
                            List<Map<String,String>> receiveList=(List<Map<String,String>>)dataMap.get("receiveList");
                            String companyId=paramMap.get("companyId").toString();
                            String groupId=paramMap.get("groupId").toString();
                            String token=paramMap.get("token").toString();
                            cleanReceiveData(companyId,groupId,token,receiveList);
                        }
                        return resData(new ResponseBuilder().setSuccessResult(null, "批量响应成功!").encryptionResult());
                    }
                }
            }
            return resData(new ResponseBuilder().setParamFailResult("参数异常,请检查参数!").encryptionResult());
        }catch (Exception e) {
            e.printStackTrace();
            logger.error("清理容器数据异常：", e);
            return resData(new ResponseBuilder().setFailResult("清理容器数据异常!" + e.getMessage()).encryptionResult());
        }
    }

    /**
     * 清除发送（客户端）响应
     * @param token
     * @param pushList
     */
    private void  cleanPushData(String companyId,String groupId,String token,List<Map<String,String>> pushList){
        logger.info("清除发送（客户端）响应");
        synchronized(pustKey) {
            if (pushList == null || pushList.size() <= 0) {
                return;
            }
            for (Map<String, String> tempMap : pushList) {
                String keyToken = token + tempMap.get("serialNumber");
                BaseBuilder builder =MtContainerUtil.getMtReceiveMap(companyId,groupId,keyToken);
                if (builder != null) {
                    MtContainerUtil.mtReceiveMapRemove(companyId,groupId,token);
                }
            }
        }
    }
    /**
     * 清除响应（客户端）响应
     * @param token
     * @param receiveList
     */
    private void cleanReceiveData(String companyId,String groupId,String token,List<Map<String,String>> receiveList){
        logger.info("清除响应（客户端）响应");
        synchronized(receiveKey) {
            if(receiveList==null || receiveList.size()<=0) {
                return;
            }
            ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String,BaseBuilder>>> mtPushMap = MtContainerUtil.getMtPushMap();
            for (Map<String, String> tempMap : receiveList){
                String keyToken = token + tempMap.get("serialNumber");
                BaseBuilder builder = MtContainerUtil.getMtPushMap(companyId,groupId,keyToken);
                if (builder != null) {
                    MtContainerUtil.mtPushRemove(companyId,groupId,keyToken);
                }
            }
        }
    }
}
