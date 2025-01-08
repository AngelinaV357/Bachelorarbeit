package com.example.Graphimplementierung.Grundstruktur.Nodes;

public class EndEventNode extends Node {
    public EndEventNode(String id, String name, Lane lane) {
        super(id, "EndEvent", name, lane);
    }

    @Override
    public String toString() {
        return String.format("EndEventNode{id='%s', name='%s', lane='%s'}",
                getId(), getName(), getLane() != null ? getLane().getName() : "None");
    }
}
