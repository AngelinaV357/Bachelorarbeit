package com.example.Graphimplementierung;

class GatewayNode extends Node {
    private String gatewayType;

    public GatewayNode(String id, String lane, String gatewayType) {
        super(id, "Gateway", lane);
        this.gatewayType = gatewayType;
    }

    public String getGatewayType() {
        return gatewayType;
    }
}
