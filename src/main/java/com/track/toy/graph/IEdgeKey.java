package com.track.toy.graph;

@FunctionalInterface
public interface IEdgeKey<T, E> {
    E edgeKey(T source, T target);
}
