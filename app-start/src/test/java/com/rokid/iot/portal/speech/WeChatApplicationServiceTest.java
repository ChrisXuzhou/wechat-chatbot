package com.rokid.iot.portal.speech;

import com.rokid.iot.Application;
import com.rokid.iot.portal.wechat.application.EventApplicationService;
import com.rokid.iot.portal.wechat.application.WeChatApplicationService;
import com.rokid.iot.portal.wechat.application.WechatRequest;
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
public class WeChatApplicationServiceTest extends TestConcern {

    @Resource
    private WeChatApplicationService wechatApplicationService;

    @Test
    public void testSpeak() throws InterruptedException {

        WechatRequest request = WechatRequest.builder()
                .FromUserName("oVcpRwwWBjlcCnbQuk40BUFCSk_UA")
                .ToUserName("gh_37a89089d58e")
                .Content("开灯")
                .MsgType("text")
                .build();

        wechatApplicationService.speak(request);

        Thread.sleep(10000);

    }

    @Resource
    private EventApplicationService eventApplicationService;

    @Test
    public void testInitiatedMsg() {

        WechatRequest request = WechatRequest.builder()
                .FromUserName("oVcpRwwWBjlcCnbQuk40BUFCSk_U")
                .ToUserName("gh_37a89089d58e")
                .Event("subscribe")
                .MsgType("event")
                .build();
        eventApplicationService.handle(request);
    }

    @Test
    public void testHelpEvent() {

        WechatRequest request = WechatRequest.builder()
                .FromUserName("oVcpRwwWBjlcCnbQuk40BUFCSk_U")
                .ToUserName("gh_37a89089d58e")
                .EventKey("HELP")
                .MsgType("event")
                .build();
        eventApplicationService.handle(request);
    }

    @Test
    public void testCommunityEvent() {

        WechatRequest request = WechatRequest.builder()
                .FromUserName("oVcpRwwWBjlcCnbQuk40BUFCSk_U")
                .ToUserName("gh_37a89089d58e")
                .EventKey("JOIN_COMMUNITY")
                .MsgType("event")
                .build();
        eventApplicationService.handle(request);
    }

}
