package com.example.Graphimplementierung.Grundstruktur;

public abstract class Node {
    private final String id;  // final, da ID sich nicht ändern sollte
    private final String type;
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
        return type;
    }

    public String getName() {
        return name;
    }

    public Lane getLane() {
        return lane;
    }

    // Setter für Lane (optional, falls Lane später geändert werden soll)
    public void setLane(Lane lane) {
        this.lane = lane;
    }

    @Override
    public String toString() {
        return String.format("Node{id='%s', type='%s', name='%s', lane='%s'}",
                id, type, name, lane != null ? lane.getName() : "None");
    }
}
