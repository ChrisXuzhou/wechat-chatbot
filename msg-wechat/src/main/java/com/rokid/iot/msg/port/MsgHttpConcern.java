package com.rokid.iot.msg.port;

import com.google.common.base.Preconditions;
import com.google.gson.reflect.TypeToken;
import com.rokid.iot.msg.domain.KFMessage;
import com.rokid.iot.portal.share.JsonHelper;
import com.vdurmont.emoji.EmojiParser;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public interface MsgHttpConcern {

    default Map<String, Object> post(String url, Map<String, String> uriParams, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String uri = buildUrl(url, uriParams);

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> result =
                new RestTemplate().postForEntity(
                        uri,
                        entity,
                        String.class);

        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();

        return JsonHelper.fromJson(result.getBody(), mapType);
    }

    default Map<String, Object> get(String url, Map<String, String> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String uri = buildUrl(url, params);

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<String> result =
                new RestTemplate().exchange(
                        uri,
                        HttpMethod.GET,
                        requestEntity,
                        String.class);

        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();

        return JsonHelper.fromJson(result.getBody(), mapType);
    }

    default String buildUrl(String url, Map<String, String> params) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (!CollectionUtils.isEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());
            }
        }

        String uri = builder.build().toUriString();
        return uri;
    }


    default KFMsgBody ofBody(KFMessage kfMessage) {
        return KFMsgBody.of(kfMessage);
    }

    @Getter
    @Builder
    class KFMsgBody {
        private String touser;
        private String msgtype;

        private Text text;

        @Getter
        @Builder
        static class Text {
            private String content;
        }

        private Image image;

        @Getter
        @Builder
        static class Image {
            private String media_id;
        }


        public static KFMsgBody of(KFMessage kfMessage) {
            Preconditions.checkArgument(Objects.nonNull(kfMessage));
            kfMessage.legal();

            String content = kfMessage.getContent();
            if (StringUtils.isNotBlank(content)) {
                content = EmojiParser.removeAllEmojis(content);
            }

            // 确定 msgType
            String msgType = "text";
            if (StringUtils.isNotEmpty(kfMessage.getMsgType())) {
                msgType = kfMessage.getMsgType();
            }

            //确定 image
            Image image = null;
            String mediaId = kfMessage.getMediaId();
            if (StringUtils.isNotBlank(mediaId)) {
                image = Image.builder().media_id(mediaId).build();
            }

            return KFMsgBody.builder()
                    .msgtype(msgType)
                    .touser(kfMessage.getTo().getOpenId())
                    .text(
                            Text.builder().content(content).build()
                    )
                    .image(image)
                    .build();
        }
    }
}
