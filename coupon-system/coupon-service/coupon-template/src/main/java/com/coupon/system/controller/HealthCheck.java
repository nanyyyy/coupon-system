package com.coupon.system.controller;

import com.coupon.system.exception.CouponException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.client.discovery.DiscoveryClient;


@Slf4j
@RestController
public class HealthCheck {

    /** The service discovers the client */
    private final DiscoveryClient client;

    /** A service registration interface that provides a way to get a service ID */
    private final Registration registration;

    public HealthCheck(DiscoveryClient client, Registration registration) {
        this.client = client;
        this.registration = registration;
    }

    /**
     * 127.0.0.1:7001/coupon-template/health
     * 127.0.0.1:9000/example/coupon-template/health Through the gateway, an error is reported because there is no token
     * 127.0.0.1:9000/example/coupon-template/health?token=x will pass with a random token
     * */
    @GetMapping("/health")
    public String health() {
        log.debug("view health api");
        return "CouponTemplate Is Ok!";
    }

    /**
     * 127.0.0.1:7001/coupon-template/exception
     * 127.0.0.1:9000/example/coupon-template/exception
     * */
    @GetMapping("/exception")
    public String exception() throws CouponException {
        log.debug("view exception api");
        throw new CouponException("CouponTemplate Has Some Problem");
    }

    /**
     * Get the microservice meta information on Eureka Server
     * 127.0.0.1:7001/coupon-template/info
     * 127.0.0.1:9000/example/coupon-template/info
     */
    @GetMapping("/info")
    public List<Map<String, Object>> info() {
        // It takes about two minutes to obtain the registration information
        List<ServiceInstance> instances = client.getInstances(registration.getServiceId());

        List<Map<String, Object>> result = new ArrayList<>(instances.size());

        instances.forEach(serviceInstance -> {
            Map<String, Object> info = new HashMap<>();
            info.put("serviceId", serviceInstance.getServiceId());
            info.put("instanceId", serviceInstance.getInstanceId());
            info.put("port", serviceInstance.getPort());

            result.add(info);
        });

        return result;
    }
}
