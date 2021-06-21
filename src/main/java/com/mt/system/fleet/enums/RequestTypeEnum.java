package com.mt.system.fleet.enums;

public enum RequestTypeEnum implements CodeEnum {
    //
    PING(1, "ping心跳"), NOTIFY(2, "通知");

    private final int code;
    private String desc;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    RequestTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "RequestTypeEnum{" + "code=" + code + ", desc='" + desc + '\'' + '}';
    }
}
