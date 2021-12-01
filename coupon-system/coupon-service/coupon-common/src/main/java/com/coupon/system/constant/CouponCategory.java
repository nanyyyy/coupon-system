package com.coupon.system.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CouponCategory {
    AMOUNT("Amount discounted from item’s total cost", "001"),
    PERCENTAGE("Percentage off of the total purchase or total item cost", "002"),
    BOGO("Buy one, get one free", "003");

    private String description;
    private String code;

    public static CouponCategory of(String code) {
        Objects.requireNonNull(code);

        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists!"));
    }
}
