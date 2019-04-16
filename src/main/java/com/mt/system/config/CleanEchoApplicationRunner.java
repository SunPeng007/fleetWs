package com.mt.system.config;

import com.mt.system.domain.thread.CleanPushRunnable;
import com.mt.system.domain.thread.CleanReceiveRunnable;
import com.mt.system.domain.thread.CleanSessionRunnable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Created with IDEA
 * author: chenpan
 * Date:2019/4/13
 * Time:15:20
 */
@Component
public class CleanEchoApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        //开启线程执行
        // 清除/重发消息
        CleanPushRunnable pushRunnable=new CleanPushRunnable();
        new Thread(pushRunnable).start();
        //清除接收数据
        CleanReceiveRunnable receiveRunnable = new CleanReceiveRunnable();
        new Thread(receiveRunnable).start();
        //清除连接
        CleanSessionRunnable sessionRunnable = new CleanSessionRunnable();
        new Thread(sessionRunnable).start();
    }
}
