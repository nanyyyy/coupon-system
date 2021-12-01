package com.coupon.system.service.impl;

import com.coupon.system.dao.CouponTemplateDao;
import com.coupon.system.entity.CouponTemplate;
import com.coupon.system.exception.CouponException;
import com.coupon.system.service.IAsyncService;
import com.coupon.system.service.IBuildTemplateService;
import com.coupon.system.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {

    @Autowired
    private final CouponTemplateDao couponTemplateDao;
    @Autowired
    private final IAsyncService asyncService;

    public BuildTemplateServiceImpl(CouponTemplateDao couponTemplateDao, IAsyncService asyncService) {
        this.couponTemplateDao = couponTemplateDao;
        this.asyncService = asyncService;
    }

    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {

        if (!request.validate()) {
            throw new CouponException("BuildTemplate Param Is Not Valid!");
        }
        if (null != couponTemplateDao.findByName(request.getName())) {
            throw new CouponException("Exist Same Name Template!");
        }

        // insert a coupon template into the database and return a coupon template with auto generated id
        CouponTemplate couponTemplate = requestToTemplate(request);
        couponTemplate = couponTemplateDao.save(couponTemplate);

        // generate coupon codes asynchronously according to coupon templates
        asyncService.asyncConstructCouponByTemplate(couponTemplate);

        return couponTemplate;
    }

    private CouponTemplate requestToTemplate(TemplateRequest request) {

        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
