package com.rokid.iot.portal.share;

public class SupportException extends RuntimeException {

    private String msg;

    public SupportException(String msg, Exception e) {
        super(msg, e);
    }
}
