package com.rokid.iot.portal.speak.application;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpeakResp {

    private ErrNo errNo;

    private String reply;
    private String detail;

    public boolean isSuccess() {
        return ErrNo.SUCCESS.equals(errNo);
    }

    public boolean noNeedToReply() {
        return ErrNo.NO_RESP.equals(errNo);
    }

    public static SpeakResp ofTimeOut() {
        return SpeakResp.builder()
                .errNo(ErrNo.TIME_OUT)
                .reply("服务器开小差了，请重新试一下吧")
                .build();
    }

    public static SpeakResp ofInitiated(
            String nick,
            String url) {

        String origin = TEMPLATE;
        origin = origin.replace("{USER.DATA}", nick);
        String resp = origin.replace("{URL.DATA}", url);

        return SpeakResp.builder()
                .errNo(ErrNo.INIT)
                .reply(resp)
                .build();
    }

    public static SpeakResp ofExample() {

        return SpeakResp.builder()
                .errNo(ErrNo.SUCCESS)
                .reply("【常用指令】\n" +
                        "如果您已经完成设备添加，那您可以按照如下规则对我发起指令哦：\n" +
                        "1. 打开某个设备：例子：打开灯；\n" +
                        "2. 打开某个房间的设备：例子：打开客厅灯；\n" +
                        "3. 若您在使用中有无法控制的指令可以点击问题反馈，反馈给我们哦。")
                .build();
    }

    public static SpeakResp ofHelp() {

        return SpeakResp.builder()
                .errNo(ErrNo.SUCCESS)
                .reply("0571-88730209")
                .build();
    }

    private static final String TEMPLATE = "Hi~~{USER.DATA} 我叫小万(小若琪的弟弟), 一个万能遥控机器人。\n" +
            "我可以帮您控制管理家里的一切设备，希望能成为您的好伙伴！\n您可以点击链接" +
            "<a href=\"{URL.DATA}\">注册/登录Rokid帐户</a>" +
            ", 开启您的魔法之旅";

    enum ErrNo {
        /**
         * 超时
         */
        TIME_OUT,
        INIT,
        /**
         * 【异步】无响应
         */
        NO_RESP,
        /**
         * 成功
         */
        SUCCESS,
        ;
    }

}
