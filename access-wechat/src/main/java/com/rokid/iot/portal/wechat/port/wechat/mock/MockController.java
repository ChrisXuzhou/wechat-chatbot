package com.rokid.iot.portal.wechat.port.wechat.mock;

import com.rokid.iot.portal.share.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class MockController {

    @PostMapping(value = "/emoji/body")
    public void receive(@RequestBody Map params) {

        log.info("params: {}", JsonHelper.toJson(params));

    }
}
