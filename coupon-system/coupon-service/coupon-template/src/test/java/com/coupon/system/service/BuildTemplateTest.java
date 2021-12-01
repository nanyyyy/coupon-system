package com.coupon.system.service;

import com.alibaba.fastjson.JSON;
import com.coupon.system.constant.CouponCategory;
import com.coupon.system.constant.DistributeTarget;
import com.coupon.system.constant.PeriodType;
import com.coupon.system.constant.ProductLine;
import com.coupon.system.dao.CouponTemplateDao;
import com.coupon.system.entity.CouponTemplate;
import com.coupon.system.exception.CouponException;
import com.coupon.system.service.impl.BuildTemplateServiceImpl;
import com.coupon.system.vo.TemplateRequest;
import com.coupon.system.vo.TemplateRule;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

//@RunWith(PowerMockRunner.class)
//@PowerMockRunnerDelegate(SpringRunner.class)
//@PrepareForTest(BuildTemplateServiceImpl.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class BuildTemplateTest {

    @Autowired
    private IBuildTemplateService buildTemplateService;

    @Test
    public void testBuildTemplate() throws Exception {

        /*CouponTemplateDao couponTemplateDao = PowerMockito.mock(CouponTemplateDao.class);
        IAsyncService asyncService = PowerMockito.mock(IAsyncService.class);
        BuildTemplateServiceImpl buildTemplateService = new BuildTemplateServiceImpl(couponTemplateDao, asyncService);

        TemplateRequest templateRequest = new TemplateRequest();
        templateRequest.setCategory("001");
        templateRequest.setProductLine(1);
        templateRequest.setTarget(1);
        templateRequest.setCount(10);
        templateRequest.setUserId(2L);
        try {
            buildTemplateService.buildTemplate(templateRequest);
        }catch (CouponException couponException) {
            assert(couponException.getMessage().equals("BuildTemplate Param Is Not Valid!"));
        }
        templateRequest.setName("coupon1");
        templateRequest.setLogo("logo1");
        templateRequest.setDesc("this is a coupon");

        CouponTemplate couponTemplate = new CouponTemplate();
        couponTemplate.setId(1);
        couponTemplate.setName("coupon1");

        PowerMockito.doNothing().when(asyncService).asyncConstructCouponByTemplate(couponTemplate);
        PowerMockito.when(buildTemplateService.buildTemplate(templateRequest)).thenReturn(couponTemplate);

        CouponTemplate couponTemplate1 = buildTemplateService.buildTemplate(templateRequest);
        assertTrue(null != couponTemplate1.getId());

        try {
            PowerMockito.when(couponTemplateDao.findByName(templateRequest.getName())).thenReturn(couponTemplate);
            buildTemplateService.buildTemplate(templateRequest);
        }catch (CouponException couponException) {
            assert (couponException.getMessage().equals("Exist Same Name Template!"));
        }*/

        System.out.println(JSON.toJSONString(
                buildTemplateService.buildTemplate(fakeTemplateRequest())
        ));
        // Thread sleep time, which ensures that the asynchronous thread has enough time to execute before the main thread ends
        Thread.sleep(5000);
    }

    /**
     * fake template request
     * @return
     */
    private TemplateRequest fakeTemplateRequest() {
        TemplateRequest request = new TemplateRequest();
        request.setName("coupon template-" + new Date().getTime());
        request.setLogo("http://www.coupon.com");
        request.setDesc("this is a coupon template");
        request.setCategory(CouponCategory.PERCENTAGE.getCode());
        request.setProductLine(ProductLine.CLOTHING.getCode());
        request.setCount(10000);
        request.setUserId(10001L);  // fake user id
        request.setTarget(DistributeTarget.SINGLE.getCode());

        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
                PeriodType.SHIFT.getCode(),
                1, DateUtils.addDays(new Date(), 60).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(5, 1));
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage(
                "Queensland", "Brisbane",
                JSON.toJSONString(Arrays.asList("entertainment", "home"))
        ));
        rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));

        request.setRule(rule);

        return request;
    }
}
