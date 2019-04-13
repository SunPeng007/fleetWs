package com.mt.system.domain.constant;

/**
 * 类型状态码
 * Created by chenpan
 */
public interface TypeConstant {
    /*请求类型:ios*/
    String REQUEST_IOS_TYPE = "ios";
    /*请求类型:android*/
    String REQUEST_AN_TYPE = "an";
    /*请求类型: h5*/
    String REQUEST_H5_TYPE = "h5";
    /*客户端回应信息*/
    String REQUEST_RESPONSE_TYPE = "555555";

    /*响应成功时状态*/
    String RESPONSE_SUCCESS_TYPE = "000000";
    /*服务器主动推送数据*/
    String RESPONSE_PUSH_TYPE = "666666";


}