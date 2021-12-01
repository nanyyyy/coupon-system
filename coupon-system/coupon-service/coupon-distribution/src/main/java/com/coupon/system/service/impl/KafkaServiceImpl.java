package com.coupon.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.coupon.system.constant.Constant;
import com.coupon.system.constant.CouponStatus;
import com.coupon.system.dao.CouponDao;
import com.coupon.system.entity.Coupon;
import com.coupon.system.service.IKafkaService;
import com.coupon.system.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * The core idea is to synchronize the state changes of the Coupons in Cache to the DB
 */
@Slf4j
@Component
public class KafkaServiceImpl implements IKafkaService {

    @Autowired
    private final CouponDao couponDao;
    public KafkaServiceImpl(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    @Override
    @KafkaListener(topics = {Constant.TOPIC}, groupId = "coupon-system-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {

        // The Optional class is a container object that can be null. The isPresent() method returns true if the value exists, and calling the get() method returns the object.
        // Optional is a container: it can hold values of type T, or just NULL. Optional provides a number of useful methods so that we don't have to explicitly null-check.
        // The introduction of the Optional class is a good solution to null-pointer exceptions.
        // Returns the specified value of the Optional description if it is non-empty, otherwise returns an empty Optional.
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        // Call consumer with the value if it exists, otherwise do nothing
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            CouponKafkaMessage couponInfo = JSON.parseObject(
                    message.toString(),
                    CouponKafkaMessage.class
            );

            log.info("Receive CouponKafakMessage: {}", message.toString());

            CouponStatus status = CouponStatus.of(couponInfo.getStatus());

            switch (status) {
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponInfo, status);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponInfo, status);
                    break;
            }
        }
    }

    /**
     * process used coupons
     * @param kafkaMessage
     * @param status
     */
    private void processUsedCoupons(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        // TODO send message to users
        processCouponByStatus(kafkaMessage, status);
    }

    /**
     * process expired coupons
     * @param kafkaMessage
     * @param status
     */
    private void processExpiredCoupons(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        // TODO send message to users
        processCouponByStatus(kafkaMessage, status);
    }

    private void processCouponByStatus(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        List<Coupon> coupons = couponDao.findAllById(kafkaMessage.getIds());
        if (CollectionUtils.isEmpty(coupons) || coupons.size() != kafkaMessage.getIds().size()) {
            log.error("Can Not Find Right Coupon Info: {}",
                    JSON.toJSONString(kafkaMessage));
            // TODO send emails
            return;
        }

        // replace the original status with the new status
        coupons.forEach(c -> c.setStatus(status));
        log.info("CouponKafakMessage Op Coupon Count: {}",
                couponDao.saveAll(coupons).size());
    }
}















