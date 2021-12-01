package com.coupon.system.dao;

import com.coupon.system.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponTemplateDao extends JpaRepository<CouponTemplate, Integer> {

    CouponTemplate findByName(String templateName);

    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available, Boolean expired);

    List<CouponTemplate> findAllByExpired(Boolean expired);
}
