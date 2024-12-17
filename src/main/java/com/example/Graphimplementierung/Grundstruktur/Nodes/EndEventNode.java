package com.example.Graphimplementierung.Grundstruktur.Nodes;

import com.example.Graphimplementierung.Grundstruktur.Lane;

public class EndEventNode extends Node {
    public EndEventNode(String id, String name, Lane lane) {
        super(id, "EndEvent", name != null && !name.isEmpty() ? name : "End", lane); // Standardname "End" verwenden
    }

    @Override
    public String toString() {
        return String.format("EndEventNode{id='%s', name='%s', lane='%s'}",
                getId(), getName(), getLane() != null ? getLane().getName() : "None");
    }
}
