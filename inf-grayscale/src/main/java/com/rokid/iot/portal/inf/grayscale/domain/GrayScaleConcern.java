package com.rokid.iot.portal.inf.grayscale.domain;

import com.rokid.iot.portal.inf.grayscale.domain.hash.Harsher;
import com.rokid.iot.portal.share.Tracer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 【灰度】相关
 */
public interface GrayScaleConcern {

    interface Strategy {

        Advice giveAdvice();
    }

    /**
     * 灰度的结果ENUM
     */
    enum Advice {
        A,
        B,
        ;
    }

    @Slf4j
    @AllArgsConstructor
    class DefaultStrategy implements Strategy {

        private String uniqueness;

        private int percent;
        private Harsher harsher;

        public static DefaultStrategy of(int percent,
                                         Harsher harsher,
                                         GrayScaleRequest request) {
            return new DefaultStrategy(request.getUniqueness(), percent, harsher);
        }

        @Override
        public Advice giveAdvice() {

            Tracer tracer = Tracer.of(log, "DefaultStrategy.giveAdvice", this);
            int index = harsher.hash(uniqueness);
            tracer.addParam("index", index);
            Advice advice = (index < percent) ? Advice.A : Advice.B;

            tracer.result(advice).print();
            return advice;
        }


    }

}
