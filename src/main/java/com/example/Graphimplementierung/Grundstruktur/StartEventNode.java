package com.example.Graphimplementierung.Grundstruktur;

public class StartEventNode extends Node {
    public StartEventNode(String id, String name, Lane lane) {
        super(id, "StartEvent", name, lane); // Typ ist "StartEvent" und wird über die Basisklasse gesetzt
    }

    @Override
    public String toString() {
        return String.format("StartEventNode{id='%s', name='%s', lane='%s'}",
                getId(), getName(), getLane() != null ? getLane().getName() : "None");
    }
}
