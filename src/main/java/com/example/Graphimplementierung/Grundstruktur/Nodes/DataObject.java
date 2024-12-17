package com.example.Graphimplementierung.Grundstruktur.Nodes;

import com.example.Graphimplementierung.Grundstruktur.Lane;

public class DataObject extends Node {
    private String data;

    public DataObject(String id, String name, Lane lane) {
        super(id, "DataObject", name, lane);  // Typ für DataObject setzen
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "DataObjectNode{id='" + getId() + "', name='" + getName() + "', lane='" + getLane().getName() + "', data='" + data + "'}";
    }
}
