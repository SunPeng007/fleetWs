package com.mt.system.domain.entity.msg;

import java.util.Map;

public class PushMessage {
    //消息总数量
    private Integer msgCount;
    //消息对象
    private Map<String,Object> msgData;
    //消息类型
    private Integer msgType=1;//1站内信、2企业圈

    public Integer getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(Integer msgCount) {
        this.msgCount = msgCount;
    }

    public Map<String, Object> getMsgData() {
        return msgData;
    }

    public void setMsgData(Map<String, Object> msgData) {
        this.msgData = msgData;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }
}
