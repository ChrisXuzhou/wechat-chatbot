package com.rokid.iot.portal.speech;

import com.rokid.iot.Application;
import com.rokid.iot.portal.share.JsonHelper;
import com.rokid.iot.portal.speak.domain.Speech;
import com.rokid.iot.portal.speak.port.speech.SpeechAdaptor;
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
public class SpeechAdaptorTest extends TestConcern {

    @Resource
    private SpeechAdaptor speechAdaptor;

    @Test
    public void testSpeak() {

        MockDevice mocked = mock();

        Speech speech = Speech.builder()
                .speaker(
                        Speech.Speaker.builder()
                                .deviceId("wxoVcpRw7Z35v71w4yDXKM5DmjZpXU")
                                .deviceTypeId("2148986BE3AB4126A4C448C9A8010DC3")
                                .masterId(mocked.getMasterId())
                                .build())
                .sentence("开灯")
                .build();

        speechAdaptor.speak(speech);
        System.out.println(JsonHelper.toJson(speech));
    }

}
