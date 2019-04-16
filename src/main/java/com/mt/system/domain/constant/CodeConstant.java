package com.mt.system.domain.constant;

/**
 * 状态码常量类
 * Created by chenpan on 4/27/18.
 */
public interface CodeConstant {

    /****************  全局编码 ********************/
    String DEFAULT_RESPONSE_SUCCESS_CODE = "000000";//返回成功时默认值
    String DEFAULT_RESPONSE_FAIL_CODE = "999999";   //返回失败时默认值
    String DEFAULT_PARAM_FAIL_CODE = "555555";//参数错误
    String NOT_FIND_TOKEN = "444444";//token不存在、失效(过期)、token未传

    /****************  用户类 ********************/
    String NOT_LOGIN = "100000";                      //请先登录
    String LOGIN_IS_DELETE="100001";                 //账号已被删除
    String NOT_ACCOUNT="100002";                      //账号不存在
    String EXIST_ACCOUNT="100003";                    //账号已存在
    String ERROR_PASSWORD="100004";                   //密码错误

}