package com.example.Graphimplementierung.Grundstruktur.Nodes;

import java.util.*;
import java.util.stream.Collectors;

public class BPMNGraph {
    private final Map<String, Node> nodes;
    private final List<Edge> edges;

    public BPMNGraph() {
        this.nodes = new HashMap<>();
        this.edges = new ArrayList<>();
    }


    // Knoten hinzufügen
    public void addNode(Node node) {
        // Überprüfen, ob der Knoten mit der gleichen ID bereits existiert
        if (nodes.containsKey(node.getId())) {
            // Hier können wir entscheiden, was mit bestehenden Knoten zu tun ist:
            // Möglichkeit 1: Knoten ignorieren und keine Warnung anzeigen.
            // System.out.println("Knoten mit ID " + node.getId() + " existiert bereits und wird ignoriert.");

            // Möglichkeit 2: Den Knoten aktualisieren, wenn er bereits existiert.
            nodes.put(node.getId(), node); // Knoten aktualisieren
        } else {
            nodes.put(node.getId(), node); // Füge den neuen Knoten hinzu
        }
    }



    // Kante hinzufügen
    public void addEdge(Edge edge) {
        if (!nodes.containsKey(edge.getSource().getId()) || !nodes.containsKey(edge.getTarget().getId())) {
            throw new IllegalArgumentException("Source or target node for the edge does not exist.");
        }
        edges.add(edge);
    }

    public Collection<Node> getNodes() {
        return nodes.values();
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Node getNodeById(String nodeid) {
        return nodes.get(nodeid);  // Gibt den Knoten mit der ID zurück, oder null, wenn der Knoten nicht gefunden wurde
    }
}
