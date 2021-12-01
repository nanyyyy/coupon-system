package com.coupon.system.controller;

import com.alibaba.fastjson.JSON;
import com.coupon.system.entity.CouponTemplate;
import com.coupon.system.exception.CouponException;
import com.coupon.system.service.IBuildTemplateService;
import com.coupon.system.service.ITemplateBaseService;
import com.coupon.system.vo.CouponTemplateSDK;
import com.coupon.system.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class CouponTemplateController {

    @Autowired
    private final IBuildTemplateService buildTemplateService;
    @Autowired
    private final ITemplateBaseService templateBaseService;

    public CouponTemplateController(IBuildTemplateService buildTemplateService, ITemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
    }

    /**
     * build CouponTemplate
     * 127.0.0.1:7001/coupon-template/template/build
     * 127.0.0.1:9000/example/coupon-template/template/build
     * @param request from operations
     * @return CouponTemplate
     */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request) throws CouponException {
        log.info("Build Template: {}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    /**
     * build coupon template details
     * 127.0.0.1:7001/coupon-template/template/info?id=1
     * @param id
     * @return
     */
    @GetMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id) throws CouponException {
        log.info("Build Template Info For: {}", id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * Find all available coupon templates
     * 127.0.0.1:7001/coupon-template/template/sdk/all
     * @return
     */
    @GetMapping("template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template.");
        return templateBaseService.findAllUsableTemplate();
    }

    /**
     * Gets the mapping from template IDS to CouponTemplateSDK
     * 127.0.0.1:7001/coupon-template/template/sdk/infos
     * 127.0.0.1:9000/example/coupon-template/template/sdk/infos?ids=1,2
     * @param ids
     * @return
     */
    @GetMapping("template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK (@RequestParam("ids") Collection<Integer> ids){
        log.info("FindIds2TemplateSDK: {}", JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}
















