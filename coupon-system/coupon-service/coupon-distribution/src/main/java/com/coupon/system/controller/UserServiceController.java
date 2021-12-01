package com.coupon.system.controller;

import com.alibaba.fastjson.JSON;
import com.coupon.system.entity.Coupon;
import com.coupon.system.exception.CouponException;
import com.coupon.system.service.IUserService;
import com.coupon.system.vo.AcquireTemplateRequest;
import com.coupon.system.vo.CouponTemplateSDK;
import com.coupon.system.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class UserServiceController {

    @Autowired
    private final IUserService userService;
    public UserServiceController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * find coupon records based on user id and couponStatus
     * @param userId
     * @param status
     * @return
     * @throws CouponException
     */
    @GetMapping("/coupons")
    public List<Coupon> findCouponByStatus(
            @RequestParam("userId") Long userId,
            @RequestParam("status") Integer status) throws CouponException {

        log.info("Find Coupons By Status: {}, {}", userId, status);
        return userService.findCouponByStatus(userId, status);
    }

    /**
     * find usable coupons by user id
     * @param userId
     * @return
     * @throws CouponException
     */
    @GetMapping("/template")
    public List<CouponTemplateSDK> findAvailableTemplate(
            @RequestParam("userId") Long userId) throws CouponException {
        log.info("Find Available Template: {}", userId);
        return userService.findAvailableTemplate(userId);
    }

    /**
     * user get coupons
     * @param request
     * @return
     * @throws CouponException
     */
    @PostMapping("/acquire/template")
    public Coupon acquireTemplate(@RequestBody AcquireTemplateRequest request) throws Exception {
        log.info("Acquire Template: {}", JSON.toJSONString(request));
        return userService.acquireTemplate(request);
    }

    /**
     * coupon settlement
     * @param info
     * @return
     * @throws CouponException
     */
    @PostMapping("/settlement")
    public SettlementInfo settlement(@RequestBody SettlementInfo info) throws CouponException {
        log.info("Settlement: {}", JSON.toJSONString(info));
        return userService.settlement(info);
    }

}
