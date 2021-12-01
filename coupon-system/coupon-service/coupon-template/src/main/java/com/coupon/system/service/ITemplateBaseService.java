package com.coupon.system.service;

import com.coupon.system.entity.CouponTemplate;
import com.coupon.system.exception.CouponException;
import com.coupon.system.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ITemplateBaseService {
    /**
     * Obtain the coupon template information according to the coupon template ID
     * @param id
     * @return
     * @throws CouponException
     */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    /**
     * Find all available coupon templates
     * @return
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * Get the mapping from template IDS to CouponTemplateSDK
     * @param ids
     * @return
     */
    Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
