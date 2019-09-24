package com.rokid.iot.portal.wechat.application;

import com.rokid.iot.msg.application.WechatMsgApplicationService;
import com.rokid.iot.msg.domain.KFMessage;
import com.rokid.iot.portal.share.Tracer;
import com.rokid.iot.portal.speak.application.SpeakResp;
import com.rokid.iot.portal.wechat.domain.MediaDescriptor;
import com.rokid.iot.portal.wechat.domain.WeChatMessage;
import com.rokid.iot.portal.wechat.port.msg.MsgAdaptor;
import com.rokid.iot.portal.wechat.port.pic.PicAdaptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Service
public class EventApplicationService {

    @Value("${rokid.iot.mobile.url}")
    private String url;
    @Value("${rokid.iot.mobile.oauth.path}")
    private String oauthPath;

    @Resource
    private MsgAdaptor msgAdaptor;

    public void handle(WechatRequest r) {

        Tracer tracer = Tracer.of(log, "Event.handle", r);

        try {
            if (r.isSubscribeEvent()) {
                weChatApplicationService.notify2Login(r);
            }
            if (r.isJoinCommunityEvent()) {
                community(r);
            }
            if (r.isExampleEvent()) {
                example(r);
            }
            if (r.isHelpEvent()) {
                help(r);
            }
        } catch (Exception e) {
            tracer.error(e);
            throw e;
        } finally {
            tracer.print();
        }
    }

    @Resource
    private WeChatApplicationService weChatApplicationService;


    private void help(WechatRequest request) {
        MediaDescriptor mediaDescriptor = picAdaptor.ofHelpPic();
        if (Objects.isNull(mediaDescriptor)) {
            throw new IllegalStateException("HELP_PIC_NOT_FOUND");
        }
        sendPic(mediaDescriptor, request);
    }

    private void example(WechatRequest request) {
        WeChatMessage msg = WeChatMessage.ofSpeakReq(request.getText(),
                request.getToUserName(),
                request.getFromUserName(),
                SpeakResp.builder()
                        .reply(
                                SpeakResp.ofExample().getReply()
                        )
                        .build()
        );
        msgAdaptor.speak(msg);
    }

    @Resource
    private WechatMsgApplicationService wechatMsgApplicationService;

    @Resource
    private PicAdaptor picAdaptor;

    private void community(WechatRequest request) {

        MediaDescriptor mediaDescriptor = picAdaptor.ofCommunityPic();
        if (Objects.isNull(mediaDescriptor)) {
            throw new IllegalStateException("COMMUNITY_PIC_NOT_FOUND");
        }
        sendPic(mediaDescriptor, request);
    }

    private void sendPic(MediaDescriptor mediaDescriptor, WechatRequest request) {
        KFMessage kfMessage =
                KFMessage.builder()
                        .msgType("image")
                        .mediaId(mediaDescriptor.getMedia_id())
                        .from(
                                KFMessage.User.builder()
                                        .openId(request.getToUserName())
                                        .build())
                        .to(
                                KFMessage.User.builder()
                                        .openId(request.getFromUserName())
                                        .build()
                        )
                        .build();
        wechatMsgApplicationService.send(kfMessage);
    }
}
