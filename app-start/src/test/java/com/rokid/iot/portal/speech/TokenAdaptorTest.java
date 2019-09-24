package com.rokid.iot.portal.speech;

import com.rokid.iot.Application;
import com.rokid.iot.msg.port.token.TokenAdaptor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TokenAdaptorTest {

    @Resource
    private TokenAdaptor tokenAdaptor;

    @Test
    public void testGetToken() {
        System.out.println(tokenAdaptor.of());
    }

    @Test
    public void testOfToken() {
        System.out.println(tokenAdaptor.ofForeignToken());
    }
}
