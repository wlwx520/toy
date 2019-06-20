package com.track.toy;

import com.track.toy.test.core.Constant;

public class Test {
    public static void main(String[] args) {
        Constant.initExpression();

        String array = "$COLLECT(/[123,1234,12345/]#)";

        String express = Constant.express(array);

        System.out.println(express);
    }
}
