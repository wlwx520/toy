package com.track.toy.test.core.prepare;

import com.track.toy.graph.HierarchyNode;
import com.track.toy.test.core.asserts.TestAssertType;
import com.track.toy.test.core.node.TestNode;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

//开启测试节点的条件
@Slf4j
public enum PrepareType implements ICheckPrepared {
    //当所有父节点完成时，开启测试
    ALL {
        @Override
        public boolean isPrepared(String prepareValue, TestNode testNode) {
            HierarchyNode<TestNode> hierarchy = testNode.getTestGraph().getTempGraphData().getPlusHandler().getHierarchy(testNode.getName(), -1, 0);
            Set<HierarchyNode<TestNode>> sources = hierarchy.getSources();
            if (sources == null || sources.isEmpty()) {
                return true;
            }
            return sources.stream().allMatch(source -> source.getData().isSuccess());
        }
    },
    //当任意数量的父节点完成时，开启测试，prepareValue为需要开启测试的父节点数
    ANY {
        @Override
        public boolean isPrepared(String prepareValue, TestNode testNode) {
            int preparedSize;
            try {
                preparedSize = Integer.valueOf(prepareValue);
            } catch (Exception e) {
                log.info("any prepareValue must be a number . to select ALL , testNode = {}", testNode.getName());
                return ALL.isPrepared(prepareValue, testNode);
            }
            HierarchyNode<TestNode> hierarchy = testNode.getTestGraph().getTempGraphData().getPlusHandler().getHierarchy(testNode.getName(), -1, 0);
            Set<HierarchyNode<TestNode>> sources = hierarchy.getSources();
            if (sources == null || sources.isEmpty()) {
                return true;
            }
            return sources.stream().filter(source -> source.getData().isSuccess()).count() >= preparedSize;
        }
    },
    //当指定父节点完成时，开启测试，prepareValue为指定父节点名，多个按‘,’分隔
    SIGN {
        @Override
        public boolean isPrepared(String prepareValue, TestNode testNode) {
            String[] split;
            try {
                split = prepareValue.split(",");
            } catch (Exception e) {
                log.info("SIGN prepareValue must be String of the name to split ',' . to select ALL , testNode = {}", testNode.getName());
                return ALL.isPrepared(prepareValue, testNode);
            }
            if (split.length != 0) {
                List<String> names = Arrays.asList(split);
                Stream<String> nameStream = names.stream().filter(name -> !"".equals(name.trim()));
                if (nameStream.count() != 0) {
                    return nameStream.allMatch(name -> testNode.getTestGraph().getTempGraphData().getNodeHandler().getNode(name).isSuccess());
                }
            }
            return ALL.isPrepared(prepareValue, testNode);
        }
    };

    public static PrepareType getFromName(String type){
        if (type == null) {
            throw new RuntimeException("PrepareType is null");
        }
        PrepareType[] types = PrepareType.values();
        for (PrepareType prepareType : types) {
            if (prepareType.name().toLowerCase().equals(type.toLowerCase())) {
                return prepareType;
            }
        }
        throw new RuntimeException("PrepareType not found , type = " + type);
    }

}
