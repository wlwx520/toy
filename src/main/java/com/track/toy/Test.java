package com.track.toy;

import com.alibaba.fastjson.JSON;
import com.track.toy.test.core.Constant;
import com.track.toy.test.core.asserts.TestAssertType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        Constant.initExpression();

//        String array = "$COLLECT(/[123,1234,12345/]#)";

        List<Item> items = new ArrayList<>();
        items.add(new Item("d1", 1, new SubItem("s1", 11)));
        items.add(new Item("d2", 2, new SubItem("s2", 12)));
        items.add(new Item("d3", 3, new SubItem("s3", 13)));

        String array2 = JSON.toJSONString(items);

        String a = "$COLLECT(" + array2 + "#item#sname)";
        String express = Constant.express(a);

        System.out.println(express);

        boolean s1 = TestAssertType.CONTAINS.judge(express, "s1");
        System.out.println(s1);
    }

    @AllArgsConstructor
    @Data
    private static class Item {
        String name;
        int id;
        SubItem item;
    }

    @AllArgsConstructor
    @Data
    private static class SubItem {
        String sname;
        int sid;
    }
}
