package com.track.toy.graph;

public class Edge<T, R extends Comparable<R>, K, E> {
    private IEdgeKey<T, E> key;
    private IEdgeRight<T, R> right;

    private Node<T, R, K, E> source;
    private Node<T, R, K, E> target;

    private Graph<T, R, K, E> graph;

    E getKey() {
        return key.edgeKey(source.getData(), target.getData());
    }

    Node<T, R, K, E> getSource() {
        return source;
    }

    Node<T, R, K, E> getTarget() {
        return target;
    }

    R getRight() {
        return right.edgeRight(source.getData(), target.getData());
    }

    Graph<T, R, K, E> getGraph() {
        return graph;
    }

    Edge(IEdgeKey<T, E> key, IEdgeRight<T, R> right, Node<T, R, K, E> source, Node<T, R, K, E> target, Graph<T, R, K, E> graph) {
        this.key = key;
        this.right = right;
        this.source = source;
        this.target = target;
        this.graph = graph;

        source.addTarget(this);
        target.addSource(this);
    }

    void remove() {
        source.removeTarget(this);
        target.removeSource(this);
    }
}
