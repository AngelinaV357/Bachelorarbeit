package com.example.Graphimplementierung.Grundstruktur.Nodes;

public class Edge {
    private String id;
    private Node source;
    private Node target;
    private String condition;  // Optional, für Bedingungen an den Kanten

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

    public boolean hasCondition() {
        return condition != null && !condition.isEmpty();
    }

    @Override
    public String toString() {
        // Standard Edge Ausgabe, ohne Bedingung, wenn sie null oder leer ist
        if (hasCondition()) {
            return String.format("Edge{source='%s', target='%s', condition='%s'}",
                    source.getName(), target.getName(), condition);
        } else {
            return String.format("Edge{source='%s', target='%s'}",
                    source.getName(), target.getName());
        }
    }
}
