package com.example.Graphimplementierung;

class ActivityNode extends Node {
    private String activityType;

    public ActivityNode(String id, String lane, String activityType) {
        super(id, "Activity", lane);
        this.activityType = activityType;
    }

    public String getActivityType() {
        return activityType;
    }
}
