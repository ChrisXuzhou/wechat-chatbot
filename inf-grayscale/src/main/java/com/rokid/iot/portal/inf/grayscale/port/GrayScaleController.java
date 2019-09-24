package com.rokid.iot.portal.inf.grayscale.port;

import com.google.common.base.Preconditions;
import com.rokid.iot.portal.inf.grayscale.domain.percent.PercentHolder;
import com.rokid.iot.portal.share.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class GrayScaleController {

    @Value("${rokid.portal.control.access.token}")
    private String accessToken;

    @Resource
    private PercentHolder percentHolder;

    @PostMapping(path = "/config/percent/{token}/change/{percent}")
    public Boolean setPercent(@PathVariable String token,
                              @PathVariable Integer percent) {
        Tracer tracer = Tracer.of(log, "GrayScaleController.setPercent", percent).addParam("token", token);

        try {
            accessAllowed(token);
            boolean ret = percentHolder.putPercent(percent);
            tracer.result(ret).print();
            return ret;
        } catch (Exception e) {
            tracer.error(e).print();
            return false;
        }
    }

    @GetMapping(path = "/config/percent/{token}/get")
    public String getPercent(@PathVariable String token) {
        Tracer tracer = Tracer.of(log).method("GrayScaleController.getPercent").addParam("token", token);

        try {
            accessAllowed(token);
            int ret = percentHolder.percent();
            tracer.result(ret).print();
            return String.valueOf(ret);
        } catch (Exception e) {
            tracer.error(e).print();
            return "报错了:" + e.getMessage();
        }
    }

    @PostMapping(path = "/config/percent/{token}/remove")
    public Boolean removePercent(@PathVariable String token) {
        Tracer tracer = Tracer.of(log).method("GrayScaleController.removePercent").addParam("token", token);

        try {
            accessAllowed(token);
            boolean ret = percentHolder.removePercent();
            tracer.result(ret).print();
            return ret;
        } catch (Exception e) {
            tracer.error(e).print();
            return false;
        }
    }

    private void accessAllowed(String token) {
        Preconditions.checkArgument(StringUtils.equals(token, accessToken), "accessToken not legal");
    }

}
