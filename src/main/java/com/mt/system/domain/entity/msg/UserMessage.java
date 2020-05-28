package com.mt.system.domain.entity.msg;

public class UserMessage {
    //用户id
    private Long uid;
    //消息总数量
    private Integer msgCount;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Integer getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(Integer msgCount) {
        this.msgCount = msgCount;
    }
}
