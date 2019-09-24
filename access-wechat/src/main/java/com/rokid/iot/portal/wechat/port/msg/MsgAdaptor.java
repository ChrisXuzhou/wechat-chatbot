package com.rokid.iot.portal.wechat.port.msg;

import com.rokid.iot.msg.application.WechatMsgApplicationService;
import com.rokid.iot.msg.domain.KFMessage;
import com.rokid.iot.msg.domain.MsgConcern;
import com.rokid.iot.msg.domain.WeChatCard;
import com.rokid.iot.portal.share.DateHelper;
import com.rokid.iot.portal.wechat.domain.WeChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.rokid.iot.msg.domain.MsgConcern.CardType.PORTAL_ERROR;

@Slf4j
@Service
public class MsgAdaptor {

    @Resource
    private WechatMsgApplicationService wechatMsgApplicationService;

    public void speak(WeChatMessage msg) {

        if (!msg.isSuccess()) {
            WeChatCard wechatCard = WeChatCard.builder()
                    .cardType(PORTAL_ERROR)
                    .to(MsgConcern.User.builder()
                            .openId(msg.getToUserName())
                            .build()
                    )
                    .forward(MsgConcern.Forward.builder().build())
                    .build();

            if (StringUtils.isNotBlank(msg.getVoice())) {
                wechatCard.putDetail("first", "非常抱歉！\"" + msg.getVoice() + "\" 出错了");
            }
            wechatCard.putDetail("keyword1", msg.getContent());
            wechatCard.putDetail("keyword2", DateHelper.current());
            wechatCard.putDetail("remark", "请稍后重试，谢谢您的配合！");
            wechatCard.putDetail("keyword3", "请稍后重试，谢谢您的配合");
            wechatCard.putDetail("remark", "您可以点击详情查看具体解决方案");


            wechatMsgApplicationService.send(wechatCard);
        } else {
            this.wechatMsgApplicationService.send(
                    KFMessage.builder()
                            .from(
                                    KFMessage.User.builder()
                                            .openId(msg.getFromUserName())
                                            .build()
                            )
                            .to(
                                    KFMessage.User.builder()
                                            .openId(msg.getToUserName())
                                            .build()
                            )
                            .content(msg.getContent())
                            .build()
            );
        }
    }
}
