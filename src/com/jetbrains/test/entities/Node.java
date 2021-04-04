package com.jetbrains.test.entities;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Node {
    private final int id;
    private final Set<Integer> parents;
    private final Set<Integer> children;

    public Node(int id) {
        this.id = id;
        this.parents = new HashSet<>();
        this.children = new HashSet<>();
    }

    public Set<Integer> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    public Set<Integer> getParents() {
        return Collections.unmodifiableSet(parents);
    }

    public boolean hasChild(@NotNull Integer childId) {
        return children.contains(childId);
    }

    public boolean addChild(@NotNull Integer childId) {
        return children.add(childId);
    }

    public boolean addParent(@NotNull Integer parId) {
        return parents.add(parId);
    }

    public boolean removeChild(@NotNull Integer childId) {
        return children.remove(childId);
    }

    public boolean removeParent(@NotNull Integer parId) {
        return parents.remove(parId);
    }

    public int getId() {
        return id;
    }
}
