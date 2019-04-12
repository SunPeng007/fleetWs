package com.mt.system.domain.entity;

/**
 * Created with IDEA
 * author: chenpan
 * Date:2019/4/12
 * Time:10:11
 */
public class EchoBuilder {
    //发送token
    private String pustToken;
    //消息内容
    private BaseBuilder baseBuilder;
    //接收token
    private String receiveToken;

    public String getPustToken() {
        return pustToken;
    }

    public void setPustToken(String pustToken) {
        this.pustToken = pustToken;
    }

    public BaseBuilder getBaseBuilder() {
        return baseBuilder;
    }

    public void setBaseBuilder(BaseBuilder baseBuilder) {
        this.baseBuilder = baseBuilder;
    }

    public String getReceiveToken() {
        return receiveToken;
    }

    public void setReceiveToken(String receiveToken) {
        this.receiveToken = receiveToken;
    }
}
