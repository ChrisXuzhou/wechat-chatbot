package com.rokid.iot.portal.speak.application;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Builder
public class SpeakRequest {

    private String masterId;
    private String deviceId;
    private String deviceTypeId;

    /**
     * 控制语句
     */
    private String sentence;
    /**
     * 来源
     */
    private Source source;

    /**
     * 来源
     */
    public enum Source {
        /**
         * 微信
         */
        WECHAT,
        ;
    }

    public SpeakRequest legal() {
        Preconditions.checkArgument(StringUtils.isNotBlank(masterId), "masterId is empty");
        Preconditions.checkArgument(StringUtils.isNotBlank(deviceId), "deviceId is empty");
        Preconditions.checkArgument(StringUtils.isNotBlank(deviceTypeId), "deviceTypeId is empty");
        return this;
    }
}
