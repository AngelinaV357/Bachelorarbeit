package com.example.Graphimplementierung.Grundstruktur.Nodes;

// BoundaryEventNode erbt von Node und fügt spezielle Eigenschaften hinzu
public class BoundaryEventNode extends Node {
    private String condition;  // Bedingung für das Boundary Event, z.B. "Delivery Problems"

    public BoundaryEventNode(String id, String name, Lane lane) {
        super(id, "BoundaryEvent", name, lane);  // "BoundaryEvent" als Typ
        this.condition = condition;  // Bedingung für das Event
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return String.format("BoundaryEventNode{id='%s', name='%s', lane='%s'}",
                getId(), getName(), getLane() != null ? getLane().getName() : "None");
    }
}
