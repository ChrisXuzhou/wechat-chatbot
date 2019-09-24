package com.rokid.iot.portal.speech.menu;


import com.rokid.iot.Application;
import com.rokid.iot.msg.port.menu.MenuAdaptor;
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
public class MenuAdaptorTest {

    @Resource
    private MenuAdaptor menuAdaptor;

    @Test
    public void testCreateMenu() {
        menuAdaptor.defineMenu();
    }

    @Test
    public void testDeleteMenu() {
        menuAdaptor.delete();
    }
}
