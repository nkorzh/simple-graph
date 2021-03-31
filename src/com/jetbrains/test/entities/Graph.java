package com.jetbrains.test.entities;

import com.jetbrains.test.interfaces.NodeQuery;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Graph implements NodeQuery {
    private final List<Node> nodeList;
    private final Map<String, Integer> nameMap;

    private Graph() {
        nodeList = new ArrayList<>();
        nameMap = new HashMap<>();
    }

    public static Graph createEmpty() {
        return new Graph();
    }

    public void saveToFile(Path path) throws IOException {
        try (final BufferedWriter writer = Files.newBufferedWriter(Objects.requireNonNull(path),
                StandardCharsets.UTF_8)) {
            writer.write(String.valueOf(nodeList.size()));
            writer.newLine();
            for (Node node : nodeList) {
                writer.write(node.getValue());
                writer.newLine();
            }
            for (Node node : nodeList) {
                writer.write(node.getNodes().size() + " ");
                for (Node child : node.getNodes()) {
                    writer.write(nameMap.get(child.getValue()) + " ");
                }
                writer.newLine();
            }
        } catch (final IOException e) {
            throw new IOException("Error writing to file '" + path.getFileName() + "': " + e.getMessage());
        } catch (final SecurityException e) {
            throw new IOException("Don't have access to write to '" + path.getFileName() + "'", e);
        }
    }

    public static Graph readFromFile(Path path) throws IOException {
        Graph graph = createEmpty();
        try (final Scanner scanner = new Scanner(Files.newBufferedReader(Objects.requireNonNull(path),
                StandardCharsets.UTF_8))) {
            int nodesAmount = scanner.nextInt();
            scanner.skip("\\s+");
            List<Node> nodes = graph.nodeList;
            for (int i = 0; i < nodesAmount; i++) {
                final String nodeValue = scanner.nextLine();
                if (graph.nameMap.put(nodeValue, i) != null) {
                    throw new IOException("Invalid file. Node with value '" + nodeValue +
                            "' already exists.");
                }
                nodes.add(new Node(nodeValue));
            }
            for (final Node parent : nodes) {
                final int childrenAmount = scanner.nextInt();
                for (int j = 0; j < childrenAmount; j++) {
                    parent.addNode(nodes.get(scanner.nextInt()));
                }
            }
        } catch (final InputMismatchException e) {
            throw new IOException("Invalid file. Wrong token type: " + e.getMessage());
        } catch (final IOException e) {
            throw new IOException("Error writing to file '" + path.toAbsolutePath() + "': " + e.getMessage());
        } catch (final SecurityException e) {
            throw new IOException("Don't have access to write to '" + path.toAbsolutePath() + "'", e);
        }
        return graph;
    }

    @Override
    public boolean addNode(String nodeName) {
        if (nameMap.containsKey(Objects.requireNonNull(nodeName))) {
            return false;
        }
        nameMap.put(nodeName, nodeList.size());
        nodeList.add(new Node(nodeName));
        return true;
    }

    @Override
    public List<String> getChildren(String nodeName) {
        final Node node = getNode(nodeName);
        return node == null ?
            Collections.emptyList() :
            node.getNodes()
                    .stream()
                    .map(Node::getValue)
                    .collect(Collectors.toList());
    }

    private Node getNode(String nodeName) {
        final Integer nodeId = nameMap.get(Objects.requireNonNull(nodeName));
        if (nodeId == null) {
            System.err.println("Node '" + nodeName + "' is not in the graph.");
            return null;
        }
        return nodeList.get(nodeId);
    }

    @Override
    public boolean addEdge(String fromName, String toName) {
        return addEdge(getNode(fromName), getNode(toName));
    }

    private boolean addEdge(Node fromNode, Node toNode) {
        if (fromNode == null || toNode == null) {
            return false;
        }
        return fromNode.addNode(toNode);
    }

    @Override
    public boolean removeEdge(String fromName, String toName) {
        return removeEdge(getNode(fromName), getNode(toName));
    }

    private boolean removeEdge(Node fromNode, Node toNode) {
        if (fromNode == null || toNode == null) {
            return false;
        }
        return fromNode.removeNode(toNode);
    }

    @Override
    public boolean isEdge(String fromName, String toName) {
        return isEdge(getNode(fromName), getNode(toName));
    }

    private boolean isEdge(Node fromNode, Node toNode) {
        if (fromNode == null || toNode == null) {
            return false;
        }
        return fromNode.getNodes().contains(toNode);
    }

    @Override
    public List<String> getAllNodes() {
        return nodeList.stream()
                .map(Node::getValue)
                .collect(Collectors.toList());
    }
}
