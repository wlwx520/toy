package com.track.toy.test.core.expression;

import com.alibaba.fastjson.JSON;
import com.track.toy.test.core.common.ActionInfo;
import com.track.toy.test.core.common.Constant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionInfoExpression {

    static {
        //使用$ActionInfo{1234#系统}的表达式快速生成actionInfo
        Constant.EXPRESSION_HELPER.addFilter("$ActionInfo", (origin, objects) -> {
            ActionInfo actionInfo = new ActionInfo();
            try {
                String[] split = origin.split("#");
                if (split != null && split.length >= 1) {

                    actionInfo.setUserId(Integer.valueOf(split[0]));
                }
                if (split != null && split.length >= 2) {
                    actionInfo.setNickname(split[1]);
                }
            } catch (Exception e) {
                log.info("expressionFilter to actionInfo error.", e);
            }
            return JSON.toJSONString(actionInfo);
        });
    }
}
