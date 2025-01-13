package com.example.Graphimplementierung.Grundstruktur.Nodes;

public class IntermediateNode extends Node {
    private final String eventType;    // Event-Typ (Catch oder Throw)
    private final String eventSubType; // Sub-Typ (Timer, Message, etc.)

    // Konstruktor für IntermediateNode
    public IntermediateNode(String id, String name, Lane lane, String eventType, String eventSubType) {
        // Aufruf des Konstruktors der übergeordneten Node-Klasse
        super(id, "IntermediateEvent", name, lane); // Typ ist immer "IntermediateEvent"
        this.eventType = eventType;       // Setze den Event-Typ (Catch oder Throw)
        this.eventSubType = eventSubType; // Setze den Sub-Typ (Timer oder Message)
    }

    // Getter für den Event-Typ
    public String getEventType() {
        return eventType;
    }

    // Getter für den Event-Subtyp
    public String getEventSubType() {
        return eventSubType;
    }

    @Override
    public String toString() {
        return String.format("IntermediateNode{id='%s', name='%s', eventType='%s', eventSubType='%s', lane='%s'}",
                getId(), getName(), eventType, eventSubType, getLane() != null ? getLane().getName() : "None");
    }
}
