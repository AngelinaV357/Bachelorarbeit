package com.example.Graphimplementierung;

// Klasse für eine Kante im Graph
class Edge {
    private String id;
    private Node source;
    private Node target;
    private String condition; // Optionale Bedingung

    public Edge(String id, Node source, Node target, String condition) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.condition = condition;
    }

    public String getId() {
        return id;
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public String getCondition() {
        return condition;
    }
}
