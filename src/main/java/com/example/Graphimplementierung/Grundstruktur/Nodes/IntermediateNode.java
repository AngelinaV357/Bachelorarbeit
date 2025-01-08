package com.example.Graphimplementierung.Grundstruktur.Nodes;

public class IntermediateNode extends Node {
    private final String eventType;  // Event-Typ (Catch oder Throw)

    // Konstruktor für IntermediateNode
    public IntermediateNode(String id, String name, Lane lane, String eventType) {
        // Aufruf des Konstruktors der übergeordneten Node-Klasse
        super(id, "IntermediateEvent", name, lane); // Typ ist immer "IntermediateEvent"
        this.eventType = eventType; // Setze den Event-Typ (Catch oder Throw)
    }

    // Getter für den Event-Typ
    public String getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return String.format("IntermediateNode{id='%s', name='%s', eventType='%s', lane='%s'}",
                getId(), getName(), eventType, getLane() != null ? getLane().getName() : "None");
    }
}
