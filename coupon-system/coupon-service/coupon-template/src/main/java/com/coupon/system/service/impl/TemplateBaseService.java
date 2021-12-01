package com.coupon.system.service.impl;

import com.coupon.system.dao.CouponTemplateDao;
import com.coupon.system.entity.CouponTemplate;
import com.coupon.system.exception.CouponException;
import com.coupon.system.service.ITemplateBaseService;
import com.coupon.system.vo.CouponTemplateSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TemplateBaseService implements ITemplateBaseService {

    @Autowired
    private final CouponTemplateDao couponTemplateDao;

    public TemplateBaseService(CouponTemplateDao couponTemplateDao) {
        this.couponTemplateDao = couponTemplateDao;
    }

    /**
     * Obtain the coupon template information based on the coupon template ID
     * @param id
     * @return
     * @throws CouponException
     */
    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {
        Optional<CouponTemplate> couponTemplate = couponTemplateDao.findById(id);
        if (!couponTemplate.isPresent()) {
            throw new CouponException("Template Is Not Exist: " + id);
        }
        return couponTemplate.get();
    }

    /**
     * Find all available coupon templates
     * @return
     */
    @Override
    public List<CouponTemplateSDK> findAllUsableTemplate() {

        List<CouponTemplate> couponTemplates = couponTemplateDao.findAllByAvailableAndExpired(true, false);
        return couponTemplates.stream().map(this::template2TemplateSDK).collect(Collectors.toList());
    }

    /**
     * Gets the mapping from template IDS to CouponTemplateSDK
     * @param ids
     * @return¥
     */
    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {

        List<CouponTemplate> couponTemplates = couponTemplateDao.findAllById(ids);
        return couponTemplates.stream().map(this::template2TemplateSDK).
                collect(Collectors.toMap(CouponTemplateSDK::getId, Function.identity()));
    }

    private CouponTemplateSDK template2TemplateSDK(CouponTemplate couponTemplate) {
        return new CouponTemplateSDK(
                couponTemplate.getId(),
                couponTemplate.getName(),
                couponTemplate.getLogo(),
                couponTemplate.getDesc(),
                couponTemplate.getCategory().getCode(),
                couponTemplate.getProductLine().getCode(),
                // not a full key
                couponTemplate.getKey(),
                couponTemplate.getTarget().getCode(),
                couponTemplate.getRule()
        );
    }
}
