package com.track.toy.graph;

public interface IGraph<T, R extends Comparable<R>, K, E> extends INodeKey<T, K>, IEdgeKey<T, E>, IEdgeRight<T, R> {
}
