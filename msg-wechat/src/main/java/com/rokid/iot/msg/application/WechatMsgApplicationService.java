package com.rokid.iot.msg.application;

import com.rokid.iot.msg.domain.KFMessage;
import com.rokid.iot.msg.domain.WeChatCard;
import com.rokid.iot.msg.port.card.WechatCardAdaptor;
import com.rokid.iot.msg.port.kf.WechatKFAdaptor;
import com.rokid.iot.portal.share.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class WechatMsgApplicationService {

    @Resource
    private WechatKFAdaptor wechatKFAdaptor;

    public void send(KFMessage kfMessage) {
        Tracer tracer = Tracer.of(log, "WechatMsgApplicationService.send[msg]", kfMessage);
        try {
            wechatKFAdaptor.sendKFMsg(kfMessage.legal());
        } catch (Exception e) {
            tracer.error(e);
        } finally {
            tracer.print();
        }
    }

    @Resource
    private WechatCardAdaptor wechatCardAdaptor;

    public void send(WeChatCard wechatCard) {

        Tracer tracer = Tracer.of(log, "WechatMsgApplicationService.send[card]", wechatCard);
        try {
            wechatCardAdaptor.send(wechatCard.legal());
        } catch (Exception e) {
            tracer.error(e);
        } finally {
            tracer.print();
        }
    }

}
