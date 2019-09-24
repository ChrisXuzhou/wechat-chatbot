package com.rokid.iot.portal.wechat.port.pic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rokid.iot.msg.port.MsgHttpConcern;
import com.rokid.iot.msg.port.token.TokenAdaptor;
import com.rokid.iot.portal.share.Tracer;
import com.rokid.iot.portal.wechat.domain.MediaDescriptor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class PicAdaptor implements MsgHttpConcern {

    @Value("${wechat.pic.query.url}")
    private String url;

    @Getter
    @Builder
    public static class PicParam {

        private String type;
        private String offset;
        private String count;

        static PicParam ofDefault() {
            return PicParam.builder().type("image").offset("0").count("20")
                    .build();
        }
    }

    @Resource
    private TokenAdaptor tokenAdaptor;

    public List<Map> currentPics() {

        PicParam picParam = PicParam.ofDefault();

        Tracer tracer = Tracer.of(log, "PicAdaptor.currentPics", picParam)
                .addParam("url", url);
        try {
            Map<String, String> params = Maps.newHashMap();
            params.put("access_token", tokenAdaptor.of());

            Map<String, Object> ret = post(url, params, picParam);
            tracer.result(ret);

            List<Map> itemList = (List<Map>) ret.get("item");
            if (Objects.isNull(itemList)) {
                itemList = Lists.newArrayList();
            }

            return itemList;
        } catch (Exception e) {
            tracer.error(e);
            throw new IllegalStateException("Wechat Http Commute Error [" + e.getMessage(), e);
        } finally {
            tracer.print();
        }
    }

    public MediaDescriptor ofHelpPic() {

        List<Map> pics = currentPics();
        if (CollectionUtils.isEmpty(pics)) {
            return null;
        }

        for (Map pic : pics) {
            String name = (String) pic.get("name");
            if (name.startsWith("HELP")) {
                return MediaDescriptor.builder()
                        .media_id((String) pic.get("media_id"))
                        .name(name)
                        .build();
            }
        }
        return null;
    }


    public MediaDescriptor ofCommunityPic() {

        List<Map> pics = currentPics();
        if (CollectionUtils.isEmpty(pics)) {
            return null;
        }

        for (Map pic : pics) {
            String name = (String) pic.get("name");
            if (name.startsWith("COMMUNITY")) {
                return MediaDescriptor.builder()
                        .media_id((String) pic.get("media_id"))
                        .name(name)
                        .build();
            }
        }
        return null;
    }
}
