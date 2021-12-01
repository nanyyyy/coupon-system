package com.coupon.system.dao;

import com.coupon.system.constant.CouponStatus;
import com.coupon.system.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponDao extends JpaRepository<Coupon, Integer> {

    /**
     * find coupon record by userId + couponStatus
     * @param userId
     * @param status
     * @return
     */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);
}
