package com.mt.system.config;

import com.mt.system.domain.thread.im.CleanPushRunnable;
import com.mt.system.domain.thread.im.CleanReceiveRunnable;
import com.mt.system.domain.thread.im.CleanSessionRunnable;
import com.mt.system.domain.thread.msg.CleanMsgPushRunnable;
import com.mt.system.domain.thread.msg.CleanMsgReceiveRunnable;
import com.mt.system.domain.thread.msg.CleanMsgSessionRunnable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Created with IDEA
 * author: chenpan
 * Date:2019/4/13
 * Time:15:20
 * UpdateDate:2020/5/28
 */
@Component
public class CleanEchoApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        //开启线程执行
        /*----[协同数据清除]----*/
        // 清除/重发消息
        CleanPushRunnable pushRunnable=new CleanPushRunnable();
        new Thread(pushRunnable).start();
        //清除接收数据
        CleanReceiveRunnable receiveRunnable = new CleanReceiveRunnable();
        new Thread(receiveRunnable).start();
        //清除连接
        CleanSessionRunnable sessionRunnable = new CleanSessionRunnable();
        new Thread(sessionRunnable).start();
        /*----[消息数据清除]----*/
        // 清除/重发消息
        CleanMsgPushRunnable msgPushRunnable=new CleanMsgPushRunnable();
        new Thread(msgPushRunnable).start();
        //清除接收数据
        CleanMsgReceiveRunnable msgReceiveRunnable = new CleanMsgReceiveRunnable();
        new Thread(msgReceiveRunnable).start();
        //清除连接
        CleanMsgSessionRunnable sessionMsgRunnable = new CleanMsgSessionRunnable();
        new Thread(sessionMsgRunnable).start();
    }
}
