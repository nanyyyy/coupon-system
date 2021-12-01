package com.coupon.system.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Rule type enumeration definition
 */
@Getter
@AllArgsConstructor
public enum RuleFlag {

    // single discount type
    AMOUNT("Amount discounted caculation rule"),
    PERCENTAGE("Percentage off caculation rule"),
    BOGO("BOGO caculation rule"),

    // multiple discount type
    AMOUNT_PERCENTAGE("Amount discounted + percentage off caculation rule");

    /** description of caculation rule */
    private String description;
}
