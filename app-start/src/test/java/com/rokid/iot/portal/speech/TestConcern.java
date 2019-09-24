package com.rokid.iot.portal.speech;

import lombok.Builder;
import lombok.Getter;

public class TestConcern {

    @Getter
    @Builder
    public static class MockDevice {

        private String masterId;
        private String deviceId;
        private String deviceTypeId;
    }

    public static MockDevice mock() {
        return MockDevice.builder()
                .deviceId("WXoVcpRwwWBjlcCnbQuk40BUFCSk_U")
                .deviceTypeId("2148986BE3AB4126A4C448C9A8010DC3")
                .build();
    }

}
