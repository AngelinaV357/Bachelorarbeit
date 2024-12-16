package com.example.Graphimplementierung;

class DataObjectNode extends Node {
    private String name;

    public DataObjectNode(String id, String lane, String name) {
        super(id, "DataObject", lane);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
