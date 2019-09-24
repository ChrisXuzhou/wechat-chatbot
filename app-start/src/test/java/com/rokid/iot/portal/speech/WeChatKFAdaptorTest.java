package com.rokid.iot.portal.speech;

import com.rokid.iot.Application;
import com.rokid.iot.msg.domain.KFMessage;
import com.rokid.iot.msg.port.kf.WechatKFAdaptor;
import com.rokid.iot.portal.share.Tracer;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class WeChatKFAdaptorTest {

    @Resource
    private WechatKFAdaptor wechatKFAdaptor;

    @Test
    public void testSendKFMsg() {

        Tracer tracer = Tracer.of(log, "test.send.kf.msg", null);

        String content = "\uD83D\uDE02\uD83D\uDC7F";
        tracer.addParam("content", content);

        String newContent = EmojiParser.parseToUnicode(content);
        tracer.addParam("newContent", newContent);

        String escaped = StringEscapeUtils.unescapeJava(content);
        tracer.addParam("escaped", escaped);

        String decode = decode1(content);
        tracer.addParam("decode", decode);

        KFMessage kfMessage =
                KFMessage.builder()
                        .content(decode)
                        .from(
                                KFMessage.User.builder()
                                        .openId("gh_37a89089d58e")
                                        .build())
                        .to(
                                KFMessage.User.builder()
                                        .openId("oVcpRwwWBjlcCnbQuk40BUFCSk_U")
                                        .build()
                        )
                        .build();

        tracer.addParam("kfMessage", kfMessage);
        wechatKFAdaptor.sendKFMsg(kfMessage);

        tracer.print();
    }


    static final Pattern reUnicode = Pattern.compile("\\\\u([0-9a-zA-Z]{4})");

    public static String decode1(String s) {
        Matcher m = reUnicode.matcher(s);
        StringBuffer sb = new StringBuffer(s.length());
        while (m.find()) {
            m.appendReplacement(sb,
                    Character.toString((char) Integer.parseInt(m.group(1), 16)));
        }
        m.appendTail(sb);
        return sb.toString();
    }


    @Test
    public void testSendKFImage() {

        Tracer tracer = Tracer.of(log, "test.send.kf.msg.image", null);
        KFMessage kfMessage =
                KFMessage.builder()
                        .msgType("image")
                        .mediaId("am0Ol5uakdLJbJuqz3Pe3TXNPfi5uXB6TJjnok0nv_E")
                        .from(
                                KFMessage.User.builder()
                                        .openId("gh_37a89089d58e")
                                        .build())
                        .to(
                                KFMessage.User.builder()
                                        .openId("oVcpRwwWBjlcCnbQuk40BUFCSk_U")
                                        .build()
                        )
                        .build();

        tracer.addParam("kfMessage", kfMessage);
        wechatKFAdaptor.sendKFMsg(kfMessage);

        tracer.print();
    }

}
