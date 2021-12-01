package com.coupon.system.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IKafkaService {
    void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record);
}
