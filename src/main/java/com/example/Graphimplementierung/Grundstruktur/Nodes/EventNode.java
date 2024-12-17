package com.example.Graphimplementierung.Grundstruktur.Nodes;

import com.example.Graphimplementierung.Grundstruktur.Lane;

public class EventNode extends Node {
    private final String eventType;

    public EventNode(String id, String name, Lane lane, String eventType) {
        super(id, "Event", name, lane); // Typ "Event" wird festgelegt
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return String.format("EventNode{id='%s', name='%s', eventType='%s', lane='%s'}",
                getId(), getName(), eventType,
                getLane() != null ? getLane().getName() : "None");
    }
}
