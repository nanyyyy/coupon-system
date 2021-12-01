package com.coupon.system.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Token Check Filter
 */
@Slf4j
@Component
public class TokenFilter extends AbstractPreZuulFilter {

    @Override
    protected Object cRun() {
        HttpServletRequest httpServletRequest = context.getRequest();
        // httpServletRequest.getMethod() can be "POST" or "GET" etc.
        log.info(String.format("%s request to %s",
                httpServletRequest.getMethod(), httpServletRequest.getRequestURL().toString()));
        Object token = httpServletRequest.getParameter("token");
        if (null == token) {
            log.error("error: token is empty");
            return fail(401, "error: token is empty");
        }
        return success();
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}
