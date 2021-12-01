package com.coupon.system.service;

import com.coupon.system.entity.Coupon;
import com.coupon.system.exception.CouponException;
import com.coupon.system.vo.AcquireTemplateRequest;
import com.coupon.system.vo.CouponTemplateSDK;
import com.coupon.system.vo.SettlementInfo;

import java.util.List;

/**
 * The interface for user related service
 * 1. show user coupon with 3 kinds of status
 * 2. check which coupon template user can get (coupon-template service)
 * 3. getting coupon service
 * 4. consuming coupon service (coupon-settlement service)
 */
public interface IUserService {

    /**
     * find coupon record by userId + couponStatus
     * @param userId
     * @param status
     * @return
     * @throws CouponException
     */
    List<Coupon> findCouponByStatus(Long userId, Integer status) throws CouponException;

    /**
     * find availble template by userId
     * @param userId
     * @return
     * @throws CouponException
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws  CouponException;

    /**
     * acquire template
     * @param request
     * @return
     * @throws Exception
     */
    Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException;

    /**
     * coupon settlement
     * @param info
     * @return
     * @throws Exception
     */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;

}
