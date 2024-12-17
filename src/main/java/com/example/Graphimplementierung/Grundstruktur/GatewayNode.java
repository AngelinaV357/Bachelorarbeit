package com.example.Graphimplementierung.Grundstruktur;

public class GatewayNode extends Node {
    private String gatewayType;

    public GatewayNode(String id, String name, Lane lane, String gatewayType) {
        super(id, "Gateway", name != null ? name : "Merge Gateway", lane);  // Standardname "Merge Gateway" falls leer
        this.gatewayType = gatewayType;
    }

    public String getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(String gatewayType) {
        this.gatewayType = gatewayType;
    }

    @Override
    public String toString() {
        return String.format("GatewayNode{id='%s', name='%s', gatewayType='%s', lane='%s'}",
                getId(), getName(), gatewayType,
                getLane() != null ? getLane().getName() : "None");
    }
}

