package com.rokid.iot.msg.port.listener;

import com.rokid.iot.msg.application.WechatMsgApplicationService;
import com.rokid.iot.msg.domain.KFMessage;
import com.rokid.iot.msg.domain.MsgConcern;
import com.rokid.iot.portal.share.JsonHelper;
import com.rokid.iot.portal.share.Tracer;
import com.rokid.iot.portal.share.trace.TokenHelper;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Slf4j
@Service
public class RespVoiceListener implements KafkaListenerConcern, MsgConcern {

    @Resource
    private WechatMsgApplicationService wechatMsgApplicationService;

    @KafkaListener(topics = "iot_control_tts", groupId = "WECHAT_VOICE")
    public void consume(String message) {
        String traceId = TokenHelper.randomToken();
        MDC.put("traceId", traceId);
        Tracer tracer = Tracer.of(log, "RespVoiceListener.consume", message);
        try {
            if (StringUtils.isEmpty(message)) {
                return;
            }
            KafkaRespVoice msg = JsonHelper.fromJson(message, KafkaRespVoice.class);
            if (!msg.wechatConcern()) {
                return;
            }
            tracer.addParam("msg", msg);

            String voice = msg.getUserText();
            MDC.put("voice", voice);
            KFMessage kfMessage = KFMessage.builder()
                    .to(User.builder()
                            .openId(msg.ofOpenId())
                            .build()
                    )
                    .content(msg.getTts())
                    .voice(msg.getUserText())
                    .build();
            sendMsg(traceId, voice, kfMessage);
        } catch (Exception e) {
            tracer.error(e);
        } finally {
            tracer.print();
        }
    }


    @Async
    public void sendMsg(String traceId, String voice, KFMessage kfMessage) {
        MDC.put("traceId", traceId);
        MDC.put("voice", voice);
        wechatMsgApplicationService.send(kfMessage);
    }
}
