package com.coupon.system.executor;

import com.coupon.system.constant.CouponCategory;
import com.coupon.system.constant.RuleFlag;
import com.coupon.system.exception.CouponException;
import com.coupon.system.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coupon Settlement Rule Execution Manager
 * Settle an Executor based on a user's request
 * BeanPostProcessor: Bean post-processor
 */

@Slf4j
@Component
@SuppressWarnings("all")
public class ExecuteManager implements BeanPostProcessor {

    /** rule executor mapping */
    private static Map<RuleFlag, RuleExecutor> executorIndex = new HashMap<>(RuleFlag.values().length);

    /**
     * entry of coupon settlement rule caculation
     * note: make sure that the number of coupons passed in is >= 1 (this judgment is handled by the business side)
     * @param settlementInfo
     * @return
     * @throws CouponException
     */
    public SettlementInfo computeRule(SettlementInfo settlementInfo) throws CouponException {

        SettlementInfo result = null;

        // one coupon
        if (settlementInfo.getCouponAndTemplateInfos().size() == 1) {
            // get coupon's type
            CouponCategory category = CouponCategory.of(
                    settlementInfo.getCouponAndTemplateInfos().get(0).getTemplate().getCategory()
            );

            switch (category) {
                case AMOUNT:
                    result = executorIndex.get(RuleFlag.AMOUNT).computeRule(settlementInfo);
                    break;
                case PERCENTAGE:
                    result = executorIndex.get(RuleFlag.PERCENTAGE).computeRule(settlementInfo);
                    break;
                case BOGO:
                    result = executorIndex.get(RuleFlag.BOGO).computeRule(settlementInfo);
                    break;
            }
        }else {
            // more than one coupon
            List<CouponCategory> categories = new ArrayList<>(settlementInfo.getCouponAndTemplateInfos().size());
            settlementInfo.getCouponAndTemplateInfos().forEach(ct ->
                    categories.add(CouponCategory.of(ct.getTemplate().getCategory())));
            if (categories.size() != 2) {
                throw new CouponException("Not Support For More Template Category");
            }else {
                if (categories.contains(CouponCategory.AMOUNT) && categories.contains(CouponCategory.PERCENTAGE)) {
                    result = executorIndex.get(RuleFlag.AMOUNT_PERCENTAGE).computeRule(settlementInfo);
                }else {
                    throw new CouponException("Not Support For Other Template Category");
                }
            }
        }
        return result;
    }

    /**
     * execute before bean initialization (before)
     * initialize the executorIndex
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (!(bean instanceof RuleExecutor)) {
            return bean;
        }

        RuleExecutor executor = (RuleExecutor) bean;
        RuleFlag ruleFlag = executor.ruleConfig();

        if (executorIndex.containsKey(ruleFlag)) {
            throw new IllegalStateException("There is already an executor for rule flag: " + ruleFlag);
        }

        log.info("Load executor {} for rule falg {}.",
                executor.getClass(), ruleFlag);
        executorIndex.put(ruleFlag, executor);

        return null;
    }

    /**
     * execute after bean initialization (after)
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException{
        return bean;
    }
}
















