package com.coupon.system.advice;

import com.coupon.system.exception.CouponException;
import com.coupon.system.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * Global Exception Handler
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {


    /**
     * handle the exception uniformly
     * @param req the request comes from the client
     * @param ex
     * @return
     */
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(HttpServletRequest req, CouponException ex) {
        CommonResponse<String> response = new CommonResponse<>(-1, "business error");
        response.setData(ex.getMessage());
        return response;
    }

}
