package com.track.toy.graph;

@FunctionalInterface
public interface IEdgeRight<T, R extends Comparable<R>> {
    R edgeRight(T source, T target);
}
