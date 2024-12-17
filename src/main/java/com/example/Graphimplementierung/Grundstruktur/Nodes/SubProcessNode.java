package com.example.Graphimplementierung.Grundstruktur.Nodes;

import com.example.Graphimplementierung.Grundstruktur.Lane;

public class SubProcessNode extends Node {
    public SubProcessNode(String id, String name, Lane lane) {
        super(id, "SubProcess", name, lane); // Typ "SubProcess" explizit setzen
    }

    @Override
    public String toString() {
        return String.format("SubProcessNode{id='%s', type='%s', name='%s', lane='%s'}",
                getId(), getType(), getName(), getLane() != null ? getLane().getName() : "None");
    }
}
