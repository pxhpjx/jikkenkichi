package org.nozomi.jikkenkichi.machikouba.fliter;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@RequestMapping
/**
 * a simple Frankenstein
 * as a mark for PathMatchConfigurer in this project
 */
public @interface RestMappingController {
    @AliasFor(annotation = RequestMapping.class)
    String[] path() default {};

    @AliasFor(annotation = RequestMapping.class)
    RequestMethod[] method() default {};
}
