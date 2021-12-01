package com.coupon.system.entity;

import com.coupon.system.constant.CouponCategory;
import com.coupon.system.constant.DistributeTarget;
import com.coupon.system.constant.ProductLine;
import com.coupon.system.converter.CouponCategoryConverter;
import com.coupon.system.converter.DistributeTargetConverter;
import com.coupon.system.converter.ProductLineConverter;
import com.coupon.system.converter.RuleConverter;
import com.coupon.system.serialization.CouponTemplateSerialize;
import com.coupon.system.vo.TemplateRequest;
import com.coupon.system.vo.TemplateRule;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon_template")
@JsonSerialize(using = CouponTemplateSerialize.class)
public class CouponTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @Column(name = "expired", nullable = false)
    private Boolean expired;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "logo", nullable = false)
    private String logo;

    @Column(name = "intro", nullable = false)
    private String desc;

    @Column(name = "category", nullable = false)
    @Convert(converter = CouponCategoryConverter.class)
    private CouponCategory category;

    @Column(name = "product_line", nullable = false)
    @Convert(converter = ProductLineConverter.class)
    private ProductLine productLine;

    @Column(name = "coupon_count", nullable = false)
    private Integer count;

    @CreatedDate
    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "template_key", nullable = false)
    private String key;

    @Column(name = "target", nullable = false)
    @Convert(converter = DistributeTargetConverter.class)
    private DistributeTarget target;

    @Column(name = "rule", nullable = false)
    @Convert(converter = RuleConverter.class)
    private TemplateRule rule;


    public CouponTemplate(String name, String logo, String desc, String category,
                          Integer productLine, Integer count, Long userId,
                          Integer target, TemplateRule rule) {

        this.available = false;
        this.expired = false;
        this.name = name;
        this.logo = logo;
        this.desc = desc;
        this.category = CouponCategory.of(category);
        this.productLine = ProductLine.of(productLine);
        this.count = count;
        this.userId = userId;
        // coupon template unique code = 4(productLine + category) + 8(date: 20190101) + id(expand to 4 digits)
        this.key = productLine.toString() + category +
                new SimpleDateFormat("yyyyMMdd").format(new Date());
        this.target = DistributeTarget.of(target);
        this.rule = rule;
    }
}
