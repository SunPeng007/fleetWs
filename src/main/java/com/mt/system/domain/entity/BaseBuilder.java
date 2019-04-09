package com.mt.system.domain.entity;

/**
 *  响应json数据包类
 * Created by chenpan on 4/27/18.
 */
public class BaseBuilder<T> {

    /*响应类型*/
    private String type;

    /*流水号*/
    private String serialNumber;

    /*返回数据*/
    private T data;

    public BaseBuilder(){

    }

    public BaseBuilder(String type, String serialNumber){
        this.type=type;
        this.serialNumber=serialNumber;
    }
    public BaseBuilder(String type, String serialNumber, T data){
        this.type=type;
        this.serialNumber=serialNumber;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

