package com.rokid.iot.portal.speech;

import com.rokid.iot.portal.wechat.inf.wxtools.AesException;
import com.rokid.iot.portal.wechat.port.wechat.WechatAuthConcern;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class AuthTest implements WechatAuthConcern {


    String content = "<xml><URL><![CDATA[https://chris-kten.localhost.run/entry]]></URL><ToUserName><![CDATA[gh_37a89089d58e]]></ToUserName><FromUserName><![CDATA[oVcpRwwWBjlcCnbQuk40BUFCSk_U]]></FromUserName><CreateTime>1567677156</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[开灯]]></Content><MsgId>6733122115706290176</MsgId></xml>";

    String decrypt = "<xml>    <ToUserName><![CDATA[gh_37a89089d58e]]></ToUserName>    <Encrypt><![CDATA[oncb5Z2J+zhiTjMkW7vwoaT+kj4H8Q0xaITTqyb0bFoWeyHUJVPDrPBqKCTceBaZhVj9ucyK6rInSxl7C2VkYVEkPQainehI1nDjCDt0FpkH0JefR9JVFPjyx5ZqBhZvCU315i+Dw2LuB/uuHFY84hQckJ+ahMALM2gAJotwId6lbAoQXvLl16fqE20o1djB8TWRY5OqHGiPU4hfWX8k8cu3gqVcmYAbCv5DRnnKcwBaLk8MCzwYvzgqc03dJkkT8bxtQ701PI9xq56zJ5pE+CCmUXEU6Dy0Z1zSQkEZbaYLS0mkxMdPS9X2xISopXmEU88S5OShYvI2CB/PMPIjrEwm5eXf+PHUgJVvQEuL89H0ip1nkyIOVjIOdtZHzV6FoJ6viifoJbwF4ouWibtIBLBalWl6hLNgkO7qYcAQJuUm7Yw6PC+a3TLCN0vdgGvtX1Jsee8+FpWgx4c8XNtPmfMCIFpjCO7IApRJuF96d1QT6gJq5tQiB8l0Jf3P+i2F]]></Encrypt></xml>";

    String xmlFormat = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";

    @Test
    public void testAuth() throws AesException, ParserConfigurationException, IOException, SAXException {

        SecretPackage secretPackage = new SecretPackage();
        secretPackage.setAppId("wx85851e71de3ae99e");
        secretPackage.setEncodingAesKey("eazqoHg2RjMQBb2TA7u6BZ9vWwefNDUEGFWe93VGrrM");
        secretPackage.setToken("hello");

        Auth auth = Auth.builder()
                .secretPackage(secretPackage)
                .nonce("1676012851")
                .timestamp("1567683451")
                .signature("6341c110586c115a6f92a14c319393dfa88b1a82")
                .build();

        String afterEncrpt = auth.encrypt(content);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(afterEncrpt);
        InputSource is = new InputSource(sr);
        Document document = db.parse(is);

        Element root = document.getDocumentElement();
        NodeList nodelist1 = root.getElementsByTagName("Encrypt");
        NodeList nodelist2 = root.getElementsByTagName("MsgSignature");

        String encrypt = nodelist1.item(0).getTextContent();
        String msgSignature = nodelist2.item(0).getTextContent();
        String fromXML = String.format(xmlFormat, encrypt);

        String afterDecrpt = auth.decrypt(msgSignature, fromXML);

        System.out.println(
                afterEncrpt + '\n' +
                        msgSignature + '\n' +
                        afterDecrpt
        );

    }

    @Test
    public void testTime() {
        System.out.println(System.currentTimeMillis());
    }
}
