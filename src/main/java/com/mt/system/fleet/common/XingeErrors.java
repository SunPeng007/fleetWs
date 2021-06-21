package com.mt.system.fleet.common;

public interface XingeErrors extends CommonErrors {
    BusiError ERR_AN_PUT_ALL = new BusiError("0101", "Android推送putAll异常：");
    BusiError ERR_AN_PUT_TOKEN_LIST = new BusiError("0102", "Android推送pushTokenList异常：");

    BusiError ERR_IOS_PUT_ALL = new BusiError("0201", "IOS推送putAll异常：");
    BusiError ERR_IOS_PUT_TOKEN_LIST = new BusiError("0202", "IOS推送pushTokenList异常：");

    BusiError ERR_AN_IOS_PUT_ALL = new BusiError("1001", "Android和IOS推送putAll异常：");
    BusiError ERR_AN_IOS_PUT_TOKEN_LIST = new BusiError("1002", "Android和IOS推送pushTokenList异常：");

}