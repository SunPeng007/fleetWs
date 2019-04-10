package com.mt.system.domain.entity;

import javax.websocket.Session;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/4/10
 * Time:17:23
 */
public class MtSession {

    private Session session;

    private Long connectTime;

    private String token;

    public MtSession(){}

    public MtSession(Session session,String token,Long connectTime){
        this.session=session;
        this.token=token;
        this.connectTime=connectTime;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Long getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(Long connectTime) {
        this.connectTime = connectTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
