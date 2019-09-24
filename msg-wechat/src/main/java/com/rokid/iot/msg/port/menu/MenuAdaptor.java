package com.rokid.iot.msg.port.menu;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class MenuAdaptor implements MsgHttpConcern {

    @Value("${wechat.menu.define.url}")
    private String url;
    @Resource
    private MeterRegistry registry;
    @Resource
    private TokenAdaptor tokenAdaptor;

    public void defineMenu() {

        Menu menu = ofMenu();

        Tracer tracer = Tracer.of(log, "MenuAdaptor.defineMenu", menu)
                .registry(registry)
                .addParam("url", url);
        try {
            Map<String, String> params = Maps.newHashMap();
            params.put("access_token", tokenAdaptor.of());
            Map<String, Object> ret = post(url, params, menu);
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

    @Value("${rokid.iot.mobile.url}")
    private String homeUrl;
    @Value("${rokid.iot.mobile.home.path}")
    private String homePath;

    private Menu ofMenu() {

        Menu menu = new Menu();

        menu.add(
                Menu.Button.builder()
                        .type("view")
                        .name("设备管理")
                        .url(homeUrl)
                        .pagepath("/wechat/auth")
                        .build()
        ).add(
                Menu.Button.builder()
                        .type("view")
                        .name("问题反馈")
                        .sub_button(
                                Lists.newArrayList(
                                        Menu.Button.builder()
                                                .type("click")
                                                .name("加入社群")
                                                .key("JOIN_COMMUNITY")
                                                .build(),
                                        Menu.Button.builder()
                                                .type("click")
                                                .name("联系客服")
                                                .key("HELP")
                                                .build(),
                                        Menu.Button.builder()
                                                .type("view")
                                                .name("常见问题")
                                                .url("https://iot-mobile-test.rokid.com")
                                                .build(),
                                        Menu.Button.builder()
                                                .type("click")
                                                .name("常用指令")
                                                .key("EXAMPLE")
                                                .build()
                                )
                        )
                        .build()
        );
        return menu;
    }

    @Getter
    static class Menu {

        private final List<Button> button = Lists.newArrayList();

        public Menu add(Button bu) {
            Preconditions.checkArgument(Objects.nonNull(bu));
            button.add(bu);
            return this;
        }

        @Getter
        @Builder
        static class Button {

            private String type;
            private String name;
            private String key;

            private String url;
            private String appid;
            private String pagepath;

            private List<Button> sub_button;

        }
    }

    private String deleteUrl = "https://api.weixin.qq.com/cgi-bin/menu/delete";

    public void delete() {

        Tracer tracer = Tracer.of(log, "MenuAdaptor.deleteMenu", null)
                .addParam("url", deleteUrl);
        try {
            Map<String, String> params = Maps.newHashMap();
            params.put("access_token", tokenAdaptor.of());
            Map<String, Object> ret = get(deleteUrl, params);
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
