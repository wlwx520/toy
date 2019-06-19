package com.track.toy.test.core.expression;

import com.track.toy.test.core.Constant;
import com.track.toy.test.core.factory.ConfigureFactory;

public class DataExpression {
    public static void init() {
        Constant.addFilter("$NULL", (origin, objects) -> null);
        Constant.addFilter("$PROPERTY", (origin, objects) -> ConfigureFactory.get(origin));
    }
}
