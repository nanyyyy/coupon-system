package com.coupon.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.coupon.system.constant.Constant;
import com.coupon.system.constant.CouponStatus;
import com.coupon.system.dao.CouponDao;
import com.coupon.system.entity.Coupon;
import com.coupon.system.exception.CouponException;
import com.coupon.system.feign.SettlementClient;
import com.coupon.system.feign.TemplateClient;
import com.coupon.system.service.IRedisService;
import com.coupon.system.service.IUserService;
import com.coupon.system.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The state of all operations is stored in Redis, and messages are passed to MySQL via Kafka
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final CouponDao couponDao;
    private final IRedisService redisService;
    private final TemplateClient templateClient;
    private final SettlementClient settlementClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public UserServiceImpl(CouponDao couponDao, IRedisService redisService, TemplateClient templateClient, SettlementClient settlementClient, KafkaTemplate<String, String> kafkaTemplate) {
        this.couponDao = couponDao;
        this.redisService = redisService;
        this.templateClient = templateClient;
        this.settlementClient = settlementClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * find coupon by userId + couponStatus
     * @param userId
     * @param status
     * @return
     * @throws CouponException
     */
    @Override
    public List<Coupon> findCouponByStatus(Long userId, Integer status) throws CouponException {
        List<Coupon> curCached = redisService.getCachedCoupons(userId, status);
        List<Coupon> preTarget;

        // find coupons in cache
        if (CollectionUtils.isNotEmpty(curCached)) {
            log.debug("Coupon Cache Is Not Empty: {}, {}", userId, status);
            preTarget = curCached;
        }else {
            // find coupons in db

            log.debug("Coupon Cache Is Empty, Get Coupon From DB: {}, {}", userId, status);
            List<Coupon> dbCoupons = couponDao.findAllByUserIdAndStatus(userId, CouponStatus.of(status));
            // if there is no record in db, just return directly, and an empty coupon list has already been saved in cache
            if (CollectionUtils.isEmpty(dbCoupons)) {
                log.debug("Current User Do Not Have Coupon: {}, {}", userId, status);
                return dbCoupons;
            }

            // fill the templateSDK field for dbCoupons
            Map<Integer, CouponTemplateSDK> id2TemplateSDK = templateClient.findIds2TemplateSdk(
                    dbCoupons.stream().map(Coupon::getTemplateId).collect(Collectors.toList())
            ).getData();
            dbCoupons.forEach(
                    dc -> dc.setTemplateSDK(id2TemplateSDK.get(dc.getTemplateId()))
            );
            // there are records in db
            preTarget = dbCoupons;
            // write the records in cache
            redisService.addCouponToCache(userId, preTarget, status);
        }

        // delete the unavailable coupon
        preTarget = preTarget.stream().filter(c -> c.getId() != -1).collect(Collectors.toList());
        // if you are currently getting available coupons, you also need to deal with expired coupons
        if (CouponStatus.of(status) == CouponStatus.USABLE) {
            CouponClassify classify = CouponClassify.classify(preTarget);
            // check for any EXPIRED coupons are USABLE now and update them to the cache and db
            if (CollectionUtils.isNotEmpty(classify.getExpired())) {
                log.info("Add Expired Coupons To Cache From FindCouponByStatus: {}, {}", userId, status);
                // update cache directly
                redisService.addCouponToCache(userId, classify.getExpired(), CouponStatus.EXPIRED.getCode());
                // update db by kafka
                kafkaTemplate.send(
                        Constant.TOPIC,
                        JSON.toJSONString(new CouponKafkaMessage(
                                CouponStatus.EXPIRED.getCode(),
                                classify.getExpired().stream().map(Coupon::getId).collect(Collectors.toList())
                        ))
                );
            }
            return classify.getUsable();
        }

        return preTarget;
    }

    /**
     * find currently available coupon templates based on userId
     * @param userId
     * @return
     * @throws CouponException
     */
    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {

        long curTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKS = templateClient.findAllUsableTemplate().getData();

        log.debug("Find All Template (From TemplateClient) Count: {}", templateSDKS.size());

        // filter expired coupon templates
        templateSDKS = templateSDKS.stream().filter(t ->
                t.getRule().getExpiration().getDeadline() > curTime).collect(Collectors.toList());

        log.info("Find Usable Template Count: {}", templateSDKS.size());

        // key: TemplateId
        // value: left - Template limitation, right - coupon template
        // convert the coupon template templateSDKS to Map form, containing its information
        Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template = new HashMap<>(templateSDKS.size());
        templateSDKS.forEach(t ->
                limit2Template.put(
                        t.getId(),
                        Pair.of(t.getRule().getLimitation(), t)
                ));

        List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());
        List<Coupon> userUsableCoupons = findCouponByStatus(userId, CouponStatus.USABLE.getCode());

        log.debug("Current User has Usable Coupons: {}, {}", userId, userUsableCoupons.size());

        // key: TemplateId
        // do "group by" according to the TemplateId
        // convert userUsableCoupons to Map form, containing its information
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons
                .stream().collect(Collectors.groupingBy(Coupon::getTemplateId));

        // check if the user can get the coupon template according to the template rule
        limit2Template.forEach((k, v) -> {
            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();
            if (templateId2Coupons.containsKey(k) && templateId2Coupons.get(k).size() >= limitation) {
                return;
            }
            result.add(templateSDK);
        });

        return result;
    }

    /**
     * Users get corresponding coupons according to the template
     *
     * 1. Get coupon from TemplateClient and check if it's expired
     * 2. Check whether the user can get it or not according to limitation
     * 3. save to db
     * 4. Populate the CouponTemplateSDK
     * 5. save to cache
     * @param request
     * @return
     * @throws CouponException
     */
    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {

        // get coupon from TemplateClient
        Integer templateId = request.getTemplateSDK().getId();
        Map<Integer, CouponTemplateSDK>  id2Template = templateClient.findIds2TemplateSdk(Collections.singletonList(templateId)).getData();

        if (id2Template.size() == 0) {
            log.error("Can Not Acquire Template From TemplateClient: {}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can Not Acquire Template From TemplateClient");
        }

        // check whether the user can get the coupon
        List<Coupon> userUsableCoupons = findCouponByStatus(
                request.getUserId(), CouponStatus.USABLE.getCode()
        );
        Map<Integer, List<Coupon>> templateId2Coupons =
                userUsableCoupons.stream().collect(Collectors.groupingBy(Coupon::getTemplateId));
        if (templateId2Coupons.containsKey(templateId)
                // check if it's expired
                && templateId2Coupons.get(templateId).size() >= request.getTemplateSDK().getRule().getLimitation()) {
            log.error("Exceed Template Assign Limitation: {}", templateId);
            throw new CouponException("Exceed Template Assign Limitation");
        }

        // try to get coupon code
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(templateId);
        if (StringUtils.isEmpty(couponCode)) {
            log.error("Can Not Acquire Coupon Code: {}", templateId);
            throw new CouponException("Can Not Acquire Coupon Code");
        }

        Coupon newCoupon = new Coupon(templateId, request.getUserId(), couponCode, CouponStatus.USABLE);
        newCoupon = couponDao.save(newCoupon);

        // the CouponTemplateSDK must be filled before putting it in the cache
        newCoupon.setTemplateSDK(request.getTemplateSDK());

        // add to the cache
        redisService.addCouponToCache(
                request.getUserId(),
                Collections.singletonList(newCoupon),
                CouponStatus.USABLE.getCode()
        );

        return newCoupon;
    }


    /**
     * Note here that the Settlement service needs to handle rules.
     * The current service only handles business process (verification process)
     * @param info
     * @return
     * @throws CouponException
     */
    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {

        // return product price directly when there is no coupon
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = info.getCouponAndTemplateInfos();
        if (CollectionUtils.isEmpty(ctInfos)) {
            log.info("Empty Coupons For Settle.");
            double goodsSum = 0.0;
            for (GoodsInfo gi: info.getGoodsInfos()) {
                goodsSum += gi.getCount() * gi.getPrice();
            }
            // no need to revise the other features of SettlementInfo because there is no coupon to be used
            info.setCost(retain2Decimals(goodsSum));
        }

        // check if the coupon belongs to user himself/herself
        // find all USABLE coupons from cache and db
        List<Coupon> coupons = findCouponByStatus(info.getUserId(), CouponStatus.USABLE.getCode());
        // Function.identity() can get coupon itself
        Map<Integer, Coupon> id2Coupon = coupons.stream().collect(Collectors.toMap(
                Coupon::getId,
                Function.identity()
        ));
        List<Integer> usableCoupons = (List<Integer>) id2Coupon.keySet();

        // coupons in SettlementInfo
        List<Integer> infoCoupon = ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId)
                .collect(Collectors.toList());

        // if there is no USABLE coupon
        // if the coupons passed in are not part of the subset of coupons available to the user
        if (MapUtils.isEmpty(id2Coupon)
                || !CollectionUtils.isSubCollection(infoCoupon, usableCoupons)) {
            log.info("{}", usableCoupons);
            log.info("{}", infoCoupon);
            log.error("User Coupon Has Some Problem, It Is Not SubCollection Of Coupons!");
            throw new CouponException("User Coupon Has Some Problem, It Is Not SubCollection Of Coupons!");
        }

        log.debug("Current Settlement Coupons Is User's: {}", ctInfos.size());

        List<Coupon> settleCoupons = new ArrayList<>(ctInfos.size());
        ctInfos.forEach(ci -> settleCoupons.add(id2Coupon.get(ci)));

        // get settlement info from settlement service
        SettlementInfo processInfo = settlementClient.computeRule(info).getData();
        if (processInfo.getEmploy() && CollectionUtils.isNotEmpty(processInfo.getCouponAndTemplateInfos())) {
            log.info("Settle User Coupon: {}, {}", info.getUserId(), JSON.toJSONString(settleCoupons));
            // update the cache
            redisService.addCouponToCache(
                    info.getUserId(),
                    settleCoupons,
                    CouponStatus.USED.getCode()
            );
            // update the db
            kafkaTemplate.send(
                    Constant.TOPIC,
                    JSON.toJSONString(new CouponKafkaMessage(
                      CouponStatus.USED.getCode(),
                      settleCoupons.stream().map(Coupon::getId).collect(Collectors.toList())
                    ))
            );
        }

        return processInfo;

    }


    /**
     * Keep two decimal
     * @param value
     * @return
     */
    private double retain2Decimals(double value) {
        // BigDecimal.ROUND_HALF_UP represent rounding
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}











