package com.mt.system.controller;

import com.mt.system.common.result.ResponseBuilder;
import com.mt.system.common.util.RSAUtils4Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/4/16
 * Time:10:25
 */
@Controller
@RequestMapping("/clean")
public class CleanDataController extends BaseController{
    private static Logger logger = LoggerFactory.getLogger(CleanDataController.class);

    /**
     * 根据uid获取用户详情
     */
    @ResponseBody
    @RequestMapping(value = "/containerData", method = RequestMethod.POST)
    public Object containerData(HttpServletRequest requset,Map<String,Object> paramData) {
        try {
            if (requset.getParameter("paramsData") != null) {//验证参数加密字符串是否存
                Map<String, Object> paramMap = RSAUtils4Client.decryptionPostParam(requset.getParameter("paramsData").replace(" ", "+"), RSAUtils4Client.REQ_PRIVATE_KEY);//解密
                if (paramMap != null && paramMap.get("data") != null) {
                    Map<String, Object> dataMap = (Map<String, Object>) paramMap.get("data");
                    if (dataMap.get("uid") != null) {//验证参数
                       // return resData(userService.getUserInfo(dataMap.get("uid").toString()).encryptionResult());
                    }
                }
            }
            return resData(new ResponseBuilder().setParamFailResult("参数异常,请检查参数!").encryptionResult());
        }catch (Exception e) {
            e.printStackTrace();
            logger.error("清理容器数据异常：", e);
            return resData(new ResponseBuilder().setFailResult("查询用户详情发生异常!" + e.getMessage()).encryptionResult());
        }
    }

}
