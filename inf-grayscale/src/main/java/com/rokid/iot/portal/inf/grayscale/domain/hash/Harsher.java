package com.rokid.iot.portal.inf.grayscale.domain.hash;

public interface Harsher {

    /**
     * 基于原始String的输入，返回【0-100】内的Integer
     *
     * @param origin String输入
     * @return int
     */
    int hash(String origin);
}
