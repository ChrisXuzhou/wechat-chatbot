package com.rokid.iot.portal.wechat.port.speech;

import com.rokid.iot.portal.speak.application.SpeakResp;
import com.rokid.iot.portal.speak.application.SpeechApplicationService;
import com.rokid.iot.portal.wechat.domain.DialogHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class WeChatSpeechAdaptor {

    @Resource
    private SpeechApplicationService speechApplicationService;

    public void speak(DialogHandler describe) {
        SpeakResp resp = speechApplicationService.speak(describe.ofSpeakRequest());
        describe.setReply(resp);
    }

}
