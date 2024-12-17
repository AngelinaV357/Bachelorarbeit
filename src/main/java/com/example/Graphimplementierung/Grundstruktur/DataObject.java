package com.example.Graphimplementierung.Grundstruktur;

class DataObject extends Node {
    private String data;

    public DataObject(String id, String type, String name, Lane lane) {
        super(id, type, name, lane);
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "DataObjectNode{id='" + getId() + "', name='" + getName() + "', lane='" + getLane().getName() + "', data='" + data + "'}";
    }
}