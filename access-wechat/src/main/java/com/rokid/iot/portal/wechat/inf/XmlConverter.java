package com.rokid.iot.portal.wechat.inf;

import com.thoughtworks.xstream.XStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

public class XmlConverter {

    private static XStream xstream = new XStream();

    public static  <T> T convert(String raw, Class<T> clazz) throws DocumentException {
        Document doc = DocumentHelper.parseText(raw);
        String xml = doc.asXML();
        xstream.alias("xml", clazz);
        return (T) xstream.fromXML(xml);
    }

    public static String convert(Object obj) {
        xstream.alias("xml", obj.getClass());
        return xstream.toXML(obj);
    }

}
