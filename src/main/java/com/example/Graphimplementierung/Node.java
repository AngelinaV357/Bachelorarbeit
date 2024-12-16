package com.example.Graphimplementierung;

import java.util.HashMap;
import java.util.Map;

// Basisklasse für alle Knoten im Graph
abstract class Node {
    private String id;
    private String type;
    private String lane; // Optional Lane-Zugehörigkeit
    private Map<String, Object> attributes; // Zusätzliche Attribute

    public Node(String id, String type, String lane) {
        this.id = id;
        this.type = type;
        this.lane = lane;
        this.attributes = new HashMap<>();
        if (lane != null) {
            this.attributes.put("lane", lane);
        }
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getLane() {
        return lane;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
