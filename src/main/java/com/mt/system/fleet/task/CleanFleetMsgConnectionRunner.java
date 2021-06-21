package com.mt.system.fleet.task;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class CleanFleetMsgConnectionRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        // 清除连接
        SessionPingRunnable sessionMsgRunnable = new SessionPingRunnable();
        new Thread(sessionMsgRunnable).start();
    }
}
