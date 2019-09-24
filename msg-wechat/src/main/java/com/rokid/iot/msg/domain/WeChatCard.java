package com.rokid.iot.msg.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rokid.iot.portal.share.DateHelper;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;

@Data
@Builder
public class WeChatCard implements MsgConcern {

    private String voice;

    private final String current = DateHelper.current();

    private User to;
    private Forward forward;

    private CardType cardType;
    /**
     * 卡片信息
     */
    private String title;
    private String subTitle;
    private final Map<String, String> details = Maps.newHashMap();

    public WeChatCard legal() {
        Preconditions.checkArgument(Objects.nonNull(to), "card sent to is null");
        Preconditions.checkArgument(Objects.nonNull(forward), "card forward to is null");
        Preconditions.checkArgument(Objects.nonNull(cardType), "card type to is null");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(details), "details is empty");
        return this;
    }

    public WeChatCard putDetail(String key, String detail) {
        details.put(key, detail);
        return this;
    }
}
