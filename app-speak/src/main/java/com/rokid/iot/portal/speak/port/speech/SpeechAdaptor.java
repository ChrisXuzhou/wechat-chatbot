package com.rokid.iot.portal.speak.port.speech;

import com.google.common.base.Preconditions;
import com.rokid.iot.portal.speak.domain.Speech;
import com.rokid.iot.portal.share.Tracer;
import com.rokid.open.nlp.facade.NLPServiceGrpc;
import com.rokid.open.nlp.facade.Nlp;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class SpeechAdaptor {

    @Value("${rokid.speech.server.grpc.host}")
    private String host;
    @Value("${rokid.speech.server.grpc.port}")
    private String port;

    /**
     * 1、正常：拿到【语音链路】结果
     * 3、错误：抛出【错误】
     *
     * @param speech 语音对话
     */
    public void speak(Speech speech) {

        Tracer tracer = Tracer.of(log, "SpeechAdaptor.send", speech);
        speech.legal();
        try {
            Nlp.serviceCallResponse resp = doSpeech(speech);
            Preconditions.checkArgument(Objects.nonNull(resp), "speech resp is null");

            tracer.result(resp);
            Speech.Resp speechResp = NlpRespHandler.of(
                    resp.getService(),
                    resp.getNlp())
                    .build();

            speech.setResp(speechResp);
        } catch (Exception e) {
            tracer.error(e);
            throw e;
        } finally {
            tracer.print();
        }
    }

    private Nlp.serviceCallResponse doSpeech(Speech speech) {
        ManagedChannel nlpChannel = null;
        try {
            nlpChannel = ManagedChannelBuilder
                    .forAddress(host, Integer.valueOf(port))
                    .usePlaintext()
                    .build();

            return NLPServiceGrpc.newBlockingStub(nlpChannel)
                    .serviceCall(
                            Nlp.serviceCallRequest
                                    .newBuilder()
                                    .setAccountId(speech.getSpeaker().getMasterId())
                                    .setDeviceId(speech.getSpeaker().getDeviceId())
                                    .setDeviceTypeId(speech.getSpeaker().getDeviceTypeId())
                                    .setSentence(speech.getSentence())
                                    .setStack("R4851AB35A0E4D338F455D470AEC95D3")
                                    .build()
                    );

        } finally {
            if (nlpChannel != null
                    && (!nlpChannel.isTerminated() || !nlpChannel.isShutdown())) {
                nlpChannel.shutdownNow();
            }
        }
    }


}
