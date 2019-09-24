package com.rokid.iot.portal.speak.application;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.rokid.iot.portal.speak.domain.Speech;
import com.rokid.iot.portal.speak.port.speech.SpeechAdaptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class SpeechApplicationService {

    @Resource
    private SpeechAdaptor speechAdaptor;

    /**
     * 【异常情况】处理过程
     * <p>
     * 1、【超时机制】当【语音链路】【响应超时】时，Hystrix返回【报错文本】
     * 2、【报错机制】当【语音链路】【报错】时，抛出错误，到服务的最顶层，方便【定位错误】
     *
     * @param request 语音控制请求
     * @return SpeakResp 语音反馈
     */
    /*
    @HystrixCommand(
            fallbackMethod = "fallbackSpeak",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000")
            })

     */
    public SpeakResp speak(SpeakRequest request) {

        request.legal();
        Speech speech = Speech.builder()
                .speaker(
                        Speech.Speaker.builder()
                                .deviceId(request.getDeviceId())
                                .deviceTypeId(request.getDeviceTypeId())
                                .masterId(request.getMasterId())
                                .build())
                .sentence(request.getSentence())
                .build();

        speechAdaptor.speak(speech);

        String voice = speech.getResp().getVoice();

        SpeakResp.ErrNo errNo = SpeakResp.ErrNo.SUCCESS;
        if (StringUtils.isBlank(voice)) {
            errNo = SpeakResp.ErrNo.NO_RESP;
        }
        return SpeakResp.builder()
                .errNo(errNo)
                .reply(voice)
                .build();
    }

    public SpeakResp fallbackSpeak(SpeakRequest request) {
        return SpeakResp.ofTimeOut();
    }


}
