package com.mt.system.fleet.common;

public interface CommonErrors {
    BusiError RES_SUCCESS = new BusiError("0001", "成功");
    BusiError UNKNOWN_ERR = new BusiError("0000", "未知错误，请稍后重试！");

    BusiError ERR_WEB_PARAM_IS_NULL = new BusiError("1001", "参数为空");
    BusiError ERR_WEB_PARAM_INVALID = new BusiError("1002", "参数无效");

    BusiError ERR_SRV_FAILED = new BusiError("2001", "服务调用失败，请稍后重试！");

    BusiError ERR_DB_FAILED = new BusiError("3001", "操作失败，请稍后重试！");
}