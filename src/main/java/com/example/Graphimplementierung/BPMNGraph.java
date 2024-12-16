package com.example.Graphimplementierung;

import java.util.*;

// Hauptklasse zur Repräsentation eines BPMN-Diagramms als Graph
public class BPMNGraph {
    private List<Node> nodes; // Liste von Knoten im Graph
    private List<Edge> edges; // Liste von Kanten im Graph

    public BPMNGraph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}