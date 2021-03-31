package com.jetbrains.test.interfaces;

import java.util.List;

public interface NodeQuery {
    List<String> getChildren(final String nodeName);
    boolean addNode(final String nodeName);
    boolean addEdge(final String fromName, final String toName);
    boolean removeEdge(final String fromName, final String toName);
    boolean isEdge(final String fromName, final String toName);
    List<String> getAllNodes();
}
