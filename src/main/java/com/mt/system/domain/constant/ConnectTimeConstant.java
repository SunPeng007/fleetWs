package com.mt.system.domain.constant;

/**
 * Created with IDEA
 * author: Alnwick
 * Date:2019/4/10
 * Time:18:09
 */
public interface ConnectTimeConstant {
    /**
     * 连接有效时间
     */
    long EFFECTIVE_TIME_CODE=1800;//单位秒，分钟30*60

    /**
     * 定时清理未回应连接
     */
    long SLEEP_TIME_CODE=5;//秒

    /**
     * 最长允许回应时间
     */
    long ANSWER_TIME_CODE=5;//秒
}

