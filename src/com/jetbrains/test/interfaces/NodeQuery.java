package com.jetbrains.test.interfaces;

import java.io.IOException;
import java.util.List;

public interface NodeQuery {
    List<String> getChildren(String nodeName) throws IOException;

    void addNode(String nodeName) throws IOException;

    void removeNode(String nodeName) throws IOException;

    boolean addEdge(String fromName, String toName) throws IOException;

    boolean removeEdge(String fromName, String toName) throws IOException;

    boolean isEdge(String fromName, String toName) throws IOException;

    List<String> getAllNodes();
}
