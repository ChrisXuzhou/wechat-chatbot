package com.rokid.iot.portal.share.wechat;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

public interface WechatConcern {

    default String asDeviceId(String openId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(openId), "wechat openId must not be empty");
        return wechatPrefix() + openId;
    }

    default String wechatPrefix() {
        return "WX";
    }

    /**
     * 预处理【语句】
     * <p>
     * 1、过滤【表情】
     *
     * @param sentence 语句
     * @return 过滤结果
     */
    default String preHandle(String sentence) {
        return sentence;
    }

    /**
     * @return 返回微信 DeviceTypeId
     */
    default String wechatDeviceTypeId() {
        return "2148986BE3AB4126A4C448C9A8010DC3";
    }

    /**
     * 解析openId
     */
    default String ofOpenId(String deviceId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(deviceId));
        return deviceId.substring(2);
    }


}
