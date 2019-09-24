package com.rokid.iot.msg.port.kf;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rokid.iot.msg.domain.KFMessage;
import com.rokid.iot.msg.port.MsgHttpConcern;
import com.rokid.iot.msg.port.token.TokenAdaptor;
import com.rokid.iot.portal.share.JsonHelper;
import com.rokid.iot.portal.share.Tracer;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 【微信】客服消息发送
 */
@Slf4j
@Service
public class WechatKFAdaptor implements MsgHttpConcern {

    @Value("${wechat.customer.service.url}")
    private String wechatURL;

    @Resource
    private TokenAdaptor tokenAdaptor;
    @Resource
    private MeterRegistry registry;

    public void sendKFMsg(KFMessage kfMessage) {

        Tracer tracer = Tracer.of(log, "WechatKFAdaptor.sendKFMsg", kfMessage)
                .registry(registry)
                .addParam("url", wechatURL);
        try {
            Map<String, String> params = Maps.newHashMap();
            params.put("access_token", tokenAdaptor.of());

            KFMsgBody body = ofBody(kfMessage);
            tracer.addParam("body", JsonHelper.toJson(body));

            Map<String, Object> ret = post(wechatURL, params, body);
            tracer.result(ret);
            Preconditions.checkArgument(
                    StringUtils.equals("ok", (String) ret.get("errmsg")),
                    "Wechat commute error[" + JsonHelper.toJson(ret));
        } catch (Exception e) {
            tracer.error(e);
            throw new IllegalStateException("Wechat Http Commute Error [" + e.getMessage(), e);
        } finally {
            tracer.print();
        }
    }

}
