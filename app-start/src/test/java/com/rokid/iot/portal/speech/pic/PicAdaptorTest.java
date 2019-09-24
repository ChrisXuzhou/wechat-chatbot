package com.rokid.iot.portal.speech.pic;

import com.rokid.iot.Application;
import com.rokid.iot.portal.share.JsonHelper;
import com.rokid.iot.portal.wechat.port.pic.PicAdaptor;
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
public class PicAdaptorTest {

    @Resource
    private PicAdaptor picAdaptor;

    @Test
    public void queryPic() {
        System.out.println(
                JsonHelper.toJson(
                        picAdaptor.currentPics())
        );
    }

}
