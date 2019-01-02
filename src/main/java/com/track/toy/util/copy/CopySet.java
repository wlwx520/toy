package com.track.toy.util.copy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CopySet {
    Class<? extends Set> value();
    String[] key() default {};
}