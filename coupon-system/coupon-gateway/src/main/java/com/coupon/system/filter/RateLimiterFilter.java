package com.coupon.system.filter;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Rate Limit Filter
 */
@Slf4j
@Component
public class RateLimiterFilter extends AbstractPreZuulFilter {

    // produce 2 tokens per second
    RateLimiter rateLimiter = RateLimiter.create(2.0);


    @Override
    protected Object cRun() {
        // The HttpServletRequest object represents the client's request
        HttpServletRequest httpServletRequest = context.getRequest();

        // get token and consume it
        if (rateLimiter.tryAcquire()) {
            log.info("get rate token success");
            return success();
        }else {
            log.error("rate limit: {}", httpServletRequest.getRequestURI());
            return fail(402, "erroro: rate limit");
        }
    }

    @Override
    public int filterOrder() {
        return 2;
    }
}
