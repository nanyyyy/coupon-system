package com.coupon.system.executor;

import com.coupon.system.constant.RuleFlag;
import com.coupon.system.vo.SettlementInfo;

/**
 * Coupon template rules processor interface definition
 */
public interface RuleExecutor {
    /**
     * rule type token
     * @return
     */
    RuleFlag ruleConfig();

    /**
     * caculation of coupon rules
     * @param settlementInfo the settlement info chosen by the user
     * @return corrected/checked settlement info
     */
    SettlementInfo computeRule(SettlementInfo settlementInfo);
}
