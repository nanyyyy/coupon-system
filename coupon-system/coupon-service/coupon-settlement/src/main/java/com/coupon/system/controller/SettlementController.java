package com.coupon.system.controller;

import com.alibaba.fastjson.JSON;
import com.coupon.system.exception.CouponException;
import com.coupon.system.executor.ExecuteManager;
import com.coupon.system.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SettlementController {

    /** settlement rule execute manager */
    @Autowired
    private final ExecuteManager executeManager;
    public SettlementController(ExecuteManager executeManager) {
        this.executeManager = executeManager;
    }

    /**
     * coupon settlement
     * @param settlementInfo
     * @return
     * @throws CouponException
     */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlementInfo) throws CouponException {
        log.info("Settlement: {}", JSON.toJSONString(settlementInfo));
        return executeManager.computeRule(settlementInfo);
    }
}
