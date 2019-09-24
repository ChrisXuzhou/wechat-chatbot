package com.rokid.iot.msg.port.card;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rokid.iot.msg.domain.WeChatCard;
import com.rokid.iot.msg.port.MsgHttpConcern;
import com.rokid.iot.msg.port.token.TokenAdaptor;
import com.rokid.iot.portal.share.JsonHelper;
import com.rokid.iot.portal.share.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class WechatCardAdaptor implements MsgHttpConcern, WechatCardConcern {

    @Resource
    private TokenAdaptor tokenAdaptor;

    public String ofTemplates() {

        Map<String, String> params = Maps.newHashMap();
        params.put("access_token", tokenAdaptor.of());
        String getTemplatesUrl = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template";
        Map<String, Object> ret = get(getTemplatesUrl, params);

        log.info(JsonHelper.toJson(ret));
        return JsonHelper.toJson(ret);
    }

    @Value("${wechat.card.send.url}")
    private String sendCardUrl;

    public void send(WeChatCard wechatCard) {

        wechatCard.legal();

        final Body body = Body.builder()
                .touser(wechatCard.getTo().getOpenId())
                .template_id(wechatCard.getCardType().getTemplateId())
                .url(wechatCard.getForward().getPath())
                .build();

        if (!CollectionUtils.isEmpty(wechatCard.getDetails())) {
            for (Map.Entry<String, String> entry : wechatCard.getDetails().entrySet()) {
                body.putData(
                        entry.getKey(),
                        Body.Data.builder()
                                .value(entry.getValue())
                                //.color("#173177")
                                .build()
                );
            }
        }
        body.putData("current",
                Body.Data.builder()
                        .value(wechatCard.getCurrent())
                        //.color("#173177")
                        .build()
        );

        Tracer tracer = Tracer.of(log, "WechatCardAdaptor.send", body);
        try {
            Map<String, String> params = Maps.newHashMap();
            params.put("access_token", tokenAdaptor.of());
            Map<String, Object> ret = post(sendCardUrl, params, body);
            tracer.result(ret);

            Preconditions.checkState(StringUtils.equals("ok", (String) ret.get("errmsg")));
        } catch (Exception e) {
            tracer.error(e).print();
            throw e;
        }
    }
}
