package com.example.Graphimplementierung.Grundstruktur.Nodes;

import com.example.Graphimplementierung.Grundstruktur.Lane;

public class ActivityNode extends Node {
    private String activityType;

    public ActivityNode(String id, String name, Lane lane, String activityType) {
        // Der Typ für ActivityNode ist "Task", da es sich um eine Aufgabe handelt
        super(id, "Task", name != null ? name : "Default Task", lane);
        this.activityType = activityType;
    }

    public String getActivityType() {
        return activityType;
    }

    @Override
    public String toString() {
        // Überprüfen, ob lane null ist (falls keine Lane zugeordnet wurde)
        return String.format("ActivityNode{id='%s', name='%s', lane='%s', activityType='%s'}",
                getId(),
                getName(),
                getLane() != null ? getLane().getName() : "None",  // Falls die Lane null ist, gebe "None" aus
                activityType);
    }

}


