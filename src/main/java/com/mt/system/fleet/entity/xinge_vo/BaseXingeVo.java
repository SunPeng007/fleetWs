package com.mt.system.fleet.entity.xinge_vo;

import java.io.Serializable;

public abstract class BaseXingeVo implements Serializable {

    private static final long serialVersionUID = 1L;
    protected Integer ret_code;
    protected String err_msg;
    protected String result;

    @Override
    public String toString() {
        return "BaseXingeVo [ret_code=" + ret_code + ", err_msg=" + err_msg + ", result=" + result + "]";
    }

    public Integer getRet_code() {
        return ret_code;
    }

    public void setRet_code(Integer ret_code) {
        this.ret_code = ret_code;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
