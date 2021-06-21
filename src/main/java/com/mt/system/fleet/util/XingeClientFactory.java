package com.mt.system.fleet.util;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mt.system.fleet.properties.FleetProperties;
import com.tencent.xinge.XingeApp;
import com.tencent.xinge.api.RESTAPIV3;

@Component
public class XingeClientFactory implements FactoryBean<XingeHttpClient> {

    @Autowired
    private FleetProperties fleetProperties;

    public XingeHttpClient getObject() throws Exception {
        XingeApp anXingeApp = new XingeApp.Builder().appId(fleetProperties.getAnAccessId())
            .secretKey(fleetProperties.getAnSecretKey()).domainUrl(RESTAPIV3.API_DOMAINS_GZ).build();
        XingeApp iosXingeApp = new XingeApp.Builder().appId(fleetProperties.getIosAccessId())
            .secretKey(fleetProperties.getIosSecretKey()).domainUrl(RESTAPIV3.API_DOMAINS_GZ).build();
        XingeHttpClient client = new XingeHttpClient();
        client.setAnXingeApp(anXingeApp);
        client.setIosXingeApp(iosXingeApp);
        return client;
    }

    public Class<?> getObjectType() {
        return XingeHttpClient.class;
    }

    public boolean isSingleton() {
        return true;
    }
}
