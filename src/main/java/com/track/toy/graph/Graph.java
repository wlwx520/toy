package com.track.toy.graph;

import com.track.toy.copy.BeanCopyUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Graph<T, R extends Comparable<R>, K, E> {
    INodeKey<T, K> nodeKey;
    IEdgeKey<T, E> edgeKey;
    IEdgeRight<T, R> right;

    Map<K, Node<T, R, K, E>> allNodes;
    Map<E, Edge<T, R, K, E>> allEdges;

    NodeHandler<T, R, K, E> nodeHandler;
    EdgeHandler<T, R, K, E> edgeHandler;
    PlusHandler<T, R, K, E> plusHandler;

    public Graph(IGraph<T, R, K, E> graph) {
        this.nodeKey = graph;
        this.edgeKey = graph;
        this.right = graph;
        this.allNodes = new HashMap<>();
        this.allEdges = new HashMap<>();
        this.nodeHandler = new NodeHandler<>(this);
        this.edgeHandler = new EdgeHandler<>(this);
        this.plusHandler = new PlusHandler<>(this);
    }

    public NodeHandler<T, R, K, E> getNodeHandler() {
        return nodeHandler;
    }

    public EdgeHandler<T, R, K, E> getEdgeHandler() {
        return edgeHandler;
    }

    public PlusHandler<T, R, K, E> getPlusHandler() {
        return plusHandler;
    }

    K newNode(T data) {
        K key = nodeKey.nodeKey(data);
        if (allNodes.containsKey(key)) {
            throw new GraphException("this node is already exits");
        }

        Node<T, R, K, E> newNode = new Node(data, nodeKey, this);
        allNodes.put(key, newNode);
        return key;
    }

    E newEdge(T source, T target) {
        E key = edgeKey.edgeKey(source, target);

        if (allEdges.containsKey(key)) {
            throw new GraphException("this edge is already exits");
        }

        K sourceKey = nodeKey.nodeKey(source);
        if (!allNodes.containsKey(sourceKey)) {
            throw new GraphException("source node is not exits");
        }

        K targetKey = nodeKey.nodeKey(target);
        if (!allNodes.containsKey(targetKey)) {
            throw new GraphException("target node is not exits");
        }

        Node<T, R, K, E> sourceNode = allNodes.get(sourceKey);
        Node<T, R, K, E> targetNode = allNodes.get(targetKey);

        Edge<T, R, K, E> newEdge = new Edge(edgeKey, right, sourceNode, targetNode, this);

        allEdges.put(key, newEdge);
        return key;
    }

    E newEdgeByKey(K sourceKey, K targetKey) {
        T source = getNode(sourceKey);
        if (!allNodes.containsKey(sourceKey)) {
            throw new GraphException("source node is not exits");
        }

        T target = getNode(targetKey);
        if (!allNodes.containsKey(targetKey)) {
            throw new GraphException("target node is not exits");
        }

        Node<T, R, K, E> sourceNode = allNodes.get(sourceKey);
        Node<T, R, K, E> targetNode = allNodes.get(targetKey);

        E key = edgeKey.edgeKey(source, target);

        if (allEdges.containsKey(key)) {
            throw new GraphException("this edge is already exits");
        }

        Edge<T, R, K, E> newEdge = new Edge(edgeKey, right, sourceNode, targetNode, this);

        allEdges.put(key, newEdge);
        return key;
    }

    K removeNode(T data, boolean force) {
        K key = nodeKey.nodeKey(data);
        return removeNodeByKey(key, force);
    }

    K removeNodeByKey(K key, boolean force) {
        if (!allNodes.containsKey(key)) {
            throw new GraphException("this node is not exits");
        }

        Node<T, R, K, E> removeNode = allNodes.get(key);

        if (!force && !removeNode.getSources().isEmpty()) {
            throw new GraphException("this node has any edge , can not remove , if you want to remove forced , you can use 'force = true'");
        }
        if (!force && !removeNode.getTargets().isEmpty()) {
            throw new GraphException("this node has any edge , can not remove , if you want to remove forced , you can use 'force = true'");
        }

        removeNode.getSources().forEach(source -> {
            removeEdgeByKey(source.getKey());
        });

        removeNode.getTargets().forEach(target -> {
            removeEdgeByKey(target.getKey());
        });

        return key;
    }

    E removeEdge(T source, T target) {
        E key = edgeKey.edgeKey(source, target);
        if (!allEdges.containsKey(key)) {
            throw new GraphException("this edge is not exits");
        }
        return removeEdgeByKey(key);
    }

    E removeEdgeByKey(K source, K target) {
        T sourceNode = getNode(source);
        T targetNode = getNode(target);
        return removeEdge(sourceNode, targetNode);
    }

    E removeEdgeByKey(E key) {
        if (!allEdges.containsKey(key)) {
            throw new GraphException("this edge is not exits");
        }
        Edge<T, R, K, E> removeEdge = allEdges.remove(key);
        removeEdge.remove();
        return key;
    }

    T getNode(K key) {
        Node<T, R, K, E> node = allNodes.get(key);
        return node == null ? null : node.getData();
    }

    K getNodeKey(T data) {
        return data == null ? null : nodeKey.nodeKey(data);
    }

    R getRight(T source, T target) {
        if (source == null || target == null) {
            return null;
        }
        E key = edgeKey.edgeKey(source, target);
        Edge<T, R, K, E> edge = allEdges.get(key);
        return edge == null ? null : edge.getRight();
    }

    R getRightByKey(K source, K target) {
        T sourceNode = getNode(source);
        T targetNode = getNode(target);
        return getRight(sourceNode, targetNode);
    }

    R getRightByKey(E key) {
        Edge<T, R, K, E> edge = allEdges.get(key);
        return edge == null ? null : edge.getRight();
    }

    HierarchyNode<T> getHierarchy(K key, int from, int to) {
        if (from > 0) {
            throw new GraphException("'from' can not be large then 0");
        }

        if (to < 0) {
            throw new GraphException("'to' can not be less then 0");
        }

        Node<T, R, K, E> tempNode = allNodes.get(key);
        if (tempNode == null) {
            return null;
        }

        int ancestors = 0;
        int descendants = 0;

        HierarchyNode<T> resultNode = BeanCopyUtil.deepCopy(HierarchyNode.class, tempNode);
        linkAncestors(tempNode, resultNode, ancestors, from);
        linkDescendants(tempNode, resultNode, descendants, to);
        return resultNode;
    }

    private void linkDescendants(Node<T, R, K, E> tempNode, HierarchyNode<T> resultNode, int ancestors, int to) {
        if (ancestors >= to) {
            return;
        }
        final int ancestorsNext = ancestors + 1;
        Set<Edge<T, R, K, E>> targets = tempNode.getTargets();
        targets.forEach(edge -> {
            Node<T, R, K, E> target = edge.getTarget();
            HierarchyNode<T> subTarget = BeanCopyUtil.deepCopy(HierarchyNode.class, target);
            resultNode.addTarget(subTarget);
            linkDescendants(target, subTarget, ancestorsNext, to);
        });
    }

    private void linkAncestors(Node<T, R, K, E> tempNode, HierarchyNode<T> resultNode, int descendants, int from) {
        if (descendants <= from) {
            return;
        }
        final int descendantsNext = descendants - 1;
        Set<Edge<T, R, K, E>> sources = tempNode.getSources();
        sources.forEach(edge -> {
            Node<T, R, K, E> source = edge.getSource();
            HierarchyNode<T> subSource = BeanCopyUtil.deepCopy(HierarchyNode.class, source);
            resultNode.addSource(subSource);
            linkAncestors(source, subSource, descendantsNext, from);
        });
    }
}