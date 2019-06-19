package com.track.toy.test.core.expression;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.track.toy.graph.Graph;
import com.track.toy.test.core.Constant;
import com.track.toy.test.core.factory.ConfigureFactory;
import com.track.toy.test.core.node.TestNode;

import java.util.Arrays;
import java.util.List;

public class DataExpression {
    public static void init() {
        Constant.addFilter("$NULL", (origin, objects) -> null);
        Constant.addFilter("$PROPERTY", (origin, objects) -> ConfigureFactory.get(origin));

        Constant.addFilter("$DATA", (origin, objects) -> {
            if (objects == null || objects.length < 1) {
                throw new RuntimeException("expression of DATA must input tempGraphData");
            }

            Graph<TestNode, Double, String, String> tempGraphData;
            try {
                tempGraphData = (Graph<TestNode, Double, String, String>) objects[0];
            } catch (Exception e) {
                throw new RuntimeException("expression of DATA must input tempGraphData");
            }

            String[] split = origin.split("#");
            if (split == null || split.length < 2) {
                throw new RuntimeException("expression of DATA error");
            }

            String testNodeName = split[0];
            String inOrOut = split[1];

            TestNode testNode = tempGraphData.getNodeHandler().getNode(testNodeName);
            if (testNode == null) {
                throw new RuntimeException("expression of DATA error , testNode not found name = " + testNodeName);
            }

            if (!"INPUT".equals(inOrOut) || !"OUTPUT".equals(inOrOut)) {
                throw new RuntimeException("expression of DATA error , must be INPUT or OUTPUT");
            }

            String json = "INPUT".equals(inOrOut) ? testNode.getInput().toJSONString() : testNode.getOutput().toJSONString();

            List<String> params = Arrays.asList(split);
            params.remove(0);
            params.remove(0);

            String value = getValue(json, params);

            return value;
        });
    }

    private static String getValue(String json, List<String> params) {
        if (params.isEmpty()) {
            return json;
        }

        String remove = params.remove(0);

        if (isArray(remove)) {
            int index = getArrayIndex(remove);
            JSONArray array;
            try {
                array = JSONArray.parseArray(json);
            } catch (Exception e) {
                throw new RuntimeException("json error json = " + json + " param = " + remove);
            }
            String subJson = array.get(index).toString();
            return getValue(subJson, params);
        } else {
            JSONObject object;
            try {
                object = JSONObject.parseObject(json);
            } catch (Exception e) {
                throw new RuntimeException("json error json = " + json + " param = " + remove);
            }
            if (!object.containsKey(remove)) {
                throw new RuntimeException("json error json = " + json + " param = " + remove);
            }
            String subJson = object.get(remove).toString();
            return getValue(subJson, params);
        }
    }

    private static boolean isArray(String remove) {
        if (remove.indexOf("[") != -1 && remove.indexOf("]") != -1) {
            return true;
        }
        return false;
    }

    private static int getArrayIndex(String remove) {
        remove = remove.replace("[", "");
        remove = remove.replace("]", "");

        try {
            Integer integer = Integer.valueOf(remove);
            return integer;
        } catch (Exception e) {
            throw new RuntimeException("getArrayIndex error remove = " + remove);
        }
    }
}
