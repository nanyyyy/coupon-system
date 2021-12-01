package com.coupon.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * The definition of SettlementInfo
 * Includes:
 * 1. userId
 * 2. goods list
 * 3. coupon and template list
 * 4. settlement result amount
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementInfo {

    private Long userId;
    private List<GoodsInfo> goodsInfos;
    private List<CouponAndTemplateInfo> couponAndTemplateInfos;
    /** whether to make the settlement effective */
    private Boolean employ;
    /** settlement result amount */
    private Double cost;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponAndTemplateInfo {
        /** the primary key of coupon */
        private Integer id;
        /** the template corresponding to the coupon */
        private CouponTemplateSDK template;
    }
}
