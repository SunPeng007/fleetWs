package com.mt.system.domain.constant;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/4/10
 * Time:18:09
 */
public interface ConnectTimeConstant {

    /**
     * 定时清理未回应，已接收数据，重发机制。[线程休息时间]
     */
    long CLOSE_PUSH_TIME_CODE=5000;//毫秒
    /**
     * 定时执行清理接收数据，[线程休息时间]
     */
    long CLOSE_RECEIVE_TIME_CODE=120000;//毫秒

    /**
     * 定时执行清理连接。[线程休息时间]
     */
    long CLOSE_SESSION_TIME_CODE=120000;//毫秒

    /**
     * 连接有效时间
     */
    long EFFECTIVE_TIME_CODE=1800;//单位秒，分钟30*60
    /**
     * 接收数据有效时间
     */
    long CLOSE_TIME_DATA_CODE=600;//秒
    /**
     * 最长允许回应时间
     */
    long ANSWER_TIME_CODE=5;//秒
}

