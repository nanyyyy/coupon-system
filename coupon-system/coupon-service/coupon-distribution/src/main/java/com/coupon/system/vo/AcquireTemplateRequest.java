package com.coupon.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The definition of getting coupon request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcquireTemplateRequest {
    private Long userId;
    private CouponTemplateSDK templateSDK;
}
