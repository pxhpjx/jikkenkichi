package org.nozomi.jikkenkichi.seiran;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/**
 * "Spring Cloud + feign"-like framework's contract client
 * As a single-function package,
 * the spring.factories is used directly so that it takes effect directly and automatically at startup
 */
public @interface SeiranClient {
    /**
     * target server [local.config.app-name]
     *
     * @return
     */
    String value();


    String basePath() default "";

    //in fact,we won't use GET anytime
    //just make it easy in demo
    RequestMethod requestMethod() default RequestMethod.POST;

}
