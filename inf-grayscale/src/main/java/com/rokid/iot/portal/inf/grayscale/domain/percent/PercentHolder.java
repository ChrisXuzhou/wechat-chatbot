package com.rokid.iot.portal.inf.grayscale.domain.percent;

import com.rokid.iot.portal.share.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Service
public class PercentHolder {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final long DEFAULT_DURATION = 100; //天

    /**
     * 定义【灰度测试】的percentage来
     */
    private static final String KEY = "iot:portal:wechat:grayscale:percent";

    public int percent() {

        Tracer tracer = Tracer.of(log).method("PercentHolder.percent[]");
        try {
            String percent = stringRedisTemplate.opsForValue().get(KEY);
            if (StringUtils.isBlank(percent)) {
                return 0;
            }
            int percentage = Integer.parseInt(percent);
            tracer.result(percentage);
            return percentage;
        } catch (Exception e) {
            tracer.error(e);
            throw new IllegalStateException("PercentHolder.percent error!", e);
        } finally {
            tracer.print();
        }
    }

    public boolean putPercent(int percent) {

        Tracer tracer = Tracer.of(log, "PercentHolder.putPercent[percent]", percent);
        try {
            this.stringRedisTemplate.opsForValue().set(KEY, String.valueOf(percent));
            tracer.print();
            return true;
        } catch (Exception e) {
            tracer.error(e).print();
            throw new IllegalStateException("PercentHolder.putPercent[percent] error!", e);
        }
    }

    public boolean removePercent() {

        Tracer tracer = Tracer.of(log).method("PercentHolder.removePercent[percent]");
        try {

            Boolean ret = stringRedisTemplate.delete(KEY);
            if (Objects.isNull(ret)) {
                throw new IllegalStateException("ret is null!");
            }
            tracer.result(ret).print();
            return ret;
        } catch (Exception e) {
            tracer.error(e).print();
            throw new IllegalStateException("PercentHolder.removePercent[percent] error!", e);
        }
    }

}
