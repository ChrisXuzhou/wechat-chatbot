package com.rokid.iot.portal.speak.port.speech;

import com.google.common.base.Preconditions;
import com.rokid.iot.portal.speak.domain.SmartHomeConcern;
import com.rokid.iot.portal.speak.domain.Speech;
import com.rokid.iot.portal.share.JsonHelper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class NlpRespHandler implements SmartHomeConcern {

    private String nlp;
    private String service;

    private static final String BREAK_TIME = "<speak><break time=";

    public static NlpRespHandler of(String service,
                                    String nlp) {
        Preconditions.checkArgument(StringUtils.isNotBlank(service), "服务器无响应");
        Preconditions.checkArgument(StringUtils.isNotBlank(nlp), "nlp[String] is empty!");

        NlpRespHandler handler = new NlpRespHandler();
        handler.setService(service);
        handler.setNlp(nlp);

        return handler;
    }

    private boolean isFromIot() {
        Map nlpMap = JsonHelper.toMap(getNlp());
        String appId = (String) nlpMap.get("appId");
        return isFromSmartHome(appId);
    }

    private String respVoice() {
        Map resp = JsonHelper.toMap(service);
        Preconditions.checkArgument(Objects.nonNull(resp));
        String version = (String) resp.get("version");

        String respVoice;
        if (StringUtils.equals(version, "2.0.0")) {
            respVoice = respVoice2_0(resp);
        } else if (StringUtils.equals(version, "3.0.0")) {
            respVoice = respVoice3_0(resp);
        } else {
            throw new UnsupportedOperationException("Version not supported" + version);
        }

        /*
         * 1、空请求
         */
        if (StringUtils.isBlank(respVoice)) {
            respVoice = "我还不够聪明，再换个说法吧";
        }
        /*
         * 2、异步的请求
         */
        if (respVoice.startsWith(BREAK_TIME)) {
            respVoice = "";
        }
        /*
         * 3、去除Tag信息
         */
        respVoice = replyProcess(respVoice);

        return respVoice;
    }

    private String respVoice3_0(Map resp) {
        Map action = (Map) resp.get("action");
        Preconditions.checkArgument(Objects.nonNull(action));

        Map skill = (Map) action.get("skill");
        Preconditions.checkArgument(Objects.nonNull(skill));

        Map homeBase = (Map) skill.get("R4851AB35A0E4D338F455D470AEC95D3");
        Preconditions.checkArgument(Objects.nonNull(homeBase));

        Preconditions.checkArgument(!CollectionUtils.isEmpty(action));
        List<Map> directiveList = (List<Map>) homeBase.get("directives");

        return fetchRespVoiceFromDirectives(directiveList);
    }

    private String respVoice2_0(Map resp) {
        Map response = (Map) resp.get("response");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(response));
        Map action = (Map) response.get("action");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(action));
        List<Map> directiveList = (List<Map>) action.get("directives");

        return fetchRespVoiceFromDirectives(directiveList);
    }

    private String fetchRespVoiceFromDirectives(List<Map> directiveList) {
        String respVoice = "";
        if (!CollectionUtils.isEmpty(directiveList)) {
            for (Map each : directiveList) {
                if (Objects.isNull(each)) {
                    continue;
                }
                /*
                 * 1、解析voice 类型数据
                 */
                if (StringUtils.equals("voice", (String) each.get("type"))) {
                    final Map item = (Map) each.get("item");
                    Preconditions.checkArgument(Objects.nonNull(item));
                    respVoice = (String) item.get("tts");
                }
            }
        }
        return respVoice;
    }

    private String replyProcess(String reply) {
        if (reply.contains("<")) {
            String pattern = reply.substring(reply.indexOf("<"), reply.indexOf(">") + 1);
            reply = reply.replace(pattern, "");
            reply = replyProcess(reply);
        }
        return reply;
    }

    public Speech.Resp build() {
        return Speech.Resp.builder()
                .voice(respVoice())
                .fromIot(isFromIot())
                .build();
    }

}
