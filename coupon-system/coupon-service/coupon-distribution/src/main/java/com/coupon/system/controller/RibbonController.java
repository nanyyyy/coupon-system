package com.coupon.system.controller;

import com.coupon.system.annotation.IgnoreResponseAdvice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class RibbonController {

    @Autowired
    private final RestTemplate restTemplate;
    public RibbonController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Invoke template service through the Ribbon component
     * @return
     */
    @GetMapping("/info")
    @IgnoreResponseAdvice
    public TemplateInfo getTemplateInfo() {
        String infoUrl = "http://eureka-client-coupon-template/coupon-template/info";
        return restTemplate.getForEntity(infoUrl, TemplateInfo.class).getBody();
    }

    /**
     * the meta data of template service
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TemplateInfo {
        private Integer code;
        private String message;
        private List<Map<String, Object>> data;
    }
}
