package com.coupon.system.executor;

import com.alibaba.fastjson.JSON;
import com.coupon.system.vo.GoodsInfo;
import com.coupon.system.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rule executor abstracts classes that define common methods
 */
public abstract class AbstractExecutor {

    /**
     * check whether the product type matches the coupon
     * Note:
     * 1. The verification of single category coupons is realized here, and multi-category coupons are overloaded with this method
     * 2. Goods only need to be matched by the type of goods required by a coupon
     * @param settlementInfo
     * @return
     */
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlementInfo) {

        List<Integer> goodsType = settlementInfo.getGoodsInfos()
                .stream().map(GoodsInfo::getType)
                .collect(Collectors.toList());
        List<Integer> templateGoodsType = JSON.parseObject(
                settlementInfo.getCouponAndTemplateInfos().get(0).getTemplate()
                .getRule().getUsage().getGoodsType(),
                List.class
        );

        // check if there is an intersection between goodsType and templateGoodsType
        return CollectionUtils.isNotEmpty(
                CollectionUtils.intersection(goodsType, templateGoodsType)
        );
    }


    /**
     * handle a mismatch between the item type and the coupon limit
     * @param settlementInfo
     * @param goodsSum
     * @return
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(SettlementInfo settlementInfo, double goodsSum) {

        boolean isGoodsTtypeSatisfy = isGoodsTypeSatisfy(settlementInfo);

        // when the item type is not satisfied, directly return the total price, and empty the coupon
        if (!isGoodsTtypeSatisfy) {
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
            return settlementInfo;
        }

        return null;
    }

    /**
     * goods total price
     * @param goodsInfos
     * @return
     */
    protected double goodsCostSum(List<GoodsInfo> goodsInfos) {
        return goodsInfos.stream().mapToDouble(g ->
                g.getPrice() * g.getCount()).sum();
    }


    /**
     * keep two decimal places
     * @param value
     * @return
     */
    protected double retain2Decimals(double value) {
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * minimum payment
     * it is possible that the user will have to pay a price of 0 or negative after using the coupon, which is not reasonable, so set a minimum fee of 0.1
     * @return
     */
    protected double minCost() {
        return 0.1;
    }

}










