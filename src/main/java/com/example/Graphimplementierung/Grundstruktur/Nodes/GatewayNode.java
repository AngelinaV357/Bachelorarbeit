package com.example.Graphimplementierung.Grundstruktur.Nodes;

import com.example.Graphimplementierung.Grundstruktur.Lane;
import com.example.Graphimplementierung.Grundstruktur.Edge;
import java.util.ArrayList;
import java.util.List;

public class GatewayNode extends Node {

    private String gatewayType; // Typ des Gateways (z.B. XOR, AND, etc.)
    private List<Edge> outgoingEdges; // Liste der ausgehenden Kanten von diesem Gateway

    public GatewayNode(String id, String name, Lane lane, String gatewayType) {
        super(id, "Gateway", name != null ? name : "Merge Gateway", lane);  // Standardname "Merge Gateway" falls leer
        this.gatewayType = gatewayType;
        this.outgoingEdges = new ArrayList<>();
    }

    // Getter für den Gateway-Typ
    public String getGatewayType() {
        return gatewayType;
    }

    // Setter für den Gateway-Typ
    public void setGatewayType(String gatewayType) {
        this.gatewayType = gatewayType;
    }

    // Füge eine ausgehende Kante hinzu
    public void addOutgoingEdge(Edge edge) {
        this.outgoingEdges.add(edge);
    }

    // Gib die ausgehenden Kanten zurück
    public List<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }

    @Override
    public String toString() {
        return String.format("GatewayNode{id='%s', name='%s', gatewayType='%s', lane='%s', outgoingEdges=%s}",
                getId(), getName(), gatewayType, getLane() != null ? getLane().getName() : "None", outgoingEdges);
    }
}
