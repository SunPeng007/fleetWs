package com.mt.system.fleet.common;

import java.io.Serializable;

public class BusiError implements Serializable {
    private static final long serialVersionUID = -9055099118038525164L;
    private String errorCode;
    private String errorMsg;

    public BusiError(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public boolean equals(Object o) {
        return (o != null && o.getClass().getName().equals("") && this.errorCode != null
            && this.errorCode.equals(((BusiError)o).getErrorCode()));
    }

    @Override
    public int hashCode() {
        if (this.errorCode != null) {
            return this.errorCode.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public String toString() {
        if (this.errorCode != null) {
            return "errorCode=" + this.errorCode + ",errorMsg=" + this.errorMsg;
        }
        return super.toString();
    }
}