package com.mt.system.fleet.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mt.system.common.util.DateUtils;

@Component
public class HttpClientUtil {

    private static HttpClient httpClient = HttpClientBuilder.create().build();
    private static String AN_ACCESS_ID;
    private static String IOS_ACCESS_ID;
    private static String AN_SECRET_KEY;
    private static String IOS_SECRET_KEY;

    public static <T> T xingeIosGet(String url, Map<String, String> params, Class<T> clazz)
        throws IOException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            pairs.add(basicNameValuePair);
        }
        T vo = JSONObject.parseObject(xingeGet(url, pairs, "ios"), clazz);
        return vo;
    }

    public static <T> T xingeAndroidGet(String url, Map<String, String> params, Class<T> clazz)
        throws IOException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            pairs.add(basicNameValuePair);
        }
        T vo = JSONObject.parseObject(xingeGet(url, pairs, "andriod"), clazz);
        return vo;
    }

    public static String get(String url, Map<String, String> params) throws IOException, URISyntaxException {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            pairs.add(basicNameValuePair);
        }
        return get(url, pairs);
    }

    private static String xingeGet(String url, List<NameValuePair> params, String env)
        throws IOException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException {
        String body, accessId = null, appSecret = null, timeStamp;
        HttpGet httpget = new HttpGet(url);
        if (Objects.equals(env, "andriod")) {
            appSecret = AN_SECRET_KEY;
            accessId = AN_ACCESS_ID;
        }
        if (Objects.equals(env, "ios")) {
            accessId = IOS_ACCESS_ID;
            appSecret = IOS_SECRET_KEY;
        }
        httpget.addHeader("AccessId", accessId);
        timeStamp = DateUtils.currentTimeMillis();
        httpget.addHeader("TimeStamp", timeStamp);
        String paramsBody = JSON.toJSONString(params);
        String stringToSign = timeStamp + accessId + paramsBody;

        Mac mac;
        mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(appSecret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signatureBytes = mac.doFinal(stringToSign.getBytes("UTF-8"));

        String hexStr = Hex.encodeHexString(signatureBytes);
        String signature = Base64.encodeBase64String(hexStr.getBytes());
        httpget.addHeader("Sign", signature);

        String str = EntityUtils.toString((HttpEntity)new UrlEncodedFormEntity(params));
        httpget.setURI(new URI(httpget.getURI().toString() + "?" + str));
        HttpResponse httpresponse = httpClient.execute((HttpUriRequest)httpget);
        HttpEntity entity = httpresponse.getEntity();
        body = EntityUtils.toString(entity, "UTF-8");
        return body;
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

    public static <T> T xingeIosPost(String url, Map<String, String> params, Class<T> clazz)
        throws ClientProtocolException, IOException, InvalidKeyException, NoSuchAlgorithmException, URISyntaxException {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            pairs.add(basicNameValuePair);
        }
        String bodyJsonStr = JSON.toJSONString(params);
        T vo = JSONObject.parseObject(xingePost(url, pairs, bodyJsonStr, "ios"), clazz);
        return vo;
    }

    public static <T> T xingeAndroidPost(String url, Map<String, String> params, Class<T> clazz)
        throws ClientProtocolException, IOException, InvalidKeyException, NoSuchAlgorithmException, URISyntaxException {
        List<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            pairs.add(basicNameValuePair);
        }
        String bodyJsonStr = JSON.toJSONString(params);
        T vo = JSONObject.parseObject(xingePost(url, pairs, bodyJsonStr, "andriod"), clazz);
        return vo;
    }

    private static String xingePost(String url, List<NameValuePair> params, String bodyJsonStr, String env)
        throws IOException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException {
        String body, accessId = null, appSecret = null, timeStamp;
        HttpPost httppost = new HttpPost(url);
        if (Objects.equals(env, "andriod")) {
            accessId = AN_ACCESS_ID;
            appSecret = AN_SECRET_KEY;
        }
        if (Objects.equals(env, "ios")) {
            accessId = IOS_ACCESS_ID;
            appSecret = IOS_SECRET_KEY;
        }
        httppost.addHeader("AccessId", accessId);
        timeStamp = DateUtils.currentTimeMillis();
        timeStamp = timeStamp.substring(0, timeStamp.length() - 3);
        httppost.addHeader("TimeStamp", timeStamp);
        String stringToSign = timeStamp + accessId + bodyJsonStr;
        System.out.println("stringToSign:" + stringToSign);
        System.out.println("appSecret:" + appSecret);

        Mac mac;
        mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(appSecret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signatureBytes = mac.doFinal(stringToSign.getBytes("UTF-8"));

        String hexStr = Hex.encodeHexString(signatureBytes);
        String signature = Base64.encodeBase64String(hexStr.getBytes());
        httppost.addHeader("Sign", signature);

        httppost.addHeader("Content-Type", "application/json;charset=utf-8");

        StringEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
        // entity.setContentEncoding("UTF-8");
        // entity.setContentType("application/json");
        httppost.setEntity(entity);
        HttpResponse httpresponse = httpClient.execute((HttpUriRequest)httppost);
        HttpEntity entityResp = httpresponse.getEntity();
        body = EntityUtils.toString(entityResp, "UTF-8");
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

    @Value("${fleet.anAccessId}")
    public void setAN_ACCESS_ID(String anAccessId) {
        HttpClientUtil.AN_ACCESS_ID = anAccessId;
    }

    @Value("${fleet.anSecretKey}")
    public void setAN_SECRET_KEY(String anSecretKey) {
        HttpClientUtil.AN_SECRET_KEY = anSecretKey;
    }

    @Value("${fleet.iosAccessId}")
    public void setIOS_ACCESS_ID(String iosAccessId) {
        HttpClientUtil.IOS_ACCESS_ID = iosAccessId;
    }

    @Value("${fleet.iosSecretKey}")
    public void setIOS_SECRET_KEY(String iosSecretKey) {
        HttpClientUtil.IOS_SECRET_KEY = iosSecretKey;
    }

}
