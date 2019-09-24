package com.rokid.iot.msg.domain;

import com.google.common.base.Preconditions;
import com.rokid.iot.portal.share.DateHelper;
import com.rokid.iot.portal.share.Traceable;
import com.rokid.iot.portal.share.trace.TokenHelper;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.MDC;

import java.util.Objects;

/**
 * 【微信】客服消息
 */
@Slf4j
@Getter
@Builder
public class KFMessage implements MsgConcern {

    private String voice;
    /**
     * 消息发送人
     */
    private User from;
    /**
     * 消息接收人
     */
    private User to;
    /**
     * 消息类型
     */
    private String msgType;
    /**
     * 文本消息
     */
    private String content;
    /**
     * 媒体信息
     */
    private String mediaId;

    private final String current = DateHelper.current();

    public KFMessage legal() {
        Preconditions.checkArgument(Objects.nonNull(to));
        to.legal();
        return this;
    }

}
