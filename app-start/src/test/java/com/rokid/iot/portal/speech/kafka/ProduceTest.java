package com.rokid.iot.portal.speech.kafka;


import com.rokid.iot.Application;
import com.rokid.iot.msg.port.listener.KafkaListenerConcern;
import com.rokid.iot.portal.share.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ProduceTest implements KafkaListenerConcern {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    public void sendControlMsg() {

        KafkaMessage kafkaMessage = new KafkaMessage();
        kafkaMessage.setDeviceId("wxoVcpRwwWBjlcCnbQuk40BUFCSk_U");
        kafkaMessage.setDeviceTypeId("2148986BE3AB4126A4C448C9A8010DC3");
        kafkaMessage.setUserId("userIdForTest");
        kafkaMessage.setTitle("设备控制失败");
        kafkaMessage.setContent("您有十个设备控制失败");
        kafkaMessage.setActionUrlV2("https://www.json.cn");

        this.kafkaTemplate.send("IOT_CONTROL_RESP", JsonHelper.toJson(kafkaMessage));
    }

    @Test
    public void sendRespVoice() {

        KafkaRespVoice kafkaRespVoice = new KafkaRespVoice();
        kafkaRespVoice.setDeviceId("wxoVcpRwwWBjlcCnbQuk40BUFCSk_U");
        kafkaRespVoice.setDeviceTypeId("2148986BE3AB4126A4C448C9A8010DC3");
        kafkaRespVoice.setUserId("userIdForTest");
        kafkaRespVoice.setTts("好的，已控制成功");

        this.kafkaTemplate.send("iot_control_tts", JsonHelper.toJson(kafkaRespVoice));
    }

    @Test
    public void testSendControlMsg() {

        this.kafkaTemplate.send(
                "iot_nlp_control_error_message",
                "{\"type\":\"multi-error\",\"deviceId\":\"wxoVcpRwwWBjlcCnbQuk40BUFCSk_U\",\"deviceTypeId\":\"2148986BE3AB4126A4C448C9A8010DC3\",\"userId\":\"0EEAF8B1C2C44F0819CC5F7678B69E00\",\"activityId\":\"4WDLZvu_T\",\"title\":\"点击查看问题详情\",\"actionUrlV1\":\"https://s.rokidcdn.com/homebase/rokid/dev/index.html#/control-logs/4WDLZvu_T\",\"actionUrlV2\":\"https://iot-mobile-test.rokid.com/control-logs/4WDLZvu_T\",\"actionUrlV1Webview\":\"rokid://webview/index?url=https%3A%2F%2Fs.rokidcdn.com%2Fhomebase%2Frokid%2Fdev%2Findex.html%23%2Fcontrol-logs%2F4WDLZvu_T\",\"actionUrlV2Webview\":\"rokid://webview/index?url=https%3A%2F%2Fiot-mobile-test.rokid.com%2Fcontrol-logs%2F4WDLZvu_T\",\"title\":\"控制失败\",\"content\":\"2 个设备控制失败\"}"
        );
    }

}
