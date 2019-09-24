package com.rokid.iot.portal.share;

import lombok.Data;
import org.jboss.logging.MDC;

@Data
public class Traceable {

    private String traceId;
    private String traceVoice;

    public void leaveTrace() {

        MDC.put("traceId", traceId);
        MDC.put("sentence", traceVoice);
    }
}
