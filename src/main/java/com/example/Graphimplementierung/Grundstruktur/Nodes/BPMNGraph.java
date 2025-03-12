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
        if (nodes.containsKey(node.getId())) {
            // Knoten aktualisieren, falls er bereits existiert
            nodes.put(node.getId(), node);
        } else {
            nodes.put(node.getId(), node); // Füge den neuen Knoten hinzu
        }
    }

    // Kante hinzufügen
    public void addEdge(Edge edge) {
        if (!nodes.containsKey(edge.getSource().getId()) || !nodes.containsKey(edge.getTarget().getId())) {
            throw new IllegalArgumentException("Source or target node for the edge does not exist.");
        }

        // Überprüfe, ob die Kante schon existiert oder ob es eine Selbstreferenz ist
        if (!hasEdge(edge.getId(), edge.getSource(), edge.getTarget())) {
            edges.add(edge);
        }
    }

    // Methode zum Überprüfen, ob eine Kante bereits existiert
    public boolean hasEdge(String id, Node source, Node target) {
        // Verhindere Selbstreferenz (Kante von einem Knoten zu sich selbst)
        if (source.equals(target)) {
            return true; // Keine Selbstreferenz zulassen
        }

        // Überprüfen, ob die Kante mit derselben ID und Quell-/Zielknoten bereits existiert
        for (Edge e : edges) {
            if (e.getId().equals(id) && e.getSource().equals(source) && e.getTarget().equals(target)) {
                return true; // Kante existiert bereits
            }
        }

        return false; // Kante existiert nicht
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
