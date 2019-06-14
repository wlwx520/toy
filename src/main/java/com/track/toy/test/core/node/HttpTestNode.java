package com.track.toy.test.core.node;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.track.toy.helper.HttpHelper;
import com.track.toy.test.core.Constant;
import com.track.toy.test.core.asserts.GroupTestAssert;
import com.track.toy.test.core.common.TestGraph;
import com.track.toy.test.core.prepare.PrepareType;

//http-post的方式测试节点
public class HttpTestNode extends TestNode {
    private String url;

    public HttpTestNode(String name, TestGraph testGraph, PrepareType prepareType, String prepareValue, JSONObject input, GroupTestAssert groupTestAssert, String url) {
        super(name, testGraph, prepareType, prepareValue, input, groupTestAssert);
        this.url = url;
    }

    @Override
    public void testSelf() {
        this.output = JSON.parseObject(
                HttpHelper.post(
                        Constant.EXPRESSION_HELPER.expressionFilter(this.url),
                        Constant.EXPRESSION_HELPER.expressionFilter(this.input.toJSONString())));

    }
}
