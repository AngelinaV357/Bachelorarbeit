package com.example.Graphimplementierung.Grundstruktur;

public class EndEventNode extends Node {
    public EndEventNode(String id, String name, Lane lane) {
        super(id, "EndEvent", name, lane); // Typ ist "EndEvent" und wird über die Basisklasse gesetzt
    }

    @Override
    public String toString() {
        return String.format("EndEventNode{id='%s', name='%s', lane='%s'}",
                getId(), getName(), getLane() != null ? getLane().getName() : "None");
    }
}
