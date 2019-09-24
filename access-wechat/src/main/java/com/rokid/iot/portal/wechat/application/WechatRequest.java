package com.rokid.iot.portal.wechat.application;

import com.google.common.base.Preconditions;
import com.rokid.iot.portal.share.Traceable;
import com.rokid.iot.portal.share.trace.TokenHelper;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.MDC;

@Data
@Builder
public class WechatRequest extends Traceable {
    private String URL;
    private String ToUserName;
    private String FromUserName;
    private String CreateTime;
    private String MsgType;
    private String MsgId;
    private String Content;

    private String MediaId;
    private String Format;
    private String Recognition;

    WechatRequest legal() {
        Preconditions.checkArgument(StringUtils.isNotBlank(getFromUserName()), "FromUserName is empty");
        return this;
    }

    String getText() {
        if (StringUtils.equals(getMsgType(), "text")) {
            return getContent();
        }
        if (StringUtils.equals(getMsgType(), "voice")) {
            return getRecognition();
        }
        if (StringUtils.equals(getMsgType(), "event")) {
            return getEvent();
        }
        return "";
    }

    public WechatRequest traceAble() {

        String origin = (String) MDC.get("traceId");
        String traceId = StringUtils.isBlank(origin) ? TokenHelper.randomToken() : origin;
        setTraceId(traceId);

        setTraceVoice(getText());
        return this;
    }

    private String Event;
    private String Latitude;
    private String Longitude;
    private String EventKey;
    private String Label;
    private String Precision;
    private String PicUrl;
    private String MenuId;
    private String Location_X;
    private String Location_Y;
    private String Scale;
    private String ThumbMediaId;
    private String Ticket;

    public boolean isEvent() {
        return StringUtils.equals(getMsgType(), "event");
    }

    public boolean isSubscribeEvent() {
        return StringUtils.equals(getMsgType(), "event")
                && StringUtils.equals(getEvent(), "subscribe");
    }

    public boolean isJoinCommunityEvent() {
        return StringUtils.equals(getMsgType(), "event")
                && StringUtils.equals(getEventKey(), "JOIN_COMMUNITY");
    }

    public boolean isHelpEvent() {
        return StringUtils.equals(getMsgType(), "event")
                && StringUtils.equals(getEventKey(), "HELP");
    }

    public boolean isExampleEvent() {
        return StringUtils.equals(getMsgType(), "event")
                && StringUtils.equals(getEventKey(), "EXAMPLE");
    }
}
