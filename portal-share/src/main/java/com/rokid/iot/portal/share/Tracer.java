package com.rokid.iot.portal.share;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Data
@Slf4j
public class Tracer {

    private long beginTime;
    private long endTime;

    public Tracer begin() {
        this.setBeginTime(System.currentTimeMillis());
        return this;
    }

    public Tracer end() {
        long end = System.currentTimeMillis();
        this.setEndTime(end);
        this.setTimeCost(end - this.getBeginTime());
        return this;
    }

    public Tracer log(Logger log) {
        this.setLogger(log);
        return this;
    }

    Logger logger;

    public static Tracer of(Logger log) {
        return new Tracer().begin().log(log);
    }

    public static Tracer of(Logger log, String method, Object request) {
        Tracer tracer = new Tracer().begin().addParam("request", request).method(method).log(log);
        if (request instanceof Traceable) {
            tracer.leaveTrace((Traceable) request);
        }
        return tracer;
    }

    public Tracer addParam(String key, Object param) {
        this.params.put(key, param);
        if (param instanceof Traceable) {
            leaveTrace((Traceable) param);
        }
        return this;
    }

    public Tracer error(Throwable throwable) {
        logger.error("throwable: ", throwable);
        log.error("throwable: ", throwable);
        success = false;
        this.setErrorMsg("ERROR [" + throwable.toString() + JsonHelper.toJson(throwable));
        return this;
    }

    public Tracer result(Object resp) {
        this.setResponse(resp);
        return this;
    }

    public Tracer method(String method) {
        this.setMethod(method);
        return this;
    }

    private Boolean success = true;
    private long timeCost;
    private String method;
    private Map<String, Object> params = Maps.newHashMap();
    private Object response;
    private String errorMsg;


    private Date traceTime = new Date();

    private MeterRegistry registry;

    public Tracer registry(MeterRegistry registry) {
        this.setRegistry(registry);
        return this;
    }

    private String tag;

    public Tracer metricTag(String tag) {
        this.tag = tag;
        return this;
    }

    private void uploadMetrics() {
        if (Objects.nonNull(registry)) {

            StringBuilder sb = new StringBuilder();
            String ret = "FAILED";
            if (success) {
                ret = "SUCCESS";
            }
            sb.append(ret);
            if (StringUtils.isNotBlank(getTag())) {
                sb.append(":").append(Strings.nullToEmpty(getTag()));
            }

            Counter counter = Counter.builder("TRACER:" + getMethod())
                    .description(getMethod())
                    .tag(getMethod(), sb.toString())
                    .register(registry);

            counter.increment();
        }
    }

    public void print() {

        uploadMetrics();
        this.end();
        setRegistry(null);

        log.info("[TRACER] {}:{} [{}ms] [{}] {}",
                logger.getName(),
                method, this.getTimeCost(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(traceTime),
                JsonHelper.toJson(this));
    }

    public Tracer leaveTrace(Traceable traceable) {
        traceable.leaveTrace();
        return this;
    }

    public static String errorResp(Exception e) {

        StringBuilder sb = new StringBuilder("服务器报错了，非常抱歉。");

        String errorMsg = e.getMessage();
        String traceId = MDC.get("traceId");

        if (StringUtils.isNotBlank(errorMsg)
                && !StringUtils.equals(errorMsg, "null")) {
            sb.append("\n").append("详情: ").append(errorMsg);
        }

        if (StringUtils.isNotBlank(traceId)) {
            sb.append("\n").append("TRACE: ").append(traceId);
        }

        return sb.toString();
    }
}
