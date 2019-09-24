package com.rokid.iot.portal.speech.emoji;

import com.rokid.iot.msg.port.MsgHttpConcern;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EmojiTest implements MsgHttpConcern {


    @Test
    public void testUrlEncode() throws UnsupportedEncodingException {

        String str = "Hi~~Chris😂 👿 我叫小万(小若琪的弟弟), 一个万能遥控机器人。 我可以帮您控制管理家里的一切设备，希望能成为您的好伙伴！ 您可以点击链接<a href=\"https://iot-mobile-test.rokid.com/wechat/auth\">注册/登录Rokid帐户</a>, 开启您的魔法之旅";

        System.out.println(
                URLEncoder.encode(str, "UTF-8")
        );
    }

    @Test
    public void test2Bytes() {
        System.out.println(
                "😂👿".getBytes()
        );

        System.out.println(
                "😂👿".getBytes()
        );
    }


}
