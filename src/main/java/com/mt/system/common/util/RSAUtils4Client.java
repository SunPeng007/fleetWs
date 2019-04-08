package com.mt.system.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mt.system.domain.properties.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * RSA公钥/私钥/签名工具包
 * </p>
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 * </p>
 * @author chenpan
 * @date 2018-10-31
 */
public class RSAUtils4Client {
    /** *//**
     * 请求参数---公钥的key
     */
    public static final String REQ_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDLKTH7I/2Domo8ZQIhvR6FXrEBEFgbvHGNVJlx91abYOlrWdo2+P68P2r16lmfy6zy8GN2i+P+C4K/JoR5L7azBLN2/sG/TvDCYDDimbfzny/U3f1XrXCwlFujpuN23tdM5oq1ZZxyY7tRDwu7JwmVmfAHiZ404CJySIEuA27LZwIDAQAB";
    /** *//**
     * 请求参数---私钥的key
     */
    public static final String REQ_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMspMfsj/YOiajxlAiG9HoVesQEQWBu8cY1UmXH3Vptg6WtZ2jb4/rw/avXqWZ/LrPLwY3aL4/4Lgr8mhHkvtrMEs3b+wb9O8MJgMOKZt/OfL9Td/VetcLCUW6Om43be10zmirVlnHJju1EPC7snCZWZ8AeJnjTgInJIgS4DbstnAgMBAAECgYBs8CwS5Nud6EtbEzavbL+e+IWlcmQsi0HAbSrSx7QzISZJICc47w95VfA+6WkhnI+ivXA4Mw8QPI/he//xE7q9Ev6FlpbA5qOI1FLyVwPUycE8+4Wh60gYRuL4IJ3zDiS+L7ujNEawbxmXACNn7gf//6iNxG5I+7ccrbhMDbuXYQJBAOxXS+vPtlkoKiNH3odiU/mgVyopfpiRaPkTqZp4O86HczzLUvJEh8+S011U3dotRgue9RxI3Kd9OaBKe79oJTkCQQDcD1qTsXs/U7jrEz94ujZt2MFmtPb8aPMj54x1xSQo69QYFVolh1lsu6gI98VaJgC81TxZz1bHZXKqp3ruQhWfAkBs5LwCZq3p3mur3c1WK6PtfEctEflWaoknWjvnBEbqHdamyDfrkE/TAB+K/TNK91kyuYbOBXRFZ5lJlYf25RFhAkBmLzsAGgoA/f+AKQ/wH4fmuiAUcTO/QXylz3+JsGF6Hwf9puyVQKRluT3t7B/YHI17IglEKuNA8/qrA9oxiL5BAkA2uIR4H50bXnAHzeLDLeLwYokZEDB5kwJJItE99TG+eKg12M1+69m0SKiHiPXpbRNOCAdN9F3SOgivexEfLvEQ";

    /** *//**
     * 响应参数---公钥的key
     */
    public static final String RES_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgWvg7bet5U04cxYOrdsF10uRKQxrhJPD0k3F+n9ig2H7Gu8GpxPcJy0j/P9nnjMDquSaHIw0KTvDkXhQ9EV2hdy4vCJhg+uzKZjfbZM7/3b+IRSOwPg1Zm7MOTbCKz1Qajh87gn1s8YQxmMF+kUm1EOW9swDL+BoI11kgRPBESQIDAQAB";
    /** *//**
     * 响应参数---私钥的key
     */
    public static final String RES_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKBa+Dtt63lTThzFg6t2wXXS5EpDGuEk8PSTcX6f2KDYfsa7wanE9wnLSP8/2eeMwOq5JocjDQpO8OReFD0RXaF3Li8ImGD67MpmN9tkzv/dv4hFI7A+DVmbsw5NsIrPVBqOHzuCfWzxhDGYwX6RSbUQ5b2zAMv4GgjXWSBE8ERJAgMBAAECgYBIW+RpxNcd/1ZfK02YINV5mnmDfTZy5B2K78GmFvFE24yRlwXz2rpzA/VjoOduUhh3kqbtEgb/YNHY6w8M43ow+TPQMcAm4JirVrbd5d4jfa1yZnbAJfDBbwyQgeMA/PXsm2vXpPlOMNX7FgPOwINdJLhOro/dBpqUZyY8/9OBEQJBAPQNpHyf+CmtB54Jk7KL2s/e0rG3kMTSLTga0wUvIsj6MoUwr7iaGGVO3vyHLcSijbvIAuXUVmpjgWGa014ifoUCQQCoNHcSxF6YnzMobCbY2ByzxbeuIGGsPrgmkOdAD5mO0FB5HeNcHWNmrkFbmVT139kr/Cf4ZB2dyHCBT8W4bSP1AkBGdrHpMZ7LGaQ7YvR79plEr0cR/LgVu8FGk6gdtRBMCsEPZ5E0sXkdtvn222jBf1WAxCziAr64lOZEMhmY8kipAkBGrOzPXOVmeZYUSQU1nfDJdK1OI9sGdcAIrGAOYb+i61K04WMT5GFM1rEtnMq/GRR2T9g3nVF4Z60mGG+6o1k1AkEAow+0FAHpXUTYAbJVzlC0lBamYiatNZ66sE4CCBiE99ROf0b1X/HocrCDo0GlYl3kdEZuuclKqEHehBUj+fNyIg==";
    /**
     * POST请求参数解密----私钥解密
     * @param dpString 加密后的字符串参数
     * @return
     */
    public static Map<String,Object> decryptionPostParam(String dpString,String privateKey,String entityKey,Class clazz){
        try{
            if(dpString==null || dpString==""){
                return null;
            }
            byte[] dpByte = Base64Util.decodeString(dpString);
            byte[] value =RSAUtils.decryptByPrivateKey(dpByte,privateKey);
            Map<String,Object> resultMap = JsonUtil.toObject(new String(value),new HashMap<String,Object>().getClass());//私钥解密
            resultMap.put(entityKey,mapToEntity((Map)resultMap.get(entityKey),clazz));
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * POST请求参数解密----私钥解密
     * @param dpString 加密后的字符串参数
     * @return
     */
    public static Map<String,Object> decryptionPostParam(String dpString,String privateKey){
        try{
            if(dpString==null || dpString==""){
                return null;
            }
            byte[] dpByte = Base64Util.decodeString(dpString);
            byte[] value =RSAUtils.decryptByPrivateKey(dpByte,privateKey);
            return JsonUtil.toObject(new String(value),new HashMap<String,Object>().getClass());//私钥解密
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * GET请求参数解密----私钥解密
     * @param dpString 加密后的字符串参数
     * @return
     */
    public static Map<String,Object> decryptionGetParam(String dpString,String privateKey){
        try{
            if(dpString==null||dpString==""){
                return null;
            }
            Map<String,Object> resultMap=new HashMap<>();
            byte[] dpByte = Base64Util.decodeString(dpString);
            byte[] value =RSAUtils.decryptByPrivateKey(dpByte,privateKey);//私钥解密
            String paramStr=new String(value);
            //根据&拆分
            String[] paramKey=paramStr.split("&");
            for (int i=0;i<paramKey.length;i++){
                String[] param=paramKey[i].split("=");
                if(param!=null||param.length>0){
                    resultMap.put(param[0].trim(),param[1].trim());
                }
            }
            return resultMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * md5加密
     * @param data
     * @param token
     * @param timestamp
     * @return
     */
    public static String createMd5(Map<String, Object> data, String token, Long timestamp) {
        try {
            //加上盐, 生成Md5
            return DigestUtils.md5DigestAsHex((token + timestamp + SystemProperties.systemSalt).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 响应对象加密----公钥加密
     * @param obj 需要加密对象
     * @return
     */
    public static String encryptionResult(Object obj,String publicKey) {
        try {
            byte[] data = JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect).getBytes();
            byte[] encodedData = RSAUtils.encryptByPublicKey(data,publicKey);//公钥加密
            return Base64Util.encodeByte(encodedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将请求参数数据包加密成字符串
     * @param data
     * @param token
     * @param timestamp
     * @return
     */
    public static String encryptionDataPacket(Map<String, Object> data, String token, Long timestamp){
        //请求MoTooling企业站点-->参数
        Map<String,Object> objMap=new HashMap<>();
        //参数
        objMap.put("data",data);
        //时间戳
        objMap.put("timestamp",timestamp);
        //token
        objMap.put("token",token);
        //MD5加密
        objMap.put("md5",RSAUtils4Client.createMd5(data,token, timestamp));
        //私钥加密
        return encryptionResult(objMap,REQ_PUBLIC_KEY);
    }
    /**
     * Map对象转换成实体
     * @param map
     * @param entity
     * @param <T>
     * @return
     */
    public static <T> T mapToEntity(Map<String, Object> map, Class<T> entity) {
        T t = null;
        try {
            t = entity.newInstance();
            for(Field field : entity.getDeclaredFields()) {
                if (map.containsKey(field.getName())) {
                    boolean flag = field.isAccessible();
                    field.setAccessible(true);
                    Object object = map.get(field.getName());
                    if (object!= null && field.getType().isAssignableFrom(object.getClass())) {
                        field.set(t, object);
                    }
                    field.setAccessible(flag);
                }
            }
            return t;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }
    //测试方法
//    public static void main(String[] args) throws Exception {
//        //获取公钥和私钥
//        Map<String, Object> keys = RSAUtils.genKeyPair();
//        String privateKey = RSAUtils.getPrivateKey(keys);
//        String publicKey = RSAUtils.getPublicKey(keys);
//        System.out.println("publicKey：\r\n"+publicKey);
//        System.out.println("privateKey：\r\n"+privateKey);
//
//        String source = "123456";
//        System.out.println("原文字：\r\n" + source);
//        byte[] data = source.getBytes();
//        byte[] encodedData = RSAUtils.encryptByPublicKey(data, publicKey);
//        System.out.println("加密后：\r\n" + new String(encodedData));
//        String base64EncodedData = Base64Util.encodeByte(encodedData);
//        System.out.println("1加密后BASE64：\r\n" + base64EncodedData);

//        System.out.println("2加密后BASE64：\r\n" + new BASE64Encoder().encode(encodedData));
//        byte[] jmh = Base64Util.decodeString(base64EncodedData);
//        byte[] value =RSAUtils.decryptByPrivateKey(jmh,privateKey);
//        System.out.println("解密后：\r\n" + new String(value));

//        Map<String,Object> paramMap = decryptionPostParam("Nquc547nawLEg7IjgVzXjbDygCDxal57lDdEdezJqtAVC3dhXb7FSokSJ4EDCwytsUhS6ePBKtKp8ZKSvA/4mN6utAk3I2dYhJrvC0MRmQTirvS4doOECJ8vm8bW+r2grmVOSDaCLl6PN8/9RbGJ17+gFY24Ua1YY+HVA2USKws=");
//        System.out.println("解密后：\r\n" + paramMap.get("id"));
//          Map<String,Object> result = decryptionGetParam("Nquc547nawLEg7IjgVzXjbDygCDxal57lDdEdezJqtAVC3dhXb7FSokSJ4EDCwytsUhS6ePBKtKp8ZKSvA/4mN6utAk3I2dYhJrvC0MRmQTirvS4doOECJ8vm8bW+r2grmVOSDaCLl6PN8/9RbGJ17+gFY24Ua1YY+HVA2USKws=");
//          System.out.println(""+result.get("id"));
//    }
}