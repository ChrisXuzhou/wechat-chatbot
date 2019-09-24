package com.rokid.iot.msg.domain;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public interface MsgConcern {

    @Getter
    @Builder
    class User {
        /**
         * 名称
         */
        private String name;
        /**
         * 微信OPEN_ID
         */
        private String openId;

        public User legal() {
            Preconditions.checkArgument(StringUtils.isNotBlank(openId));
            return this;
        }
    }

    @Getter
    @Builder
    class Forward {

        private String path;
        private Map<String, String> values;

    }

    enum CardType {
        /**
         * 错误卡片
         */
        CONTROL_ERROR {
            @Override
            public String getTemplateId() {
                return "RyAYSmBO9LmnoDoKmAnq3Dkj_-OLkmwpMsynR_2u1zg";
            }

            @Override
            public String errorType() {
                return "控制报错";
            }
        },

        PORTAL_ERROR {
            @Override
            public String getTemplateId() {
                return "RyAYSmBO9LmnoDoKmAnq3Dkj_-OLkmwpMsynR_2u1zg";
            }

            @Override
            public String errorType() {
                return "服务报错";
            }
        },

        ;

        public String getTemplateId() {
            throw new UnsupportedOperationException();
        }

        public String errorType() {
            throw new UnsupportedOperationException();
        }
    }
}
