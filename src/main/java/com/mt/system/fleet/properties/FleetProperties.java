package com.mt.system.fleet.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "fleet")
@PropertySource(value = "config/fleet.properties") // @PropertySource来指定自定义的资源目录
public class FleetProperties {

    private String apiGuangZhou;
    private String anAccessId;
    private String anSecretKey;
    private String iosAccessId;
    private String iosSecretKey;

    public String getApiGuangZhou() {
        return apiGuangZhou;
    }

    public void setApiGuangZhou(String apiGuangZhou) {
        this.apiGuangZhou = apiGuangZhou;
    }

    public String getAnAccessId() {
        return anAccessId;
    }

    public void setAnAccessId(String anAccessId) {
        this.anAccessId = anAccessId;
    }

    public String getAnSecretKey() {
        return anSecretKey;
    }

    public void setAnSecretKey(String anSecretKey) {
        this.anSecretKey = anSecretKey;
    }

    public String getIosAccessId() {
        return iosAccessId;
    }

    public void setIosAccessId(String iosAccessId) {
        this.iosAccessId = iosAccessId;
    }

    public String getIosSecretKey() {
        return iosSecretKey;
    }

    public void setIosSecretKey(String iosSecretKey) {
        this.iosSecretKey = iosSecretKey;
    }

    @Override
    public String toString() {
        return "FleetProperties [apiGuangZhou=" + apiGuangZhou + ", anAccessId=" + anAccessId + ", anSecretKey="
            + anSecretKey + ", iosAccessId=" + iosAccessId + ", iosSecretKey=" + iosSecretKey + "]";
    }

}
