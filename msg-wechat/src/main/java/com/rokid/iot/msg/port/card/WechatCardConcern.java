package com.rokid.iot.msg.port.card;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

public interface WechatCardConcern {

    /*
     * {
     *            "touser":"OPENID",
     *            "template_id":"ngqIpbwh8bUfcSsECmogfXcV14J0tQlEpBO27izEYtY",
     *            "url":"http://weixin.qq.com/download",
     *            "miniprogram":{
     *              "appid":"xiaochengxuappid12345",
     *              "pagepath":"index?foo=bar"
     *            },
     *            "data":{
     *                    "first": {
     *                        "value":"恭喜你购买成功！",
     *                        "color":"#173177"
     *                    },
     *                    "keyword1":{
     *                        "value":"巧克力",
     *                        "color":"#173177"
     *                    },
     *                    "keyword2": {
     *                        "value":"39.8元",
     *                        "color":"#173177"
     *                    },
     *                    "keyword3": {
     *                        "value":"2014年9月22日",
     *                        "color":"#173177"
     *                    },
     *                    "remark":{
     *                        "value":"欢迎再次购买！",
     *                        "color":"#173177"
     *                    }
     *            }
     *        }
     */

    @Builder
    @Getter
    class Body {
        private String touser;
        private String template_id;
        private String url;
        private Miniprogram miniprogram;

        private final Map<String, Data> data = Maps.newHashMap();

        @Builder
        @Getter
        public static class Miniprogram {
            private String appid;
            private String pagepath;
        }

        @Builder
        @Getter
        public static class Data {
            private String value;
            private String color;
        }

        public Body putData(String key, Data dataItem) {
            Preconditions.checkArgument(StringUtils.isNotBlank(key));
            Preconditions.checkArgument(Objects.nonNull(dataItem));
            getData().put(key, dataItem);
            return this;
        }
    }
}
