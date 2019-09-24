package com.rokid.iot.portal.speech.card;

import com.rokid.iot.Application;
import com.rokid.iot.msg.domain.MsgConcern;
import com.rokid.iot.msg.domain.WeChatCard;
import com.rokid.iot.msg.port.card.WechatCardAdaptor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class WechatWeChatCardTest implements MsgConcern {

    @Resource
    private WechatCardAdaptor wechatCardAdaptor;

    @Test
    public void testQueryTemplates() {
        System.out.println(wechatCardAdaptor.ofTemplates());
    }

    @Test
    public void testSendCard() {

        WeChatCard wechatCard = WeChatCard.builder()
                .to(User.builder().openId("oVcpRwwWBjlcCnbQuk40BUFCSk_U").build())
                .title("控制失败")
                .subTitle("7月4日")
                .build();

        wechatCard.setCardType(CardType.CONTROL_ERROR);
        wechatCard.setForward(Forward.builder().build());
        wechatCard.putDetail("first", "非常抱歉！\"" + "打开灯" + "\" 出错了");
        wechatCard.putDetail("keyword1", "您有十个设备控制出错了");
        wechatCard.putDetail("keyword2", "2019-07-04 17:25");
        wechatCard.putDetail("remark", "请稍后重试，谢谢您的配合！");


        wechatCardAdaptor.send(wechatCard);
    }
}
