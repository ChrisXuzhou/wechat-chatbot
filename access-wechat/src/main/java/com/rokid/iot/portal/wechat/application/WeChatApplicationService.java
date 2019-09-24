package com.rokid.iot.portal.wechat.application;

import com.google.common.base.Preconditions;
import com.rokid.iot.portal.share.Tracer;
import com.rokid.iot.portal.speak.application.SpeakResp;
import com.rokid.iot.portal.wechat.domain.DialogHandler;
import com.rokid.iot.portal.wechat.domain.WeChatMessage;
import com.rokid.iot.portal.wechat.port.account.AccountAdaptor;
import com.rokid.iot.portal.wechat.port.account.AccountNotFoundException;
import com.rokid.iot.portal.wechat.port.msg.MsgAdaptor;
import com.rokid.iot.portal.wechat.port.speech.WeChatSpeechAdaptor;
import com.rokid.iot.portal.wechat.port.user.UserAdaptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Service
public class WeChatApplicationService {

    @Resource
    private AccountAdaptor accountAdaptor;
    @Resource
    private WeChatSpeechAdaptor wechatSpeechAdaptor;
    @Resource
    private MsgAdaptor msgAdaptor;

    /**
     * 【异步方式】发起语音控制的过程：
     * <p>
     * 1、微信推送【消息】，异步请求【语音链路】，马上返回微信【成功】
     * 2、请求【语音链路】成功，通过【客服消息】返回文本消息/卡片
     * 3、请求【语音链路】报错，通过【客服消息】发送卡片/小程序结果
     * 4、请求【语音链路】超时，通过【客服消息】返回文本消息提示【服务器开小差了】
     * <p>
     */
    @Async
    public void speak(WechatRequest request) {

        Tracer tracer = Tracer.of(log, "[ASYNC] WeChatApplicationService.speak", request);

        try {
            WeChatMessage msg = doSpeak(request);
            tracer.addParam("we-chat msg", msg);
            if (Objects.nonNull(msg)) {
                msgAdaptor.speak(msg);
            }
        } catch (Exception e) {
            tracer.error(e);
        } finally {
            tracer.print();
        }
    }

    private WeChatMessage doSpeak(WechatRequest request) {

        Tracer tracer = Tracer.of(log, "WechatApplicationService.doSpeak", request);
        try {

            DialogHandler handler = DialogHandler.of(
                    accountAdaptor,
                    wechatSpeechAdaptor)
                    .handle(
                            request.legal().getFromUserName(),
                            request.getText()
                    );

            SpeakResp resp = handler.getReply();
            Preconditions.checkState(Objects.nonNull(resp));
            if (resp.isSuccess()) {
                return WeChatMessage.ofSpeakReq(
                        request.getText(),
                        request.getToUserName(),
                        request.getFromUserName(),
                        handler.getReply());
            }
            if (resp.noNeedToReply()) {
                return null;
            }
            return WeChatMessage.ofError(
                    request.getText(),
                    request.getToUserName(),
                    request.getFromUserName(),
                    resp.getReply());

        } catch (AccountNotFoundException e) {
            notify2Login(request);
            return null;
        } catch (Exception e) {
            tracer.error(e).print();
            return WeChatMessage.ofError(
                    request.getText(),
                    request.getToUserName(),
                    request.getFromUserName(),
                    e);
        }
    }

    @Resource
    private UserAdaptor userAdaptor;
    @Value("${rokid.iot.mobile.url}")
    private String url;
    @Value("${rokid.iot.mobile.oauth.path}")
    private String oauthPath;

    public void notify2Login(WechatRequest request) {
        UserAdaptor.UserDescriptor descriptor = userAdaptor.of(request.getFromUserName());
        String nick = descriptor.getNickName();
        WeChatMessage msg = WeChatMessage.ofSpeakReq(request.getText(),
                request.getToUserName(),
                request.getFromUserName(),
                SpeakResp.builder()
                        .reply(
                                SpeakResp.ofInitiated(nick, url + oauthPath).getReply()
                        )
                        .build()
        );
        msgAdaptor.speak(msg);
    }

}
