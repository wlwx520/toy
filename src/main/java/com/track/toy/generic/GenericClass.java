package com.track.toy.generic;

import lombok.Data;

import java.util.Map;

/**
 * 泛型结构
 *
 * @author javen
 */
@Data
public class GenericClass {
    private String genericName;

    private Class clazz;

    private GenericClass declaration;

    private Map<String, GenericClass> genericTypes;

    private Map<String, GenericClass> fields;
}
