package com.mt.system.fleet.common;

import java.io.Serializable;

public abstract class BaseBusiException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 4148838182341182768L;
    private BusiError responseCode;

    public BaseBusiException() {
        this.responseCode = CommonErrors.UNKNOWN_ERR;
    }

    public BaseBusiException(String message) {
        super(message);
        this.responseCode = CommonErrors.UNKNOWN_ERR;
    }

    public BaseBusiException(Throwable cause) {
        super(cause);
        this.responseCode = CommonErrors.UNKNOWN_ERR;
    }

    public BaseBusiException(String message, Throwable cause) {
        super(message, cause);
        this.responseCode = CommonErrors.UNKNOWN_ERR;
    }

    public BaseBusiException(BusiError responseCode) {
        super(responseCode.getErrorMsg());
        this.responseCode = responseCode;
    }

    public BaseBusiException(BusiError responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }

    public BaseBusiException(BusiError responseCode, Throwable cause) {
        super(responseCode.getErrorMsg(), cause);
        this.responseCode = responseCode;
    }

    public BaseBusiException(BusiError responseCode, String message, Throwable cause) {
        super(message, cause);
        this.responseCode = responseCode;
    }

    public BusiError getResponseCode() {
        return this.responseCode;
    }
}
