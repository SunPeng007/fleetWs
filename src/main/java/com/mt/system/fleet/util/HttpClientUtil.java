package com.mt.system.fleet.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

@Component
public class HttpClientUtil {

    private static HttpClient httpClient = HttpClientBuilder.create().build();

    public static String get(String url, Map<String, String> params) throws IOException, URISyntaxException {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            pairs.add(basicNameValuePair);
        }
        return get(url, pairs);
    }

    private static String get(String url, List<NameValuePair> params) throws IOException, URISyntaxException {
        String body = null;
        HttpGet httpget = new HttpGet(url);
        String str = EntityUtils.toString((HttpEntity)new UrlEncodedFormEntity(params));
        httpget.setURI(new URI(httpget.getURI().toString() + "?" + str));
        HttpResponse httpresponse = httpClient.execute((HttpUriRequest)httpget);
        HttpEntity entity = httpresponse.getEntity();
        body = EntityUtils.toString(entity, "UTF-8");
        return body;
    }

    public static String post(String url, Map<String, String> params) throws ClientProtocolException, IOException {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            pairs.add(basicNameValuePair);
        }
        return post(url, pairs);
    }

    public static String post(String url, List<NameValuePair> params) throws ClientProtocolException, IOException {
        String body = null;
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity((HttpEntity)new UrlEncodedFormEntity(params, "UTF-8"));
        HttpResponse httpresponse = httpClient.execute((HttpUriRequest)httppost);
        HttpEntity entity = httpresponse.getEntity();
        body = EntityUtils.toString(entity, "UTF-8");
        return body;
    }

    public static String postForJson(String url, Map<String, Object> params) throws IOException {
        String body = null;
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new StringEntity(JSON.toJSONString(params)));
        httppost.addHeader("Content-Type", "application/json;charset=utf-8");
        HttpResponse httpresponse = httpClient.execute(httppost);
        HttpEntity entity = httpresponse.getEntity();
        body = EntityUtils.toString(entity, "UTF-8");
        return body;
    }

    public static String getUrlApi(String domain, String url) {
        if (!domain.startsWith("http")) {
            domain = "http://" + domain;
        }
        if (domain.endsWith("/") && url.startsWith("/")) {
            return domain + url.substring(1);
        }
        if (!domain.endsWith("/") && !url.startsWith("/")) {
            return domain + "/" + url;
        }
        return domain + url;
    }
}
