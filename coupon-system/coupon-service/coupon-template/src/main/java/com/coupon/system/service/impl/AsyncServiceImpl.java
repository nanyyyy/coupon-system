package com.coupon.system.service.impl;

import com.coupon.system.constant.Constant;
import com.coupon.system.dao.CouponTemplateDao;
import com.coupon.system.entity.CouponTemplate;
import com.coupon.system.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.google.common.base.Stopwatch;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {

    @Autowired
    private final StringRedisTemplate redisTemplate;
    @Autowired
    private final CouponTemplateDao couponTemplateDao;

    public AsyncServiceImpl(StringRedisTemplate redisTemplate, CouponTemplateDao couponTemplateDao) {
        this.redisTemplate = redisTemplate;
        this.couponTemplateDao = couponTemplateDao;
    }

    @Async("getAsyncExecutor")
    @Override
    @SuppressWarnings("all")
    public void asyncConstructCouponByTemplate(CouponTemplate couponTemplateWithId) {

        // Timing begins
        Stopwatch watch = Stopwatch.createStarted();

        Set<String> couponCodes = buildCouponCode(couponTemplateWithId);

        // coupon_template_code_id
        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, couponTemplateWithId.getId().toString());
        log.info("Push CouponCode to Redis: {}",
                redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));

        // when redis has coupon codes then the template can be set to be available
        couponTemplateWithId.setAvailable(true);
        couponTemplateDao.save(couponTemplateWithId);

        watch.stop();
        log.info("Construct CouponCode By Template Cost: {}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));

        log.info("CouponTemplate({}) Is Available!", couponTemplateWithId.getId());
    }

    /**
     * Coupon code (18 digits for each coupon)
     * Top four: Product line + Type
     * Middle six: random date (190101)
     * Last eight: 0 to 9 random number
     * @param template
     * @return
     */
    private Set<String> buildCouponCode(CouponTemplate template) {

        Stopwatch watch = Stopwatch.createStarted();
        Set<String> result = new HashSet<>();

        // top four
        String prefix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode();
        // middle six & last eight: to be processed by buildCouponCodeSuffix14
        String date = new SimpleDateFormat("yyMMdd").format(template.getCreateTime());

        for (int i = 0; i < template.getCount(); i ++) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        while (result.size() < template.getCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }

        assert result.size() == template.getCount();

        watch.stop();

        log.info("Build coupon code cost {}ms", watch.elapsed(TimeUnit.MILLISECONDS));

        return result;
    }

    /**
     * build the last 14 digits of the coupon code
     * @param date
     * @return
     */
    private String buildCouponCodeSuffix14(String date) {
        char[] bases = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};

        // middle six
        // char[] ---> List<Character >---> shuffle this list ---> String
        List<Character> chars = date.chars().mapToObj(e -> (char)e).collect(Collectors.toList());
        Collections.shuffle(chars);
        String mid6 = chars.stream().map(Object::toString).collect(Collectors.joining());

        // last eight
        // the first digit shouldn't be 0
        String suffix8 = RandomStringUtils.random(1, bases) + RandomStringUtils.randomNumeric(7);

        return mid6 + suffix8;
    }
}
