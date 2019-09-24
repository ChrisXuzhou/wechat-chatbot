package com.rokid.iot.portal.wechat.inf;

public class ByteHelper {


    public static String byteToStr(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (Byte each : byteArray) {
            sb.append(byteToHexStr(each));
        }
        return sb.toString();
    }

    private static final char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static String byteToHexStr(byte mByte) {
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];
        return new String(tempArr);
    }
}



