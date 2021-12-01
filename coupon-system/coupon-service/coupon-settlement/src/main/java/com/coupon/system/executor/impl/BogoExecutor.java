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
public class BogoExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.BOGO;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {
        double goodsSum = retain2Decimals(goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlementInfo, goodsSum);
        // item type does not match the coupon limit
        if (null != probability) {
            log.debug("BOGO Template Is Not Match To Goods Type!");
            return probability;
        }

        // item type matches the coupon limit
        // BOGO discount coupon can be used directly, no threshold
        double freeSum = settlementInfo.getGoodsInfos().stream().mapToDouble(goodsInfo ->
                goodsInfo.getCount() / 2 * goodsInfo.getPrice()).sum();

        // calculate the price after using the coupon
        settlementInfo.setCost(retain2Decimals(
                (goodsSum - freeSum) > minCost() ? (goodsSum - freeSum) : minCost()
        ));

        log.debug("Use BOGO Coupon Make Goods Cost From {} to {}",
                goodsSum, settlementInfo.getCost());

        return settlementInfo;
    }
}
