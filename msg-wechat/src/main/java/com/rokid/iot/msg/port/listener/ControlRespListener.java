package com.rokid.iot.msg.port.listener;

import com.rokid.iot.msg.application.WechatMsgApplicationService;
import com.rokid.iot.msg.domain.MsgConcern;
import com.rokid.iot.msg.domain.WeChatCard;
import com.rokid.iot.portal.share.JsonHelper;
import com.rokid.iot.portal.share.Tracer;
import com.rokid.iot.portal.share.trace.TokenHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.rokid.iot.msg.domain.MsgConcern.CardType.CONTROL_ERROR;

@Slf4j
@Service
public class ControlRespListener implements KafkaListenerConcern, MsgConcern {

    @Resource
    private WechatMsgApplicationService wechatMsgApplicationService;

    @KafkaListener(topics = "iot_nlp_control_error_message", groupId = "WECHAT_RESP")
    public void consume(String message) {

        String traceId = TokenHelper.randomToken();
        MDC.put("traceId", traceId);
        String voice;
        Tracer tracer = Tracer.of(log, "ControlRespListener.consume", message);
        try {
            if (StringUtils.isEmpty(message)) {
                return;
            }
            KafkaMessage msg = JsonHelper.fromJson(message, KafkaMessage.class);
            if (!msg.wechatConcern()) {
                return;
            }
            voice = msg.getVoice();
            MDC.put("voice", voice);

            tracer.addParam("msg", msg);
            WeChatCard wechatCard = WeChatCard.builder()
                    .cardType(CONTROL_ERROR)
                    .to(User.builder()
                            .openId(msg.ofOpenId())
                            .build()
                    )
                    .forward(
                            Forward.builder()
                                    .path(msg.ofPath())
                                    .build()
                    )
                    .title(msg.getTitle())
                    .voice(msg.getVoice())
                    .build();
            if (StringUtils.isNotBlank(msg.getVoice())) {
                wechatCard.putDetail("first", "非常抱歉！\"" + msg.getVoice() + "\" 出错了");
            }

            wechatCard.putDetail("keyword1", msg.getContent());
            wechatCard.putDetail("keyword2", msg.happenedAt());
            wechatCard.putDetail("keyword3", "请稍后重试，谢谢您的配合");
            wechatCard.putDetail("remark", "您可以点击详情查看具体解决方案");

            tracer.addParam("msgReq", msg);
            sendMsg(traceId, voice, wechatCard);
        } catch (Exception e) {
            tracer.error(e);
        } finally {
            tracer.print();
        }
    }

    @Async
    public void sendMsg(String traceId, String voice, WeChatCard card) {
        MDC.put("traceId", traceId);
        MDC.put("voice", voice);
        wechatMsgApplicationService.send(card);
    }
}
