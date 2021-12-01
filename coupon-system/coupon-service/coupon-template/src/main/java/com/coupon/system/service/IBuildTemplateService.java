package com.coupon.system.service;

import com.coupon.system.entity.CouponTemplate;
import com.coupon.system.exception.CouponException;
import com.coupon.system.vo.TemplateRequest;

public interface IBuildTemplateService {

    CouponTemplate buildTemplate(TemplateRequest request) throws CouponException;
}
