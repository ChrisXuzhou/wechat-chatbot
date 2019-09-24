package com.rokid.iot.portal.wechat.port.wechat;

import lombok.Getter;

@Getter
public class WechatMsgException extends RuntimeException {

    private String msg;

    public WechatMsgException(Throwable throwable) {
        super(throwable);
        this.msg = throwable.getMessage();
    }
}
