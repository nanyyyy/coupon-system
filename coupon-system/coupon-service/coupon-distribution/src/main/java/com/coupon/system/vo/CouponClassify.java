package com.coupon.system.vo;


import com.coupon.system.constant.CouponStatus;
import com.coupon.system.constant.PeriodType;
import com.coupon.system.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import java.util.List;

/**
 * User coupons are classified according to coupon status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassify {

    private List<Coupon> usable;
    private List<Coupon> used;
    private List<Coupon> expired;

    public static CouponClassify classify(List<Coupon> coupons) {

        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());

        coupons.forEach(c -> {
            // check if the coupon is expired
            // do not check the statue of coupons actively, only update statue when query happens
            // so an update action is required
            boolean isTimeExpire;
            long curTime = new Date().getTime();

            if (c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(
                    PeriodType.REGULAR.getCode()
            )) {
                // period type is regular
                isTimeExpire = c.getTemplateSDK().getRule().getExpiration().getDeadline() <= curTime;
            } else {
                // period type is shift
                isTimeExpire = DateUtils.addDays(
                        c.getAssignTime(), c.getTemplateSDK().getRule().getExpiration().getPeriod()).getTime() <= curTime;
            }

            if (c.getStatus() == CouponStatus.USED) {
                used.add(c);
            }else if (c.getStatus() == CouponStatus.EXPIRED || isTimeExpire) {
                expired.add(c);
            }else {
                usable.add(c);
            }
        });

        return new CouponClassify(usable, used, expired);
    }
}
