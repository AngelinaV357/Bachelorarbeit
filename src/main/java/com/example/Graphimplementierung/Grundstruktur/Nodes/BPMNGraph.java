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
            throw new IllegalArgumentException("Node with ID " + node.getId() + " already exists.");
        }
        nodes.put(node.getId(), node);
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

    // Knoten nach Lane filtern
    public List<Node> getNodesByLane(Lane lane) {
        return nodes.values().stream() // Map.values() liefert Collection<Node>
                .filter(node -> lane.equals(node.getLane()))
                .collect(Collectors.toList());
    }

    // Kanten mit Bedingungen filtern
    public List<Edge> getEdgesWithConditions() {
        return edges.stream()
                .filter(Edge::hasCondition) // Methode hasCondition wird erwartet
                .collect(Collectors.toList());
    }
}
