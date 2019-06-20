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

    public HttpTestNode(String name, String testDateName, TestGraph testGraph, PrepareType prepareType, String prepareValue, JSONObject input, GroupTestAssert groupTestAssert, String url) {
        super(name, testDateName, testGraph, prepareType, prepareValue, input, groupTestAssert);
        this.url = url;
    }

    @Override
    public void testSelf() {
        String url = Constant.express(this.url);
        String inputString;
        if (this.input == null) {
            inputString = "param={}";
        } else {
            String express = Constant.express(this.input.toJSONString(),testGraph.getTempGraphData());
            String param = JSONObject.parseObject(express).get("param").toString();
            inputString = "param=" + param;
        }

        fileLogger.info("inputString = {}",inputString);

        String postResult = HttpHelper.post(url, inputString);
        try {
            this.output = JSON.parseObject(postResult);
        } catch (Exception e) {
            this.output = new JSONObject();
            this.output.put("error", postResult);
        }

    }

    @Override
    public HttpTestNode copy(TestGraph testGraph, String testDateName) {
        return new HttpTestNode(name, testDateName, testGraph, prepareType, prepareValue, null, groupTestAssert, url);
    }
}
