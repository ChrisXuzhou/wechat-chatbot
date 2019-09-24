package com.rokid.iot.portal.speech;

import com.rokid.iot.Application;
import com.rokid.iot.portal.wechat.port.wechat.MessageController;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class MessageControllerTest {

    @Resource
    private MessageController messageController;

    @Test
    public void testReceive() throws IOException {

        messageController.receive(
                of("this is a test"),
                new MockHttpServletResponse()
        );
    }

    HttpServletRequest of(String body) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setMethod("POST");
        request.setContent(body.getBytes());

        return request;
    }
}
