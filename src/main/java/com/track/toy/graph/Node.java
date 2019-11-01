package com.track.toy.graph;

import java.util.HashSet;
import java.util.Set;

public class Node<T, R extends Comparable<R>, K, E> {
    private T data;
    private INodeKey<T, K> key;

    private Set<Edge<T, R, K, E>> sources;
    private Set<Edge<T, R, K, E>> targets;

    private Graph<T, R, K, E> graph;

    T getData() {
        return data;
    }

    K getKey() {
        return key.nodeKey(data);
    }

    Graph<T, R, K, E> getGraph() {
        return graph;
    }

    Node(T data, INodeKey<T, K> key, Graph<T, R, K, E> graph) {
        this.data = data;
        this.key = key;
        this.sources = new HashSet<>();
        this.targets = new HashSet<>();
        this.graph = graph;
    }

    void addSource(Edge<T, R, K, E> source) {
        sources.add(source);
    }

    void addTarget(Edge<T, R, K, E> target) {
        targets.add(target);
    }

    void removeSource(Edge<T, R, K, E> source) {
        sources.remove(source);
    }

    void removeTarget(Edge<T, R, K, E> target) {
        targets.remove(target);
    }

    public Set<Edge<T, R, K, E>> getSources() {
        return sources;
    }

    public Set<Edge<T, R, K, E>> getTargets() {
        return targets;
    }
}
