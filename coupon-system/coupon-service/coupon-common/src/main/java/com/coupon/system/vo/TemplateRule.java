package com.coupon.system.vo;

import com.coupon.system.constant.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRule {

    private Expiration expiration;
    private Discount discount;
    private Usage usage;
    /**
     * each person can receive a maximum number of coupons
     */
    private Integer limitation;
    /**
     * can be used with other category of coupons, the same category of cannot be used together
     */
    private String weight;

    public boolean validate() {
        return expiration.validate() && discount.validate() && limitation > 0
                && usage.validate() && StringUtils.isNotEmpty(weight);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Expiration {
        // The validity period rule corresponds to the code field of PeriodType
        private Integer period;
        // Interval of validity is only valid for variable validity
        private Integer gap;
        private Long deadline;

        boolean validate() {
            return null != PeriodType.of(period) && gap > 0 && deadline > 0;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Discount {

        private Integer quota;
        private Integer base;

        boolean validate() {
            return quota > 0 && base > 0;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {

        private String state;
        private String city;
        // Type of goods, List [Entertainment, Fresh, Home, whole category]
        private String goodsType;

        boolean validate() {
            return StringUtils.isNotEmpty(state)
                    && StringUtils.isNotEmpty(city)
                    && StringUtils.isNotEmpty(goodsType);
        }
    }
}
