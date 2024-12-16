package com.example.Graphimplementierung;

class EventNode extends Node {
    private String eventType;

    public EventNode(String id, String lane, String eventType) {
        super(id, "Event", lane);
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }
}

