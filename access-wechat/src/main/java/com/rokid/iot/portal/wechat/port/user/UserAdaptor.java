package com.rokid.iot.portal.wechat.port.user;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rokid.iot.msg.port.MsgHttpConcern;
import com.rokid.iot.msg.port.token.TokenAdaptor;
import com.rokid.iot.portal.share.JsonHelper;
import com.rokid.iot.portal.share.Tracer;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * ?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
 */
@Slf4j
@Service
public class UserAdaptor implements MsgHttpConcern {

    @Value("${wechat.user.info.url}")
    private String url;
    @Value("${wechat.auth.appId}")
    private String appId;
    @Resource
    private MeterRegistry registry;
    @Resource
    private TokenAdaptor tokenAdaptor;

    public UserDescriptor of(String openId) {

        Tracer tracer = Tracer.of(log, "UserAdaptor.of", openId)
                .registry(registry)
                .addParam("url", url);
        try {
            Map<String, String> params = Maps.newHashMap();
            params.put("access_token", tokenAdaptor.of());
            params.put("openid", openId);
            params.put("lang", "zh_CN");

            Map<String, Object> ret = get(url, params);
            tracer.result(ret);
            String userNick = (String) ret.get("nickname");
            Preconditions.checkArgument(StringUtils.isNotBlank(userNick), "usr info empty, " + JsonHelper.toJson(ret));

            return UserDescriptor.builder().nickName(userNick).build();

        } catch (Exception e) {
            tracer.error(e);
            throw new IllegalStateException("Wechat Http Commute Error [" + e.getMessage(), e);
        } finally {
            tracer.print();
        }

    }

    @Getter
    @Builder
    public static class UserDescriptor {
        private String nickName;
    }

}
