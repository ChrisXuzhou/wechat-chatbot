package com.rokid.iot.portal.inf.grayscale.domain;

import com.google.common.base.Preconditions;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Value
public class GrayScaleRequest {

    private String uniqueness;

    public GrayScaleRequest legal() {
        Preconditions.checkArgument(StringUtils.isNotBlank(getUniqueness()), "uniqueness is empty");
        return this;
    }
}
