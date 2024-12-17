package com.example.Graphimplementierung.Grundstruktur.Nodes;

import com.example.Graphimplementierung.Grundstruktur.Lane;

public class ActivityNode extends Node {
    private String activityType;  // Der Typ des Knotens (z.B. "Task", "SubProcess", etc.)

    public ActivityNode(String id, String name, Lane lane, String activityType) {
        super(id, "Activity", name, lane);  // Der Typ für ActivityNode bleibt immer "Activity"
        this.activityType = activityType;  // Setze den Typ (z.B. "Task", "SubProcess")
    }

    public String getActivityType() {
        return activityType;
    }

    @Override
    public String toString() {
        return "ActivityNode{id='" + getId() + "', name='" + getName() + "', lane='" + getLane().getName() + "', activityType='" + activityType + "'}";
    }
}
