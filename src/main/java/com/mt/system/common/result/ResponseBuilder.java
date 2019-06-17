package com.mt.system.common.result;

import com.mt.system.common.util.JsonUtil;
import com.mt.system.common.util.RSAUtils4Client;
import com.mt.system.domain.constant.CodeConstant;

/**
 * 相应结果的构造类
 * @param <T>
 * Created by chenpan on 4/27/18.
 */
public class ResponseBuilder<T> {

    private ResponseResult response = new ResponseResult();


    /** 公共设置 - start **/
    /**
     * 设置返回status
     * @param status
     * @return
     */
    public ResponseBuilder setStatus(Integer status) {
        response.setStatus(status);
        return this;
    }

    /** 公共设置 - start **/
    /**
     * 设置返回code
     * @param codeNum
     * @return
     */
    public ResponseBuilder setCode(String codeNum) {
        response.setCode(codeNum);
        return this;
    }

    /**
     * 设置返回data
     * @param data
     * @return
     */
    public ResponseBuilder setData(T data) {
        if(data instanceof String){
            response.setData((String)data);
        }else{
            response.setData(data);
        }
        return this;
    }

    /**
     * 设置返回msg
     * @param message
     * @return
     */
    public ResponseBuilder setMsg(String message) {
        response.setMsg(message);
        return this;
    }

    /**
     * 设置返回消息、返回状态码
     * @param code 状态码
     * @param msg 消息
     * @return
     */
    public ResponseBuilder setCodeAndMessage(String code, String msg){
        if(CodeConstant.DEFAULT_RESPONSE_SUCCESS_CODE.equals(code.trim()))
            response.setStatus(0);//成功
        else
            response.setStatus(1);//失败
        return setCode(code).setMsg(msg);
    }
    /** 公共设置 - end **/

    /** 设置公用的结果集处理 - start **/
    /**
     * 返回成功 - 固定格式（code=000000），自定义消息与返回(data={#{data}})
     * @param data 设置返回数据
     * @return
     */
    public ResponseBuilder setSuccessResult(T data, String msg){
        response.setStatus(0);//成功
        return setCode(CodeConstant.DEFAULT_RESPONSE_SUCCESS_CODE).setData(data).setMsg(msg);
    }

    /**
     * 返回异常 - 固定格式（code=999999、data={}、msg=网络异常）
     * @return
     */
    public ResponseBuilder setFailResult(){
        response.setStatus(1);//失败
        return setCodeAndMessage(CodeConstant.DEFAULT_RESPONSE_FAIL_CODE,"网络异常").setData(JsonUtil.toNullJson());
    }

    /**
     * 返回异常 - 固定格式（code=999999、data={}、msg=数据库链接失败）
     * @return
     */
    public ResponseBuilder setDBLinkFailResult(){
        response.setStatus(1);//失败
        return setCodeAndMessage(CodeConstant.DEFAULT_RESPONSE_FAIL_CODE,"数据库链接失败").setData(JsonUtil.toNullJson());
    }

    /**
     * 返回异常 - 固定格式（code=999999、data={}）自定义消息（msg=#{msg}）
     * @param msg 消息
     * @return
     */
    public ResponseBuilder setFailResult(String msg){
        response.setStatus(1);//失败
        return setCodeAndMessage(CodeConstant.DEFAULT_RESPONSE_FAIL_CODE,msg).setData(JsonUtil.toNullJson());
    }

    /**
     * 返回token找不到或者过期 - 固定格式（code=444444、data={}）自定义消息（msg=#{msg}）
     * @param msg 消息
     * @return
     */
    public ResponseBuilder setNotTokenResult(String msg){
        response.setStatus(1);//失败
        return setCodeAndMessage(CodeConstant.NOT_FIND_TOKEN,msg).setData(JsonUtil.toNullJson());
    }

    /**
     * 返回参数异常 - 固定格式（code=555555、data={}）自定义消息（msg=#{msg}）
     * @param msg 消息
     * @return
     */
    public ResponseBuilder setParamFailResult(String msg){
        response.setStatus(1);//失败
        return setCodeAndMessage(CodeConstant.DEFAULT_PARAM_FAIL_CODE,msg).setData(JsonUtil.toNullJson());
    }
    /**
     * 自定义返回结果 - 自定义返回data、返回消息、返回状态码
     * @param code 状态码
     * @param msg 消息
     * @param data data
     * @return
     */
    public ResponseBuilder setResult(String code, String msg, T data){
        if(CodeConstant.DEFAULT_RESPONSE_SUCCESS_CODE.equals(code.trim()))
            response.setStatus(0);//成功
        else
            response.setStatus(1);//失败
        return setCode(code).setMsg(msg).setData(data);
    }
    /** 设置公用的结果集处理 - end **/

    public ResponseResult build() {
        return response;
    }
    /** 返回加密后字符串 - end **/
    public String encryptionResult() {
        return RSAUtils4Client.encryptionResult(response,RSAUtils4Client.RES_PUBLIC_KEY);
    }
}
