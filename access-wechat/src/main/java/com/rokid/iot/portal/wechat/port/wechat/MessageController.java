package com.rokid.iot.portal.wechat.port.wechat;

import com.rokid.iot.portal.share.Tracer;
import com.rokid.iot.portal.wechat.application.EventApplicationService;
import com.rokid.iot.portal.wechat.application.WeChatApplicationService;
import com.rokid.iot.portal.wechat.application.WechatRequest;
import com.rokid.iot.portal.wechat.inf.XmlConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
public class MessageController implements WechatAuthConcern {

    @Resource
    private SecretPackage secretPackage;
    @Resource
    private WeChatApplicationService wechatApplicationService;
    @Resource
    private EventApplicationService eventApplicationService;

    /**
     * 接收用户的【发送消息】
     */
    @PostMapping(value = "/entry")
    public void receive(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String respBody;
        Tracer tracer = Tracer.of(log).method("MessageController.receive");
        try {
            /*
             * 解密微信消息
             */
            AuthAndRequestBody auth = authCheckAndDecrypt(request, secretPackage);
            tracer.addParam("strReq", auth.getRequestBody());
            /*
             * XML微信消息
             */
            WechatRequest r = XmlConverter
                    .convert(auth.getRequestBody(), WechatRequest.class)
                    .traceAble();
            tracer.addParam("xmlReq", r);

            /*
             * 对于event消息，仅打印日志即可
             */
            if (!r.isEvent()) {
                wechatApplicationService.speak(r);
            }
            /*
             * 对于订阅消息，仅发消息
             */
            if (r.isEvent()) {
                eventApplicationService.handle(r);
            }
            /*
             * 返回成功
             */
            respBody = auth.getAuth().encrypt("success");
            tracer.result(respBody);
        } catch (Exception e) {
            tracer.error(e);
            respBody = "error[" + e.getMessage();
        } finally {
            tracer.print();
        }
        response.getWriter().print(respBody);
    }

    @GetMapping(value = "/entry")
    public String check(HttpServletRequest request) {

        Tracer tracer = Tracer.of(log).method("MessageController.check");
        try {
            checkSignAndGetAuth(request, secretPackage);
            String echostr = request.getParameter("echostr");
            tracer.result(echostr);
            return echostr;
        } catch (Exception e) {
            tracer.error(e);
            return "error[" + e.getMessage();
        } finally {
            tracer.print();
        }
    }

}
