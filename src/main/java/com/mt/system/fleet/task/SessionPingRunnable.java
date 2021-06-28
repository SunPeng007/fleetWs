package com.mt.system.fleet.task;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mt.system.common.util.DateUtils;
import com.mt.system.fleet.entity.socket.FleetSession;
import com.mt.system.fleet.websocket.MsgServer;

public class SessionPingRunnable implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(SessionPingRunnable.class);
    private final static long REMOVE_TIME = 12000;
    private final static long SLEEP_TIME = 5000;

    @Override
    public void run() {

        FleetSession fSession;
        while (true) {
            for (Map.Entry<String, FleetSession> entry : MsgServer.sessionMap.entrySet()) {
                fSession = entry.getValue();
                if (fSession == null || (DateUtils.currentTimeMilli() - fSession.getLastPingTime() > REMOVE_TIME)) {
                    MsgServer.sessionMap.remove(entry.getKey());
                }
            }
            try {
                Thread.sleep(SLEEP_TIME);
                // System.out.println("线程执行完毕，当前连接：" + MsgServer.sessionMap);
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("连接清除线程异常:" + e);
            }
        }

    }

}
