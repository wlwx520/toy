package com.track.toy.copy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CopyFiled {
    String[] value() default {};

    Class<? extends CopyCustom> handler() default CopyCustom.DefaultCopyCustom.class;

    String[] key() default {};
}