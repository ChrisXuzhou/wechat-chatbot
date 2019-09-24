package com.rokid.iot.portal.wechat.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MediaDescriptor {

    private String media_id;
    private String name;
}
