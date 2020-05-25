package com.mt.system.domain.entity.im;

/**
 *  响应json数据包类
 * Created by chenpan on 4/27/18.
 */
public class BaseBuilder implements Cloneable{

    /*请求类型*/
    private String requestType;
    /*流水号*/
    private String serialNumber;
    /*消息*/
    private String msg;
    /*数据*/
    private SynergyGroupRecord data;

    /*响应类型*/
    private String responseType;
    /*发送token*/
    private String pustToken;
    /*发送次数*/
    private int pustNumber;
    /*接收token*/
    private String receiveToken;
    /*服务器推送:发送时间*/
    private long pushTime;

    public BaseBuilder(){ }
    public BaseBuilder(String serialNumber,String msg,SynergyGroupRecord data){
        this.serialNumber=serialNumber;
        this.msg=msg;
        this.data=data;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
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

    public SynergyGroupRecord getData() {
        return data;
    }

    public void setData(SynergyGroupRecord data) {
        this.data = data;
    }

    public String getPustToken() {
        return pustToken;
    }

    public void setPustToken(String pustToken) {
        this.pustToken = pustToken;
    }

    public int getPustNumber() {
        return pustNumber;
    }

    public void setPustNumber(int pustNumber) {
        this.pustNumber = pustNumber;
    }

    public String getReceiveToken() {
        return receiveToken;
    }

    public void setReceiveToken(String receiveToken) {
        this.receiveToken = receiveToken;
    }

    public long getPushTime() {
        return pushTime;
    }

    public void setPushTime(long pushTime) {
        this.pushTime = pushTime;
    }

    @Override
    public BaseBuilder clone()
    {
        BaseBuilder entity = null;
        try{
            entity = (BaseBuilder)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return entity;
    }

}

