package com.rokid.iot.portal.inf.grayscale.application;

import com.rokid.iot.portal.inf.grayscale.domain.GrayScaleConcern;
import com.rokid.iot.portal.inf.grayscale.domain.GrayScaleRequest;
import com.rokid.iot.portal.inf.grayscale.domain.hash.DefaultHarsher;
import com.rokid.iot.portal.inf.grayscale.domain.percent.PercentHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class ABApplicationService implements GrayScaleConcern {

    @Resource
    private PercentHolder percentHolder;
    @Resource
    private DefaultHarsher defaultHarsher;

    public String giveAdvice(GrayScaleRequest request) {
        int percent = percentHolder.percent();
        return DefaultStrategy.of(percent, defaultHarsher, request.legal())
                .giveAdvice().name();
    }
}
