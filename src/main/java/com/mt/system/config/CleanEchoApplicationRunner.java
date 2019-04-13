package com.mt.system.config;

import com.mt.system.domain.entity.CleanEchoRunnable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/4/13
 * Time:15:20
 */
@Component
public class CleanEchoApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        //开启线程执行（清除/重发消息）
        System.out.println("开启线程执行:清除/重发消息");
        CleanEchoRunnable runRunnable=new CleanEchoRunnable();
        new Thread(runRunnable).start();
    }
}
