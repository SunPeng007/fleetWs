package com.mt.system.domain.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 读取application.properties文件
 * spring.profiles.active为环境：dev,test,prod
 * 设置成相应的环境，则加载相应properties，如application-test.properties、application-prod.properties.......
 * 其中检查带有prefix的词头，最后则为属性
 */
@Component
@ConfigurationProperties(prefix="com.customize")
@PropertySource("classpath:config/system.properties")//@PropertySource来指定自定义的资源目录
public class SystemProperties {

    /**
     * 统一共用（system.properties文件中的统一共用定制属性）定制属性
     */
    public static String systemSalt;//证书MD5加密盐
    public static String buApiUrl;//bu平台api接口地址

    public String getSystemSalt() {
        return systemSalt;
    }

    public void setSystemSalt(String systemSalt) {
        SystemProperties.systemSalt = systemSalt;
    }

    public String getBuApiUrl() {
        return buApiUrl;
    }

    public void setBuApiUrl(String buApiUrl) {
        SystemProperties.buApiUrl = buApiUrl;
    }
}
