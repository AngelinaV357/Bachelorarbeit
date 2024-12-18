package com.example.Graphimplementierung.Grundstruktur.Nodes;

public abstract class Node {
    private final String id;  // final, da ID sich nicht ändern sollte
    private final String type; // Typ der Node (z.B. Task, DataObject)
    private String name;
    private Lane lane;

    public Node(String id, String type, String name, Lane lane) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.lane = lane;
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getType() {
        return type;  // Rückgabe des Typs
    }

    public String getName() {
        return name;
    }

    public Lane getLane() {
        return lane;
    }

    // Setter für Lane (optional)
    public void setLane(Lane lane) {
        this.lane = lane;
    }

    @Override
    public String toString() {
        return String.format("Node{id='%s', type='%s', name='%s', lane='%s'}",
                id, type, name, lane != null ? lane.getName() : "None");
    }
}

