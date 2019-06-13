package com.track.toy.test.core.node;

import com.alibaba.fastjson.JSON;
import com.track.toy.helper.HttpHelper;
import com.track.toy.test.core.common.Constant;
import lombok.Data;

@Data
public class HttpTestNode extends TestNode {
    private String url;

    @Override
    public void testSelf() {
        this.output = JSON.parseObject(
                HttpHelper.post(
                        Constant.EXPRESSION_HELPER.expressionFilter(this.url),
                        Constant.EXPRESSION_HELPER.expressionFilter(this.input.toJSONString())));

    }
}
