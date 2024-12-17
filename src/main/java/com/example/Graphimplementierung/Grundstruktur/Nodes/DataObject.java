package com.example.Graphimplementierung.Grundstruktur.Nodes;

import com.example.Graphimplementierung.Grundstruktur.Lane;

public class DataObject extends Node {
    private String data; // Kann "Collection" oder "Single" sein

    public DataObject(String id, String name, Lane lane) {
        super(id, "DataObject", name, lane);  // Typ für DataObject setzen
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataObject{id='" + getId() + "', name='" + getName() + "', lane='" + getLane().getName() + "', data='" + data + "'}";
    }
}
