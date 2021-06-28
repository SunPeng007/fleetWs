package com.mt.system.fleet.entity.socket;

import com.mt.system.fleet.enums.RequestTypeEnum;

public class Msg {

    private String uid;
    private String uName;
    private String msgTitle;
    private String msgContext;
    private RequestTypeEnum reqType;
    private Integer sendCount = 0;

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    @Override
    public String toString() {
        return "Msg [uid=" + uid + ", uName=" + uName + ", msgTitle=" + msgTitle + ", msgContext=" + msgContext
            + ", reqType=" + reqType + ", sendCount=" + sendCount + "]";
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
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
