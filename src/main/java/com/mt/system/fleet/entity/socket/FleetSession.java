package com.mt.system.fleet.entity.socket;

import javax.websocket.Session;

public class FleetSession {

    private String uid;

    private Session session;

    private Long lastPingTime;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Long getLastPingTime() {
        return lastPingTime;
    }

    public void setLastPingTime(Long lastPingTime) {
        this.lastPingTime = lastPingTime;
    }

}
