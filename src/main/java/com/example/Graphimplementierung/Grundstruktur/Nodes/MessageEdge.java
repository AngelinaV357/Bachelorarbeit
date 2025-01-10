package com.example.Graphimplementierung.Grundstruktur.Nodes;

public class MessageEdge extends Edge {

    public MessageEdge(String id, Node source, Node target, String condition) {
        super(id, source, target, condition);  // MessageFlow hat keine Bedingung
    }

    @Override
    public String toString() {
        return String.format("MessageEdge{source='%s', target='%s'}", getSource().getName(), getTarget().getName());
    }
}
