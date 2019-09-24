package com.rokid.iot.portal.speech;

import com.rokid.iot.Application;
import com.rokid.iot.portal.share.JsonHelper;
import com.rokid.iot.portal.wechat.port.user.UserAdaptor;
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
public class UserAdaptorTest {

    @Resource
    private UserAdaptor userAdaptor;

    @Test
    public void testOfUser() {

        System.out.println(
                JsonHelper.toJson(
                        userAdaptor.of("oVcpRwwWBjlcCnbQuk40BUFCSk_U"))
        );
    }
}
