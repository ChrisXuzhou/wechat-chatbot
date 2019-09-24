package com.rokid.iot.portal.wechat.domain;

import com.google.common.base.Preconditions;
import com.rokid.iot.portal.share.Tracer;
import com.rokid.iot.portal.speak.application.SpeakResp;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

/**
 * 文本消息实体，xml如下：
 * <xml>
 * <ToUserName>< ![CDATA[toUser] ]></ToUserName>
 * <FromUserName>< ![CDATA[fromUser] ]></FromUserName>
 * <CreateTime>12345678</CreateTime>
 * <MsgType>< ![CDATA[text] ]></MsgType>
 * <Content>< ![CDATA[你好] ]></Content>
 * </xml>
 */
@Data
@Builder
public class WeChatMessage {

    private boolean success;
    private String voice;

    private String ToUserName;
    private String FromUserName;
    private String Content;
    private String MsgType;

    private final long CreateTime = System.currentTimeMillis();


    public static WeChatMessage ofSpeakReq(String voice,
                                           String toUserOpenId,
                                           String fromUserOpenId,
                                           SpeakResp speakResp) {
        Preconditions.checkArgument(Objects.nonNull(speakResp));

        String content = speakResp.getReply();

        return WeChatMessage.builder()
                .success(true)
                .voice(voice)
                .ToUserName(wrap(fromUserOpenId))
                .FromUserName(wrap(toUserOpenId))
                .MsgType(wrap("text"))
                .Content(wrap(content))
                .build();
    }

    public static WeChatMessage ofError(String voice,
                                             String toUserOpenId,
                                             String fromUserOpenId,
                                             Exception e) {

        return WeChatMessage.builder()
                .success(false)
                .voice(voice)
                .ToUserName(wrap(fromUserOpenId))
                .FromUserName(wrap(toUserOpenId))
                .MsgType(wrap("text"))
                .Content(Tracer.errorResp(e))
                .build();
    }

    public static WeChatMessage ofError(String voice,
                                        String toUserOpenId,
                                        String fromUserOpenId,
                                        String errMsg) {

        return WeChatMessage.builder()
                .success(false)
                .voice(voice)
                .ToUserName(wrap(fromUserOpenId))
                .FromUserName(wrap(toUserOpenId))
                .MsgType(wrap("text"))
                .Content(errMsg)
                .build();
    }

    private static String wrap(String origin) {
        return origin;
    }


}
