package com.example.Graphimplementierung.Grundstruktur.Nodes;

public class EndEventNode extends Node {
    private boolean isEscalation; // neues Attribut für Eskalation

    public EndEventNode(String id, String name, Lane lane, boolean isEscalation) {
        super(id, "EndEvent", name, lane);
        this.isEscalation = isEscalation;
    }

    public boolean isEscalation() {
        return isEscalation;
    }

    @Override
    public String toString() {
        return String.format("EndEventNode{id='%s', name='%s', lane='%s', isEscalation='%s'}",
                getId(), getName(), getLane() != null ? getLane().getName() : "None", isEscalation ? "Yes" : "No");
    }
}
