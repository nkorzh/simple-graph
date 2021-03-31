package com.jetbrains.test.entities;

import java.util.*;

public class Node {
    private final String value;
    private final List<Node> nodes;

    public Node(String name) {
        value = name;
        nodes = new ArrayList<>();
    }

    public String getValue() {             
        return value;
    }

    public List<Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public boolean addNode(final Node node) {
        return nodes.add(Objects.requireNonNull(node));
    }

    public boolean removeNode(final Node node) {
        return nodes.remove(node);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return value.equals(node.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
