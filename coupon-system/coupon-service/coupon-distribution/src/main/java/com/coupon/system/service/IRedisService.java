package com.coupon.system.service;

import com.coupon.system.entity.Coupon;
import com.coupon.system.exception.CouponException;

import java.util.List;

public interface IRedisService {

    /**
     * get cached coupons by userId + couponStatus
     * @param userId
     * @param status
     * @return
     */
    List<Coupon> getCachedCoupons(Long userId, Integer status);

    /**
     * save an empty list to cache
     * @param userId
     * @param status
     */
    void saveEmptyCouponListToCache(Long userId, List<Integer> status);

    /**
     * try to get a coupon code from cache
     * @param templateId
     * @return
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    /**
     * save coupon to cache
     * @param userId
     * @param coupons
     * @param status
     * @return
     * @throws Exception
     */
    Integer addCouponToCache(Long userId, List<Coupon> coupons,
                             Integer status) throws CouponException;
}
