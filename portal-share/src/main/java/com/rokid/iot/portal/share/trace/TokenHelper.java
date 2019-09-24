package com.rokid.iot.portal.share.trace;

import java.util.UUID;

public class TokenHelper {

    public static String randomToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
