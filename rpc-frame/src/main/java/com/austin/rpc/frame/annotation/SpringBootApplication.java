package com.austin.rpc.frame.annotation;

import com.austin.rpc.frame.enums.BootstrapType;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpringBootApplication {

    BootstrapType bootstrapType() default BootstrapType.SERVER;

    String ip() default "127.0.0.1";

    int port() default 8888;

}
