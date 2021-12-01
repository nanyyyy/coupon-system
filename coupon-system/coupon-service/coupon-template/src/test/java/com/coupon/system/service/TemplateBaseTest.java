package com.coupon.system.service;

import com.alibaba.fastjson.JSON;
import com.coupon.system.exception.CouponException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Arrays;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateBaseTest {

    @Autowired
    private ITemplateBaseService templateBaseService;

    @Test
    public void testBuildTemplateInfo() throws CouponException {
        System.out.println(JSON.toJSONString(
                templateBaseService.buildTemplateInfo(1)
        ));
        System.out.println(JSON.toJSONString(
                templateBaseService.buildTemplateInfo(2)
        ));
    }

    @Test
    public void testFindAllUsableTemplate() {
        System.out.println(JSON.toJSONString(
                templateBaseService.findAllUsableTemplate()
        ));
    }

    @Test
    public void testFindId2TemplateSDK() {
        System.out.println(JSON.toJSONString(
                templateBaseService.findIds2TemplateSDK(Arrays.asList(1, 2, 3))
        ));
    }
}
