package com.coupon.system.constant;

public class Constant {

    /**
     * kafka
     */
    public static final String TOPIC = "user_coupon_topic";


    /**
     * redis
     */
    public static class RedisPrefix {

        // the key prefix of coupon code
        public static final String COUPON_TEMPLATE = "coupon_template_code_";

        // the key prefixes of all available coupon that the user currently has
        public static final String USER_COUPON_USABLE = "user_coupon_usable_";

        // the key prefixes of all used coupon that the user currently has
        public static final String USER_COUPON_USED = "user_coupon_used_";

        // the key prefixes of all expired coupon that the user currently has
        public static final String USER_COUPON_EXPIRED = "user_coupon_expired_";
    }
}
