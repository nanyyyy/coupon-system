package com.coupon.system.executor.impl;

import com.coupon.system.constant.RuleFlag;
import com.coupon.system.executor.AbstractExecutor;
import com.coupon.system.executor.RuleExecutor;
import com.coupon.system.vo.CouponTemplateSDK;
import com.coupon.system.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PercentageExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.PERCENTAGE;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {
        double goodsSum = retain2Decimals(goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlementInfo, goodsSum);
        // item type does not match the coupon limit
        if (null != probability) {
            log.debug("Percentage Template Is Not Match To Goods Type!");
            return probability;
        }

        // item type matches the coupon limit
        // percentage discount coupon can be used directly, no threshold
        CouponTemplateSDK templateSDK = settlementInfo.getCouponAndTemplateInfos().get(0).getTemplate();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();

        // calculate the price after using the coupon
        settlementInfo.setCost(retain2Decimals(
                (goodsSum * (quota * 1.0 / 100)) > minCost() ? (goodsSum * (quota * 1.0 / 100)) : minCost()
        ));

        log.debug("Use ZheKou Coupon Make Goods Cost From {} To {}",
                goodsSum, settlementInfo.getCost());

        return settlementInfo;
    }
}
