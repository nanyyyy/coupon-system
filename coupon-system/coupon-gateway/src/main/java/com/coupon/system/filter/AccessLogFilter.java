package com.coupon.system.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Access Log Filter
 */
@Slf4j
@Component
public class AccessLogFilter extends AbstractPostZuulFilter{
    @Override
    protected Object cRun() {
        HttpServletRequest httpServletRequest = context.getRequest();

        // get the time stamp from PreRequestFilter
        Long startTime = (Long) context.get("startTime");
        String uri = httpServletRequest.getRequestURI();
        Long duration = System.currentTimeMillis() - startTime;
        log.info("uri: {}, duration: {}", uri, duration);

        return success();
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }
}
