package com.example.Graphimplementierung.Grundstruktur.Nodes;

public class GatewayNode extends Node {
    private final String gatewayType; // Typ des Gateways (z.B. Parallel, Exclusive)

    public GatewayNode(String id, String name, Lane lane, String gatewayType) {
        super(id, "Gateway", name, lane);
        this.gatewayType = gatewayType;
    }

    public String getGatewayType() {
        return gatewayType;
    }

    @Override
    public String toString() {
        return String.format("GatewayNode{id='%s', name='%s', lane='%s', gatewayType='%s'}",
                getId(), getName(), getLane() != null ? getLane().getName() : "None", gatewayType);
    }
}
