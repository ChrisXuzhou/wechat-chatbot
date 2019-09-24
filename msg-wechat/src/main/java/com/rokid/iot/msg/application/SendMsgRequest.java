package com.rokid.iot.msg.application;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Getter
@Builder
public class SendMsgRequest {

    private String toUserOpenId;
    private String fromUserOpenId;

    private MsgType msgType;
    private String content;

    public SendMsgRequest legal() {
        Preconditions.checkArgument(StringUtils.isNotBlank(toUserOpenId));
        Preconditions.checkArgument(StringUtils.isNotBlank(fromUserOpenId));
        Preconditions.checkArgument(StringUtils.isNotBlank(content));
        Preconditions.checkArgument(Objects.nonNull(msgType));
        return this;
    }

    public enum MsgType {
        /**
         * 文本信息
         */
        TEXT,
        ;

    }
}
