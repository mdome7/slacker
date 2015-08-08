package com.labs2160.slacker.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to enable self-documentation of Actions.
 * Action description and configuration details are used in
 * "help" functions in Slacker.  Most configuration here
 * can be overriden in the Slacker configuration file.
 */
@Target({ElementType.TYPE}) @Retention(RetentionPolicy.RUNTIME)
public @interface ActionDescription {

    /** display name for the action - e.g. "Stock Price" */
    String name() default "";

    /** details */
    String description() default "";

    /** describe arguments (values following the alias) needed by the action - e.g. <stock symbol> */
    String argsSpec()  default "";

    /** example values for args */
    String argsExample()  default "";

    /** describe configuraiton parameters */
    ConfigParam [] configParams() default {};
}
