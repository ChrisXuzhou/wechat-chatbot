package com.rokid.iot.portal.wechat.port.account;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class AccountNotFoundException extends RuntimeException {

    private String msg;

    public AccountNotFoundException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
