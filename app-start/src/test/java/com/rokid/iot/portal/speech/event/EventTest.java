package com.rokid.iot.portal.speech.event;

import com.rokid.iot.portal.share.JsonHelper;
import com.rokid.iot.portal.speak.application.SpeakResp;
import org.junit.Test;

public class EventTest {

    @Test
    public void testInitiateResp() {

        System.out.println(
                JsonHelper.toJson(
                        SpeakResp.ofInitiated(
                                "Chris",
                                "https://iot-mobile-test.rokid.com/homes").getReply())
        );
    }
}
