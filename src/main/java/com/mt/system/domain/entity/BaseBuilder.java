package com.mt.system.domain.entity;

/**
 *  响应json数据包类
 * Created by chenpan on 4/27/18.
 */
public class BaseBuilder<T> {

    /*类型*/
    private String type;

    /*流水号*/
    private String serialNumber;

    /*消息*/
    private String msg;

    /*数据*/
    private T data;

    public BaseBuilder(){ }
    public BaseBuilder(String type, String serialNumber){
        this.type=type;
        this.serialNumber=serialNumber;
    }
    public BaseBuilder(String type, String serialNumber,String msg){
        this.type=type;
        this.serialNumber=serialNumber;
        this.msg=msg;
    }
    public BaseBuilder(String type, String serialNumber,String msg, T data){
        this.type=type;
        this.serialNumber=serialNumber;
        this.msg=msg;
        this.data=data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

