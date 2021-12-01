package com.coupon.system.advice;

import com.coupon.system.annotation.IgnoreResponseAdvice;
import com.coupon.system.vo.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 *  Advanced processing of common response
 */
@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

    /**
     * determine whether the response needs to be processed
     * @param methodParameter represents the definition of Controller
     * @param aClass
     * @return
     */
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        // if the class is marked the @ignoreresponseAdvice annotation, no action is required
        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }

        // if the method is marked the @ignoreresponseAdvice annotation, no action is required
        if (methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }

        // to process the response, execute the beforeBodyWrite method
        return true;
    }

    /**
     *
     * @param object Controller's return object, and the o should be post-processing
     * @param methodParameter represents the definition of Controller
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object object, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        // define the final return object
        CommonResponse<Object> response = new CommonResponse<>(0, "");

        // if object == null, we don't need to add data to response
        if (null == object) {
            return response;

            // if the object is already a CommonResponse, we don't need to process it
        }else if (object instanceof CommonResponse) {
            response = (CommonResponse<Object>) object;

            // otherwise, set the object as the data of response
        }else {
            response.setData(object);
        }

        return response;
    }



}
