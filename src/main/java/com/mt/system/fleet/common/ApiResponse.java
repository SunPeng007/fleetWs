package com.mt.system.fleet.common;

import java.util.Objects;

public class ApiResponse {
    private String msg;
    private String code;
    private Boolean success = Boolean.valueOf(true);

    private Object data;

    public ApiResponse(Object data) {
        this.data = data;
        this.code = CommonErrors.RES_SUCCESS.getErrorCode();
        this.msg = CommonErrors.RES_SUCCESS.getErrorMsg();
    }

    public ApiResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
        this.success = Boolean.valueOf(false);
    }

    public ApiResponse(String code, String msg, Boolean success, Object data) {
        this.code = code;
        this.msg = msg;
        this.success = success;
        this.data = data;
    }

    public ApiResponse(BusiError rce, Boolean success) {
        this.code = rce.getErrorCode();
        this.msg = rce.getErrorMsg();
        this.success = success;
    }

    public ApiResponse(BusiError rce) {
        this.code = rce.getErrorCode();
        this.msg = rce.getErrorMsg();
        this.success = Objects.equals(rce, CommonErrors.RES_SUCCESS);
    }

    public ApiResponse(BusiError rce, String msg, Boolean success) {
        this.code = rce.getErrorCode();
        this.msg = msg;
        this.success = success;
    }

    public String getMsg() {
        return this.msg;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public String getCode() {
        return this.code;
    }

    public boolean isSuccess() {
        return this.success.booleanValue();
    }

    public Object getData() {
        return this.data;
    }

}
