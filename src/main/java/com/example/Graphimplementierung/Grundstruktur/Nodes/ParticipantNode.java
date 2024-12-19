package com.example.Graphimplementierung.Grundstruktur.Nodes;

public class ParticipantNode extends Node {
    public ParticipantNode(String id, String name, Lane lane) {
        super(id, "Participant", name, lane);  // Setzt den Typ auf "Participant"
    }

    @Override
    public String toString() {
        return String.format("ParticipantNode{id='%s', name='%s', lane='%s', type='%s'}",
                getId(), getName(), getLane() != null ? getLane().getName() : "None", getType());
    }
}
