package com.rokid.iot.portal.speak.domain;

import org.apache.commons.lang3.StringUtils;

public interface SmartHomeConcern {

    /**
     * TODO: UNSTABLE: 通过APP-ID判断
     *
     * @param appId 技能Id
     * @return true/false
     */
    default boolean isFromSmartHome(String appId) {
        return StringUtils.equals(appId, "R4851AB35A0E4D338F455D470AEC95D3");

    }

}
