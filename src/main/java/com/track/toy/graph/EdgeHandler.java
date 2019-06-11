package com.track.toy.graph;

public class EdgeHandler<T, R extends Comparable<R>, K, E> {
    private Graph<T, R, K, E> graph;

    EdgeHandler(Graph<T, R, K, E> graph) {
        this.graph = graph;
    }

    public E newEdge(T source, T target) {
        return graph.newEdge(source, target);
    }

    public E newEdgeByKey(K sourceKey, K targetKey) {
        return graph.newEdgeByKey(sourceKey, targetKey);
    }

    public E removeEdge(T source, T target) {
        return graph.removeEdge(source, target);
    }

    public E removeEdgeByKey(K source, K target) {
        return graph.removeEdgeByKey(source, target);
    }

    public E removeEdgeByKey(E key) {
        return graph.removeEdgeByKey(key);
    }

    public R getRight(T source, T target) {
        return graph.getRight(source, target);
    }

    public R getRightByKey(K source, K target) {
        return graph.getRightByKey(source, target);
    }

    public R getRightByKey(E key) {
        return graph.getRightByKey(key);
    }
}