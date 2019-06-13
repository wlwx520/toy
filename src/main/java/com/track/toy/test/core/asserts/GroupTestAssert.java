package com.track.toy.test.core.asserts;

import com.track.toy.test.core.node.TestNode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Data
public class GroupTestAssert extends TestAssert {
    private boolean isAnd = true;
    private List<TestAssert> children = new ArrayList<>();

    //组合断言，有或和且的选择
    public final boolean asserts(TestNode testNode) {
        Stream<TestAssert> stream = children.stream();
        Predicate<TestAssert> predicate = child -> child.asserts(testNode);
        return this.isSuccess = isAnd ? stream.allMatch(predicate) : stream.anyMatch(predicate);
    }
}
