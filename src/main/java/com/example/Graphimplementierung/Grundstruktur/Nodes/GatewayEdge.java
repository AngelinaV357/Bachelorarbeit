package com.example.Graphimplementierung.Grundstruktur.Nodes;

public class GatewayEdge extends Edge {
    public GatewayEdge(String id, Node source, Node target, String condition) {
        super(id, source, target, condition);
    }

    @Override
    public String toString() {
        // GatewayEdge Ausgabe mit Bedingung, auch wenn sie null oder leer ist
        return String.format("GatewayEdge{source='%s', target='%s', condition='%s'}",
                getSource().getName(), getTarget().getName(),
                getCondition() != null ? getCondition() : "None");
    }
}
