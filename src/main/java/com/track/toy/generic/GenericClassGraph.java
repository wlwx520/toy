package com.track.toy.generic;

import com.track.toy.graph.Graph;
import com.track.toy.graph.IGraph;

/**
 * 泛型结构图
 *
 * @author javen
 */
public class GenericClassGraph extends Graph<GenericClass, Integer, String, String> {
    public GenericClassGraph() {
        super(new IGraph<GenericClass, Integer, String, String>() {
            @Override
            public String nodeKey(GenericClass data) {
                return data.getClazz().getName();
            }

            @Override
            public String edgeKey(GenericClass source, GenericClass target) {
                return source.getClazz().getName() + "_" + target.getClazz().getName();
            }

            @Override
            public Integer edgeRight(GenericClass source, GenericClass target) {
                return 1;
            }
        });
    }

}
