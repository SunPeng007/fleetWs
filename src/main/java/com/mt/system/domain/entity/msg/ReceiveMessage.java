package com.mt.system.domain.entity.msg;

import java.util.List;
import java.util.Map;

public class ReceiveMessage {
    //用户集合
    List<UserMessage> userMsgList;
    //消息对象
    private Map<String,Object> msgData;
    //消息类型
    private Integer msgType=1;//1站内信、2企业圈

    public List<UserMessage> getUserMsgList() {
        return userMsgList;
    }

    public void setUserMsgList(List<UserMessage> userMsgList) {
        this.userMsgList = userMsgList;
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
