package com.coupon.system.vo;

import com.coupon.system.constant.CouponCategory;
import com.coupon.system.constant.DistributeTarget;
import com.coupon.system.constant.ProductLine;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {

    private String name;
    private String logo;
    private String desc;
    private String category;
    private Integer productLine;
    private Integer count;
    private Long userId;
    private Integer target;
    private TemplateRule rule;

    public boolean validate() {
        boolean stringValid = StringUtils.isNotEmpty(name)
                && StringUtils.isNotEmpty(logo)
                && StringUtils.isNotEmpty(desc);
        boolean enumValid = null != CouponCategory.of(category)
                && null != ProductLine.of(productLine)
                && null != DistributeTarget.of(target);
        boolean numValid = count > 0 && userId > 0;

        return stringValid && enumValid && numValid;
    }

}
