package com.coupon.system.executor.impl;

import com.alibaba.fastjson.JSON;
import com.coupon.system.constant.CouponCategory;
import com.coupon.system.constant.RuleFlag;
import com.coupon.system.executor.AbstractExecutor;
import com.coupon.system.executor.RuleExecutor;
import com.coupon.system.vo.GoodsInfo;
import com.coupon.system.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AmountPercentageExecutor extends AbstractExecutor implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.AMOUNT_PERCENTAGE;
    }

    @Override
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlementInfo) {
        log.debug("Check Amount And Percentage Is Match Or Not");
        List<Integer> goodsType = settlementInfo.getGoodsInfos()
                .stream().map(GoodsInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodsType = new ArrayList<>();

        settlementInfo.getCouponAndTemplateInfos().forEach(ct -> {
            templateGoodsType.addAll(JSON.parseObject(
                    ct.getTemplate().getRule().getUsage().getGoodsType(),
                    List.class
            ));
        });

        // use multiple coupons
        // templateGoodsType contains more types than goodsType
        return CollectionUtils.isEmpty(CollectionUtils.subtract(
                goodsType, templateGoodsType
        ));
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {
        double goodsSum = retain2Decimals(goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlementInfo, goodsSum);
        // item type does not match the coupon limit
        if (null != probability) {
            log.debug("Amount And Percentage Template Is Not Match To Goods Type!");
            return probability;
        }

        SettlementInfo.CouponAndTemplateInfo amount = null;
        SettlementInfo.CouponAndTemplateInfo percentage = null;

        for (SettlementInfo.CouponAndTemplateInfo ct: settlementInfo.getCouponAndTemplateInfos()) {
            if (CouponCategory.of(ct.getTemplate().getCategory()) == CouponCategory.AMOUNT) {
                amount = ct;
            } else {
                percentage = ct;
            }
        }

        assert null != amount;
        assert null != percentage;

        // if the amount discount coupon and the percentage discount coupon cannot be used together
        // empty the coupon and return the original price of the product
        if (!isTemplateCanShared(amount, percentage)) {
            log.debug("Current Amount And Percentage Coupons Can Not Shared!");
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
            return settlementInfo;
        }

        // if they can be used together
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = new ArrayList<>();
        double amountBase = (double) amount.getTemplate().getRule().getDiscount().getBase();
        double amountQuota = (double) amount.getTemplate().getRule().getDiscount().getQuota();

        // final sum
        double targetSum = goodsSum;

        // calculate amount coupon first
        if (targetSum >= amountBase) {
            targetSum -= amountQuota;
            ctInfos.add(amount);
        }

        // then calculate percentage coupon
        double percentageQuota = (double) percentage.getTemplate().getRule().getDiscount().getQuota();
        targetSum *= percentageQuota * 1.0 / 100;
        ctInfos.add(percentage);

        settlementInfo.setCouponAndTemplateInfos(ctInfos);
        settlementInfo.setCost(retain2Decimals(
                targetSum > minCost() ? targetSum : minCost()
        ));

        log.debug("Use Amount And Percentage Coupon Make Goods Cost From {} To {}",
                goodsSum, settlementInfo.getCost());

        return settlementInfo;
    }

    /**
     * whether the current two coupons can be shared
     * check the TemplateRule weight
     * @param amount
     * @param percentage
     * @return
     */
    @SuppressWarnings("all")
    private boolean isTemplateCanShared(SettlementInfo.CouponAndTemplateInfo amount, SettlementInfo.CouponAndTemplateInfo percentage) {
        // amount CouponTemplate Key
        String amountKey = amount.getTemplate().getKey() + String.format("%04d", amount.getTemplate().getId());
        // percentage CouponTemplate Key
        String percentageKey = percentage.getTemplate().getKey() + String.format("%04d", percentage.getTemplate().getId());

        List<String> allSharedKeysForAmount = new ArrayList<>();
        allSharedKeysForAmount.add(amountKey);
        allSharedKeysForAmount.addAll(JSON.parseObject(
                amount.getTemplate().getRule().getWeight(),
                List.class
        ));

        List<String> allSharedKeysForPercentage = new ArrayList<>();
        allSharedKeysForPercentage.add(percentageKey);
        allSharedKeysForPercentage.addAll(JSON.parseObject(
                percentage.getTemplate().getRule().getWeight(),
                List.class
        ));

        // allSharedKeysForAmount contains all CouponTemplate ID that can be used together with this amount coupon
        // allSharedKeysForPercentage contains all CouponTemplate ID that can be used together with this percentage coupon
        return CollectionUtils.isSubCollection(Arrays.asList(amountKey, percentageKey), allSharedKeysForAmount)
                || CollectionUtils.isSubCollection(Arrays.asList(amountKey, percentageKey), allSharedKeysForPercentage);
    }
}
