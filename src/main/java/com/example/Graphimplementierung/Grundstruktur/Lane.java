package com.example.Graphimplementierung.Grundstruktur;

public class Lane {
    private final String id;
    private final String name;

    public Lane(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("Lane{id='%s', name='%s'}", id, name);
    }
}
