package com.rokid.iot.portal.speech;

import com.rokid.iot.portal.share.JsonHelper;
import com.rokid.iot.portal.speak.port.speech.NlpRespHandler;
import org.junit.Test;

public class NlpRespHandlerTest {

    @Test
    public void testNlp() {
        System.out.println(
                JsonHelper.toJson(
                        NlpRespHandler.of(
                                "{\"version\":\"2.0.0\",\"appId\":\"R4851AB35A0E4D338F455D470AEC95D3\",\"appProtocolVersion\":\"2.0.0\",\"from\":\"INTENT\",\"response\":{\"respId\":\"37ed00cabdc3a2ee3e4d3b753ad7556\",\"action\":{\"version\":\"2.0.0\",\"shouldEndSession\":false,\"type\":\"NORMAL\",\"form\":\"cut\",\"directives\":[{\"type\":\"voice\",\"disableEvent\":false,\"action\":\"PLAY\",\"item\":{\"itemId\":\"E14B2FFCBFFF48D6B032C5114342BA54\",\"tts\":\"<speak><break time=\\\"0.1s\\\"/></speak>\"}}]}}}",
                                "{\"intent\":\"search\",\"cloud\":true,\"pattern\":\"$you?($could)?($help)?($me)?$search$devices\",\"slots\":{\"search\":{\"pinyin\":\"\",\"type\":\"search\",\"value\":\"搜索\"},\"devices\":{\"pinyin\":\"\",\"type\":\"devices\",\"value\":\"设备\"}},\"asr\":\"搜索设备。\",\"appId\":\"R4851AB35A0E4D338F455D470AEC95D3\",\"appName\":\"Rokid智能家居云端ksyun\"}"
                        ).build()
                )
        );

    }
}
