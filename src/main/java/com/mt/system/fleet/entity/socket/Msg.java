package com.mt.system.fleet.entity.socket;

import com.mt.system.fleet.enums.RequestTypeEnum;

public class Msg {

    private String uid;
    private String msgContext;
    private RequestTypeEnum reqType;
    private Integer sendCount;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getSendCount() {
        return sendCount;
    }

    public void setSendCount(Integer sendCount) {
        this.sendCount = sendCount;
    }

    public String getMsgContext() {
        return msgContext;
    }

    public void setMsgContext(String msgContext) {
        this.msgContext = msgContext;
    }

    public RequestTypeEnum getReqType() {
        return reqType;
    }

    public void setReqType(RequestTypeEnum reqType) {
        this.reqType = reqType;
    }

}
