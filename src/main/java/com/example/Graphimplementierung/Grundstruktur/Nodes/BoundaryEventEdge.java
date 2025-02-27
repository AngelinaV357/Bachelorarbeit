package com.example.Graphimplementierung.Grundstruktur.Nodes;

public class BoundaryEventEdge extends Edge {
    private final String eventType;  // Typ des Boundary Events (z. B. "Escalation", "Error")

    public BoundaryEventEdge(String id, Node source, Node target, String eventType, String condition) {
        super(id, source, target, condition);
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return String.format("BoundaryEventEdge{source='%s', target='%s', eventType='%s', condition='%s'}",
                getSource().getName(), getTarget().getName(), eventType, getCondition());
    }
}
