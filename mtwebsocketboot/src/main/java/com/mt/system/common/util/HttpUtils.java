package com.mt.system.common.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    /**
     * 发送GET请求
     *
     * @param url        目的地址
     * @param parameters 请求参数，Map类型。
     * @return 远程响应结果
     */
    public static String sendGet(String url, Map<String, String> parameters) {
        String result = "";
        BufferedReader in = null;// 读取响应输入流
        StringBuffer sb = new StringBuffer();// 存储参数
        String params = "";// 编码之后的参数
        try {
            if (parameters != null && parameters.size() != 0) {
                // 编码请求参数
                if (parameters.size() == 1) {
                    for (String name : parameters.keySet()) {
                        sb.append(name).append("=").append(URLEncoder.encode(parameters.get(name), "UTF-8"));
                    }
                    params = sb.toString();
                } else {
                    for (String name : parameters.keySet()) {
                        sb.append(name).append("=").append(URLEncoder.encode(parameters.get(name), "UTF-8")).append("&");
                    }
                    String temp_params = sb.toString();
                    params = temp_params.substring(0, temp_params.length() - 1);
                }
                url = url + "?" + params;
            }
            // 创建URL对象
            URL connURL = new URL(url);
            // 打开URL连接
            HttpURLConnection httpConn = (HttpURLConnection) connURL.openConnection();
            // 设置通用属性
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
            // 建立实际的连接
            httpConn.connect();
            // 响应头部获取
            Map<String, List<String>> headers = httpConn.getHeaderFields();
            // 定义BufferedReader输入流来读取URL的响应,并设置编码方式
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line;
            }
            //返回
            return result;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                return null;
            }
        }
    }

    /**
     * 发送HttpPost请求
     *
     * @param urls 服务地址
     * @param jsonParam json字符串,例如: "{ \"id\":\"12345\" }" ;其中属性名必须带双引号<br/>
     * @return 成功:返回json字符串
     */
    public static String sendPost(String urls,Map jsonParam) {
        StringBuffer sb = new StringBuffer();
        HttpURLConnection conn =null;
        try {
            // 创建url资源
            URL url = new URL(urls);
            // 建立http连接
            conn = (HttpURLConnection) url.openConnection();
            // 设置允许输出
            conn.setDoOutput(true);
            // 设置允许输入
            conn.setDoInput(true);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("POST");
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 设置文件字符集:
            conn.setRequestProperty("Charset", "UTF-8");
            // 转换为字节数组
            byte[] data = jsonParam.toString().replace("{","").replace("}","").getBytes();
            // 设置文件长度
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 设置文件类型:
            conn.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // 设置发送数据的格式
            // 开始连接请求
            conn.connect();
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // 写入请求的字符串
            out.write(jsonParam.toString().replace("{","").replace("}","").getBytes());
            out.flush();
            out.close();
            // 请求返回的状态
            if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                System.out.println("连接成功!");
                // 请求返回的数据
                InputStream in1 = conn.getInputStream();
                String readLine = new String();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(in1, "UTF-8"));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine).append("\n");
                }
                responseReader.close();
                in1.close();
            } else{
                System.out.println("连接失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (conn != null) {
                conn.disconnect();//关闭连接
            }
        }
        return sb.toString();
    }
}
