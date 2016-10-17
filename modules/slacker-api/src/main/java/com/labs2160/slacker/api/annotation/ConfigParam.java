package com.labs2160.slacker.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({}) @Retention(RetentionPolicy.RUNTIME)
public @interface ConfigParam {
    String key();
    String description();

    boolean required() default false;
    String defaultValue() default "";
    String example() default "";
}
