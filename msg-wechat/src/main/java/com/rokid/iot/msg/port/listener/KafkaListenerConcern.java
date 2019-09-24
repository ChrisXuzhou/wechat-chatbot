package com.rokid.iot.msg.port.listener;

import com.rokid.iot.portal.share.DateHelper;
import com.rokid.iot.portal.share.wechat.WechatConcern;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.util.Calendar;

public interface KafkaListenerConcern {

    @Data
    class KafkaMessage implements WechatConcern {

        private String voice;
        private String startAt;
        private String type;
        private String deviceId;
        private String deviceTypeId;
        private String userId;
        private String activityId;
        private String actionUrlV1;
        private String actionUrlV2;
        private String title;
        private String content;

        String ofOpenId() {
            return ofOpenId(getDeviceId());
        }

        String ofPath() {
            if (StringUtils.isNotBlank(actionUrlV2)) {
                return actionUrlV2;
            }
            return "";
        }

        boolean wechatConcern() {
            return StringUtils.startsWith(getDeviceId(), wechatPrefix())
                    && StringUtils.equals(deviceTypeId, wechatDeviceTypeId());
        }

        String happenedAt() {
            if (StringUtils.isBlank(startAt)) {
                return null;
            }
            Calendar calendar = DatatypeConverter.parseDateTime(startAt);
            return DateHelper.pareDate(calendar.getTime());
        }
    }

    @Data
    class KafkaRespVoice implements WechatConcern {
        /**
         * 用户的控制语音
         */
        private String userText;
        /**
         * 响应语音
         */
        private String tts;
        private String deviceId;
        private String deviceTypeId;
        private String userId;

        String ofOpenId() {
            return ofOpenId(getDeviceId());
        }

        boolean wechatConcern() {
            return StringUtils.startsWith(getDeviceId(), wechatPrefix())
                    && StringUtils.equals(deviceTypeId, wechatDeviceTypeId());
        }

    }


}
