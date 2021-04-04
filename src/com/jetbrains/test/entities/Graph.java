package com.jetbrains.test.entities;

import com.jetbrains.test.interfaces.NodeQuery;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Graph implements NodeQuery {
    private final static int INVALID_ID = -1;
    private final Map<String, Integer> nameToId;
    private final Map<Integer, String> idToName;
    private final Map<Integer, Node> nodes;

    private Graph() {
        nameToId = new HashMap<>();
        idToName = new HashMap<>();
        nodes = new HashMap<>();
    }

    public static Graph createEmpty() {
        return new Graph();
    }

    public void saveToFile(@NotNull Path path) throws IOException {
        try (final BufferedWriter writer = Files.newBufferedWriter(path,
                StandardCharsets.UTF_8)) {
            writer.write(String.valueOf(nodes.size()));
            writer.newLine();

            for (Node node : nodes.values()) {
                writer.write(idToName.get(node.getId()));
                writer.newLine();
                writer.write(String.valueOf(node.getId()));
                writer.newLine();
            }
            for (Node node : nodes.values()) {
                writer.write(node.getChildren().size() + " ");
                for (Integer childId : node.getChildren()) {
                    writer.write(childId + " ");
                }
                writer.newLine();
            }
        } catch (final IOException e) {
            throw new IOException("Error writing to file '" + path.getFileName() + "'", e);
        } catch (final SecurityException e) {
            throw new IOException("Don't have access to write to '" + path.getFileName() + "'", e);
        }
    }

    public static Graph readFromFile(@NotNull final Path path) throws IOException {
        Graph graph = createEmpty();
        try (final Scanner scanner = new Scanner(Files.newBufferedReader(path,
                StandardCharsets.UTF_8))) {
            final int nodesAmount = scanner.nextInt();
            scanner.skip("\\s+");
            final Map<String, Integer> nameToId = graph.nameToId;
            final Map<Integer, String> idToName = graph.idToName;
            final Map<Integer, Node> nodes = graph.nodes;

            for (int i = 0; i < nodesAmount; i++) {
                final String nodeValue = scanner.nextLine();
                final Integer id = scanner.nextInt();
                if (nameToId.put(nodeValue, id) != null || idToName.put(id, nodeValue) != null) {
                    throw new IOException("Invalid file. Node with value '" + nodeValue +
                            "' already exists.");
                }
                nodes.put(id, new Node(id));
                scanner.skip("\\s+");
            }
            for (int i = 0; i < nodesAmount; i++) {
                final int childrenAmount = scanner.nextInt();
                final Node parent = nodes.get(i);
                for (int j = 0; j < childrenAmount; j++) {
                    if (!graph.addEdge(parent, nodes.get(scanner.nextInt()))) {
                        throw new IOException("No value is associated with id '" + i +
                                "' not in the file '" + path.toAbsolutePath() + "'");
                    }
                }
            }
        } catch (final InputMismatchException e) {
            throw new IOException("Invalid file. Wrong token type. ", e);
        } catch (final IOException e) {
            throw new IOException("Error writing to file '" + path.toAbsolutePath() + "'", e);
        } catch (final SecurityException e) {
            throw new IOException("Don't have access to write to '" + path.toAbsolutePath() + "'", e);
        }
        return graph;
    }

    @Override
    public void addNode(final String nodeName) throws IOException {
        Integer newId = nodes.size();
        if (nameToId.putIfAbsent(nodeName, newId) != null) {
            throw new IOException("Node with name '" + nodeName + "' already exists");
        }
        idToName.put(newId, nodeName);
        nodes.put(newId, new Node(newId));
    }

    @Override
    public List<String> getChildren(final String nodeName) throws IOException {
        return getNode(nodeName)
                .getChildren()
                .stream()
                .map(idToName::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean addEdge(final String fromName, final String toName) throws IOException {
        return addEdge(getNode(fromName), getNode(toName));
    }

    @Override
    public void removeNode(final String nodeName) throws IOException {
        final Node node = getNode(nodeName);
        final int id = node.getId();
        node.getParents()
                .stream()
                .map(nodes::get)
                .forEach(x -> x.removeChild(id));
        node.getChildren()
                .stream()
                .map(nodes::get)
                .forEach(x -> x.removeParent(id));
        nameToId.remove(nodeName);
        idToName.remove(id);
        nodes.remove(id);
    }

    @Override
    public boolean removeEdge(final String fromName, final String toName) throws IOException {
        return removeEdge(getNode(fromName), getNode(toName));
    }

    @Override
    public boolean isEdge(final String fromName, final String toName) throws IOException {
        return isEdge(getNode(fromName), getNode(toName));
    }

    @Override
    public List<String> getAllNodes() {
        return new ArrayList<>(nameToId.keySet());
    }

    private Integer getId(@NotNull final String nodeName) {
        return nameToId.getOrDefault(nodeName, INVALID_ID);
    }

    private @NotNull Node getNode(final String nodeName) throws IOException {
        final Node node = nodes.get(getId(nodeName));
        if (node == null) {
            throw new IOException("Node with name '" + nodeName + "' does not exist");
        }
        return node;
    }

    private boolean addEdge(@NotNull final Node fromNode, @NotNull final Node toNode) {
        return fromNode.addChild(toNode.getId()) && toNode.addParent(fromNode.getId());
    }

    private boolean removeEdge(@NotNull final Node fromNode, @NotNull final Node toNode) {
        return fromNode.removeChild(toNode.getId()) && toNode.removeParent(fromNode.getId());
    }

    private boolean isEdge(@NotNull final Node fromNode, @NotNull final Node toNode) {
        return fromNode.hasChild(toNode.getId());
    }
}
