package com.example.Graphimplementierung.Grundstruktur.Nodes;

public class StartEventNode extends Node {
    public StartEventNode(String id, String name, Lane lane) {
        super(id, "StartEvent", name, lane);
    }

    @Override
    public String toString() {
        return String.format("StartEventNode{id='%s', name='%s', lane='%s'}",
                getId(), getName(), getLane() != null ? getLane().getName() : "None");
    }
}
