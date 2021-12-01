package com.coupon.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponKafkaMessage {
    /** coupon status */
    private Integer status;
    /** coupon primary key */
    private List<Integer> ids;
}
