package com.coupon.system.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CouponStatus {

    /** three different kinds of coupon status */
    USABLE("可用的", 1),
    USED("已使用的", 2),
    EXPIRED("过期为使用的", 3);

    private String description;
    private Integer code;

    public static CouponStatus of (Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "not exists"));
    }
}
