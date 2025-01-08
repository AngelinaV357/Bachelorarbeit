package com.example.Graphimplementierung.Grundstruktur.Nodes;

public class TaskNode extends Node {
    private String activityType;  // Der Typ des Knotens (z.B. "Task", "SubProcess", etc.)

    public TaskNode(String id, String name, Lane lane, String activityType) {
        super(id, "Activity", name, lane);  // Der Typ für ActivityNode bleibt immer "Activity"
        this.activityType = activityType;  // Setze den Typ (z.B. "Task", "SubProcess")
    }

    public String getActivityType() {
        return activityType;
    }

    @Override
    public String toString() {
        return "TaskNode{id='" + getId() + "', name='" + getName() + "', lane='" + getLane().getName() + "', activityType='" + activityType + "'}";
    }
}
