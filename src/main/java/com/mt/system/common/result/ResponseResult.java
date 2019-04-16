package com.mt.system.common.result;

import com.mt.system.common.util.RSAUtils4Client;

/**
 * 响应结果
 * @param <T>
 * Created by chenpan on 4/27/18.
 */
public class ResponseResult<T> {

    /*成功失败状态码，0:成功 1:失败*/
    private Integer status;

    /*返回码，成功：000000*/
    private String code;

    /*返回数据*/
    private T data;

    /*备注说明信息*/
    private String msg;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String encryptionResult(){
        return RSAUtils4Client.encryptionResult(this,RSAUtils4Client.RES_PUBLIC_KEY);
    }
}
