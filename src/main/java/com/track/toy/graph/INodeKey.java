package com.track.toy.graph;

@FunctionalInterface
public interface INodeKey<T, K> {
    K nodeKey(T data);
}
