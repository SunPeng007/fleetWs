package com.mt.system.fleet.common;

public class XingeException extends BaseBusiException {

    public XingeException() {}

    public XingeException(String message) {
        super(message);
    }

    public XingeException(Throwable cause) {
        super(cause);
    }

    public XingeException(String message, Throwable cause) {
        super(message, cause);
    }

    public XingeException(BusiError responseCode) {
        super(responseCode);
    }

    public XingeException(BusiError responseCode, String message) {
        super(responseCode, message);
    }

    public XingeException(BusiError responseCode, Throwable cause) {
        super(responseCode, cause);
    }

    public XingeException(BusiError responseCode, String message, Throwable cause) {
        super(responseCode, message, cause);
    }
}