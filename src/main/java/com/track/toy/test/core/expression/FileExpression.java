package com.track.toy.test.core.expression;

import com.track.toy.helper.FileHelper;
import com.track.toy.test.core.Constant;

public class FileExpression {
    public static void init() {
        Constant.addFilter("$CONFIGURE", (origin, objects) -> FileHelper.getAppRoot() + "/configure");
    }
}
