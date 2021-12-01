package com.coupon.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * fake goods info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsInfo {
    /** goods type */
    private Integer type;
    /** goods price */
    private Double price;
    /** goods quantity */
    private Integer count;
}
