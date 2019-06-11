package com.track.toy.graph;

import java.util.HashSet;
import java.util.Set;

public class HierarchyNode<T> {
    private T data;
    private Set<HierarchyNode<T>> sources;
    private Set<HierarchyNode<T>> targets;

    HierarchyNode() {
        this.sources = new HashSet<>();
        this.targets = new HashSet<>();
    }

    public T getData() {
        return data;
    }

    public Set<HierarchyNode<T>> getSources() {
        return sources;
    }

    public Set<HierarchyNode<T>> getTargets() {
        return targets;
    }

    void addSource(HierarchyNode<T> source) {
        this.sources.add(source);
    }

    void addTarget(HierarchyNode<T> target) {
        this.targets.add(target);
    }
}
