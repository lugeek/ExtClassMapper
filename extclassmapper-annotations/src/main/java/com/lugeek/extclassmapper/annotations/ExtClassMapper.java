package com.lugeek.extclassmapper.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Target(TYPE)
@Retention(CLASS)
public @interface ExtClassMapper {
    String[] value();
    String group() default "default";
}