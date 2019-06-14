package com.track.toy.test.core.custom;

import lombok.Data;

@Data
public class ActionInfo {
    private Integer fromProduction;
    private Integer sourceSite;
    private Byte phoneType;
    private String sourceIp;
    private Integer operRole;
    private String systemTrigger;
    private Integer userId;
    private String nickname;
}
