package com.jetbrains.test;

import com.jetbrains.test.entities.Graph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String YES = "yes";

    private enum Command {
        DEFAULT("", "Unknown command"),
        EXIT("exit", "exit"),
        ALL("all", "print all nodes"),
        LOAD("load", "load graph from file"),
        CREATE("create", "create empty graph"),
        SAVE("save", "save to file"),
        CHILDREN("pe", "print children of node"),
        CHECK_EDGE("ce", "check if edge (<parent>, <child>) exists"),
        ADD_EDGE("ae", "add <child> to <parent> node list"),
        ADD_NODE("an", "create new node"),
        DELETE_EDGE("de", "delete edge"),
        DELETE_NODE("dn", "delete node");

        public final String value;
        public final String description;

        Command(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public static Command parseCommand(String commandName) {
            for (Command command : Command.values()) {
                if (command.value.equals(commandName))
                    return command;
            }
            return DEFAULT;
        }

        public static void printCommands() {
            final Command[] commands = Command.values();
            for (int i = 1; i < commands.length; i++) {
                System.out.println(commands[i].value + " - " + commands[i].description);
            }
        }
    }

    private static Graph graph = null;
    private static boolean haveUnsaved = false;
    private static boolean isLoaded = false;
    private static Scanner input;

    private static void printAllNodes() {
        List<String> nodes = graph.getAllNodes();
        nodes.forEach(Main::printName);
        printAmount(nodes.size());
    }

    private static void load() {
        try {
            System.out.println("Enter filename:");
            final Path path = Path.of(input.nextLine());
            graph = Graph.readFromFile(path);
            isLoaded = true;
            System.out.println("Graph is loaded.");
        } catch (final InvalidPathException e) {
            System.err.println("Invalid path: " + e.getMessage());
        } catch (final IOException e) {
            System.err.println("Error loading graph: " + e.getMessage());
        }
    }

    private static void save() {
        try {
            System.out.println("Enter filename:");
            final Path path = Path.of(input.nextLine());
            if (Files.exists(path)) {
                System.out.println("File already exists and will be overwritten. Continue ('yes' or 'no')?");
                if (!YES.equals(input.nextLine())) {
                    return;
                }
            }
            graph.saveToFile(path);
            haveUnsaved = false;
            System.out.println("Saved to " + path.toAbsolutePath());
        } catch (final InvalidPathException e) {
            System.err.println("Invalid path: " + e.getMessage());
        } catch (final IOException e) {
            System.err.println("Error saving graph: " + e.getMessage());
        }
    }

    private static void create() {
        graph = Graph.createEmpty();
        isLoaded = true;
        System.out.println("Graph is loaded.");
    }

    private static boolean isLoaded() {
        if (!isLoaded) {
            System.out.println("Graph is not loaded. Type 'load' or 'create' to print all nodes.");
        }
        return isLoaded;
    }

    private static boolean confirmUnsaved() {
        if (haveUnsaved) {
            System.out.println("You have unsaved changes. Are you sure? ('yes' or 'no')");
            return YES.equals(input.nextLine());
        }
        return true;
    }

    private static void printAmount(int amount) {
        System.out.println(amount + " total.");
    }

    private static void printName(String name) {
        System.out.println("'" + name + "'");
    }

    private static void printCommands() {
        System.out.println(String.format("Available commands:%n"));
        Command.printCommands();
        System.out.println(String.format("%nWrite your command:"));
    }

    private static void printChildren() {
        System.out.println("Enter parent node name:");
        List<String> children;
        final String nodeName = input.nextLine();
        try {
            children = graph.getChildren(nodeName);
        } catch (final IOException e) {
            System.err.println("Error getting children of '" + nodeName + "': " + e.getMessage());
            return;
        }
        System.out.println("Children are:");
        children.forEach(Main::printName);
        printAmount(children.size());
    }

    private static void checkEdge() {
        System.out.println("Enter parent and child node names divided by Enter:");
        try {
            System.out.println(graph.isEdge(input.nextLine(), input.nextLine()) ?
                    "Edge exists" :
                    "No edge");
        } catch (IOException e) {
            System.err.println("Can't find nodes: " + e.getMessage());
        }
    }

    private static void addEdge() {
        System.out.println("Enter parent and child node names divided by Enter:");
        try {
            System.out.println(graph.addEdge(input.nextLine(), input.nextLine()) ? "Success." : "Failed.");
        } catch (final IOException e) {
            System.err.println("Can't add edge: " + e.getMessage());
            return;
        }
        haveUnsaved = true;
    }

    private static void addNode() {
        System.out.println("Enter new node name:");
        try {
            graph.addNode(input.nextLine());
            System.out.println("Success.");
        } catch (IOException e) {
            System.err.println("Can't add node: " + e.getMessage());
        }
        haveUnsaved = true;
    }

    private static void deleteEdge() {
        System.out.println("Enter parent and child node names divided by Enter:");
        try {
            System.out.println(graph.removeEdge(input.nextLine(), input.nextLine()) ?
                    "Edge removed." :
                    "Edge did not exist.");
        } catch (final IOException e) {
            System.err.println("Error removing edge: " + e.getMessage());
        }
    }

    private static void deleteNode() {
        System.out.println("Enter node name:");
        try {
            graph.removeNode(input.nextLine());
            System.out.println("Deleted.");
        } catch (final IOException e) {
            System.err.println("Can't delete node: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        input = new Scanner(System.in);
        System.out.println("Graph query application started.");
        printCommands();
        while (true) {
            switch (Command.parseCommand(input.nextLine())) {
                case EXIT:
                    if (confirmUnsaved()) {
                        return;
                    }
                    break;
                case LOAD:
                    if (confirmUnsaved()) {
                        load();
                    }
                    break;
                case SAVE:
                    save();
                    break;
                case CREATE:
                    if (confirmUnsaved()) {
                        create();
                    }
                    break;
                case ALL:
                    if (isLoaded()) {
                        printAllNodes();
                    }
                    break;
                case CHILDREN:
                    if (isLoaded()) {
                        printChildren();
                    }
                    break;
                case CHECK_EDGE:
                    if (isLoaded()) {
                        checkEdge();
                    }
                    break;
                case ADD_EDGE:
                    if (isLoaded()) {
                        addEdge();
                    }
                    break;
                case ADD_NODE:
                    if (isLoaded()) {
                        addNode();
                    }
                    break;
                case DELETE_EDGE:
                    if (isLoaded()) {
                        deleteEdge();
                    }
                case DELETE_NODE:
                    if (isLoaded()) {
                        deleteNode();
                    }
                case DEFAULT:
                    System.out.println("Unexpected command.");
                    printCommands();
                    break;
            }
        }
    }
}
