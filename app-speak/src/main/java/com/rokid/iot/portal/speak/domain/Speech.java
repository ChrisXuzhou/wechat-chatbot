package com.rokid.iot.portal.speak.domain;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Data
@Builder
public class Speech {

    private Speaker speaker;
    private String sentence;

    private Resp resp;

    public Speech legal() {
        Preconditions.checkArgument(Objects.nonNull(speaker), "speaker is not empty");
        Preconditions.checkArgument(StringUtils.isNotBlank(sentence), "traceVoice is not empty");
        speaker.legal();
        return this;
    }

    @Getter
    @Builder
    public static class Speaker {

        private String masterId;
        private String deviceId;
        private String deviceTypeId;

        Speaker legal() {
            Preconditions.checkArgument(StringUtils.isNotBlank(masterId), "masterId is not empty");
            Preconditions.checkArgument(StringUtils.isNotBlank(deviceId), "deviceId is not empty");
            Preconditions.checkArgument(StringUtils.isNotBlank(deviceTypeId), "deviceTypeId is not empty");
            return this;
        }
    }

    @Getter
    @Builder
    public static class Resp {
        /**
         * 是否来自于IOT的服务
         */
        private boolean fromIot;
        /**
         * 响应的TTS
         */
        private String voice;
    }

}
