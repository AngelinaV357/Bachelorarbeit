package com.example.Graphimplementierung.Grundstruktur.Nodes;

import com.example.Graphimplementierung.Grundstruktur.Lane;

public class StartEventNode extends Node {
    public StartEventNode(String id, String name, Lane lane) {
        super(id, "StartEvent", name != null && !name.isEmpty() ? name : "Start", lane); // Standardname "Start" verwenden
    }

    @Override
    public String toString() {
        return String.format("StartEventNode{id='%s', name='%s', lane='%s'}",
                getId(), getName(), getLane() != null ? getLane().getName() : "None");
    }
}
