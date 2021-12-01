package com.coupon.system.schedule;

import com.coupon.system.dao.CouponTemplateDao;
import com.coupon.system.entity.CouponTemplate;
import com.coupon.system.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Periodically clean up expired coupon templates
 */
@Slf4j
@Component
public class ScheduledTask {

    @Autowired
    private final CouponTemplateDao couponTemplateDao;

    public ScheduledTask(CouponTemplateDao couponTemplateDao) {
        this.couponTemplateDao = couponTemplateDao;
    }

    /**
     * Off line expired coupon templates
     * the 1000 means 1 sec, so 60 * 60 * 1000 means 1 hour
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void offlineCouponTemplate() {
        log.info("Start To Expire CouponTemplate");

        // find not expired coupon templates
        List<CouponTemplate> couponTemplates = couponTemplateDao.findAllByExpired(false);
        if (CollectionUtils.isNotEmpty(couponTemplates)) {
            log.info("Done To Expire CouponTemplate.");
            return;
        }

        Date cur = new Date();
        List<CouponTemplate> expiredTemplates = new ArrayList<>(couponTemplates.size());
        couponTemplates.forEach(couponTemplate -> {
            TemplateRule templateRule = couponTemplate.getRule();
            if (templateRule.getExpiration().getDeadline() < cur.getTime()) {
                couponTemplate.setExpired(true);
                expiredTemplates.add(couponTemplate);
            }
        });

        if (CollectionUtils.isNotEmpty(expiredTemplates)) {
            log.info("Expired CouponTemplate Num: {}",
                    couponTemplateDao.saveAll(expiredTemplates));
        }

        log.info("Done To Expire CouponTemplate.");
    }

}
