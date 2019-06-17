package com.track.toy.graph;

public class PlusHandler<T, R extends Comparable<R>, K, E> {
    private Graph<T, R, K, E> graph;

    PlusHandler(Graph<T, R, K, E> graph) {
        this.graph = graph;
    }

    public HierarchyNode<T> getHierarchy(K key, int from, int to) {
        return graph.getHierarchy(key, from, to);
    }

    public Graph<T, R, K, E> copy(Graph.INodeDataCopy<T> nodeCopy) {
        return graph.copy(nodeCopy);
    }
}
