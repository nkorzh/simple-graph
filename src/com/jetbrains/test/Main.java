package com.jetbrains.test;

import com.jetbrains.test.entities.Graph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final String EXIT = "exit";
    private static final String LOAD = "load";
    private static final String CREATE = "create";
    private static final String SAVE = "save";
    private static final String YES = "yes";
    private static final String ALL = "all";
    private static final String CHILDREN = "pe";
    private static final String CHECK_EDGE = "ce";
    private static final String ADD_EDGE = "ae";
    private static final String ADD_NODE = "an";
    private static final String DEL_EDGE = "de";

    private static final Map<String, String> commandsMap = Map.of(
            EXIT, "exit",
            LOAD, "load graph from file",
            CREATE, "create empty graph",
            SAVE, "save to file",
            ALL, "print all nodes",
            CHILDREN, "print children of node",
            CHECK_EDGE, "check if edge (<parent>, <child>) exists",
            ADD_EDGE, "add <child> to <parent> node list",
            ADD_NODE, "create new node");

    private static Graph graph = null;
    private static boolean haveUnsaved = false;
    private static boolean isLoaded = false;
    private static Scanner input;

    private static void load() {
        try {
            System.out.println("Enter filename:");
            final Path path = Path.of(input.nextLine());
            graph = Graph.readFromFile(path);
            isLoaded = true;
            System.out.println("Graph is loaded.");
        } catch (final InvalidPathException e) {
            System.out.println("Invalid path: " + e.getMessage());
        } catch (final IOException e) {
            System.out.println("Error loading graph: " + e.getMessage());
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
            System.out.println("Invalid path: " + e.getMessage());
        } catch (final IOException e) {
            System.out.println("Error saving graph: " + e.getMessage());
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
        commandsMap.forEach((key, value) -> System.out.println(key + " - " + value));
        System.out.println(String.format("%nWrite your command:"));
    }

    public static void main(String[] args) {
        input = new Scanner(System.in);
        System.out.println("Graph query application started.");
        printCommands();
        while (true) {
            String command = input.nextLine();
            switch (command) {
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
                        List<String> nodes = graph.getAllNodes();
                        nodes.forEach(Main::printName);
                        printAmount(nodes.size());
                    }
                    break;
                case CHILDREN:
                    if (isLoaded()) {
                        System.out.println("Enter parent node name:");
                        List<String> children = graph.getChildren(input.nextLine());
                        System.out.println("Children are:");
                        children.forEach(Main::printName);
                        printAmount(children.size());
                    }
                    break;
                case CHECK_EDGE:
                    if (isLoaded()) {
                        System.out.println("Enter parent and child node names divided by Enter:");
                        System.out.println(graph.isEdge(input.nextLine(), input.nextLine()) ?
                                "Edge exists" :
                                "No edge");
                    }
                    break;
                case ADD_EDGE:
                    if (isLoaded()) {
                        System.out.println("Enter parent and child node names divided by Enter:");
                        System.out.println(graph.addEdge(input.nextLine(), input.nextLine()) ? "Success" : "Failed.");
                        haveUnsaved = true;
                    }
                    break;
                case ADD_NODE:
                    if (isLoaded()) {
                        System.out.println("Enter new node name:");
                        System.out.println(graph.addNode(input.nextLine()) ? "Success." : "Failed.");
                        haveUnsaved = true;
                    }
                    break;
                case DEL_EDGE:
                    if (isLoaded()) {
                        System.out.println("Enter parent and child node names divided by Enter:");
                        graph.removeEdge(input.nextLine(), input.nextLine());
                    }
                default:
                    System.out.println("Unexpected command.");
                    printCommands();
                    break;
            }
        }
    }
}
