package com.coupon.system.service;

import com.coupon.system.entity.CouponTemplate;

/**
 * Asynchronous service interface
 */
public interface IAsyncService {

    /**
     * Create coupons asynchronously according to the template
     * @param couponTemplate
     */
    void asyncConstructCouponByTemplate(CouponTemplate couponTemplate);
}
