package com.coupon.system.feign.hystrix;

import com.coupon.system.exception.CouponException;
import com.coupon.system.feign.SettlementClient;
import com.coupon.system.vo.CommonResponse;
import com.coupon.system.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {
    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlementInfo) throws CouponException {
        log.error("[eureka-client-coupon-settlement] computeRule request error");

        settlementInfo.setEmploy(false);
        settlementInfo.setCost(-1.0);

        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-settlement] request error",
                settlementInfo
        );

    }
}
