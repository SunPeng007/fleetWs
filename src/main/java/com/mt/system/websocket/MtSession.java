package com.mt.system.websocket;

import javax.websocket.Session;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/4/10
 * Time:17:23
 */
public class MtSession {

    private Session session;

    private String connectTime;

    public MtSession(){}

    public MtSession(Session session,String connectTime){
        this.session=session;
        this.connectTime=connectTime;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(String connectTime) {
        this.connectTime = connectTime;
    }
}
