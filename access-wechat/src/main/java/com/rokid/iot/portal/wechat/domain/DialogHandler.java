package com.rokid.iot.portal.wechat.domain;

import com.google.common.collect.Maps;
import com.rokid.iot.portal.share.JsonHelper;
import com.rokid.iot.portal.share.wechat.WechatConcern;
import com.rokid.iot.portal.speak.application.SpeakRequest;
import com.rokid.iot.portal.speak.application.SpeakResp;
import com.rokid.iot.portal.wechat.port.account.AccountAdaptor;
import com.rokid.iot.portal.wechat.port.speech.WeChatSpeechAdaptor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Data
@Builder
public class DialogHandler implements WechatConcern {

    private AccountAdaptor accountAdaptor;
    private WeChatSpeechAdaptor speechAdaptor;

    public static DialogHandler of(
            AccountAdaptor accountAdaptor,
            WeChatSpeechAdaptor speechAdaptor) {

        return DialogHandler.builder()
                .accountAdaptor(accountAdaptor)
                .speechAdaptor(speechAdaptor)
                .build();
    }

    public DialogHandler handle(
            String fromUserOpenId,
            String sentence) {

        /*
         * 1、构建对话体
         */
        setFrom(
                User.builder().openId(fromUserOpenId).build()
        );

        /*
         * 2、转换【语句】
         */
        setSentence(
                preHandle(sentence)
        );
        /*
         * 3、调用语音主链路
         */
        speechAdaptor.speak(this);

        return this;
    }

    private User from;
    private User to;
    private String sentence;

    private SpeakResp reply;

    public SpeakRequest ofSpeakRequest() {

        String masterId = accountAdaptor.of(getFrom()).getMasterId();
        String deviceId = asDeviceId(getFrom().getOpenId());
        return SpeakRequest.builder()
                .sentence(getSentence())
                .masterId(masterId)
                .deviceId(deviceId)
                .deviceTypeId(wechatDeviceTypeId())
                .build();
    }

    @Getter
    @Builder
    public static class User {

        /**
         * 通常 消息传过来的openId 就是fromUserName
         */
        private String openId;
        private String name;
    }

    public String inString() {
        Map<String, Object> inMap = Maps.newHashMap();
        inMap.put("from", from);
        inMap.put("to", to);
        inMap.put("sentence", sentence);


        return JsonHelper.toJson(inMap);
    }


}
