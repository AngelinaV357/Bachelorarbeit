package com.example.Graphimplementierung.Grundstruktur;

class ActivityNode extends Node {
    private String activityType;

    public ActivityNode(String id, String name, Lane lane, String activityType) {
        super(id, "Activity", name, lane);
        this.activityType = activityType;
    }

    public String getActivityType() {
        return activityType;
    }

    @Override
    public String toString() {
        return "ActivityNode{id='" + getId() + "', name='" + getName() + "', lane='" + getLane().getName() + "', activityType='" + activityType + "'}";
    }
}

