package com.coupon.system.executor.impl;

import com.coupon.system.constant.RuleFlag;
import com.coupon.system.executor.AbstractExecutor;
import com.coupon.system.executor.RuleExecutor;
import com.coupon.system.vo.CouponTemplateSDK;
import com.coupon.system.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class AmountExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.AMOUNT;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {
        double goodsSum = retain2Decimals(goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlementInfo, goodsSum);
        // item type does not match the coupon limit
        if (null != probability) {
            log.debug("Amount Template Is Not Match To Goods Type!");
            return probability;
        }
        // item type matches the coupon limit
        // check if the amount discount meet the CouponTemplateSDK rule
        CouponTemplateSDK templateSDK = settlementInfo.getCouponAndTemplateInfos().get(0).getTemplate();
        double base = (double) templateSDK.getRule().getDiscount().getBase();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        // if it does not met the CouponTemplateSDK rule, the total price of the item is returned directly
        if (goodsSum < base) {
            log.debug("Current Goods Cost Sum < Amount Coupon Base");
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
            return settlementInfo;
        }

        // if it met the rule
        // calculate the price after using the coupon
        settlementInfo.setCost(retain2Decimals(
                (goodsSum - quota) > minCost() ? (goodsSum - quota) : minCost()
        ));
        log.debug("Use Amount Coupon Make Goods Cost From {} To {}",
                goodsSum, settlementInfo.getCost());

        return settlementInfo;
    }
}












