//package com.example.Graphimplementierung.Workflowpatterns;
//
//import com.example.Graphimplementierung.Grundstruktur.Nodes.GatewayNode;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class XORGatewayPattern {
//
//    private String gatewayId;
//    private String gatewayName;
//    private Map<String, String> outgoingFlows;  // Mapping von Ziel-IDs zu Bedingungen
//
//    public XORGatewayPattern(String gatewayId, String gatewayName) {
//        this.gatewayId = gatewayId;
//        this.gatewayName = gatewayName;
//        this.outgoingFlows = new HashMap<>();
//    }
//
//    // Füge einen ausgehenden Flow hinzu
//    public void addOutgoingFlow(String targetId, String condition) {
//        outgoingFlows.put(targetId, condition);
//    }
//
//    public String getGatewayId() {
//        return gatewayId;
//    }
//
//    public String getGatewayName() {
//        return gatewayName;
//    }
//
//    public Map<String, String> getOutgoingFlows() {
//        return outgoingFlows;
//    }
//
//    @Override
//    public String toString() {
//        return String.format("XORGatewayPattern{id='%s', name='%s', outgoingFlows=%s}",
//                gatewayId, gatewayName, outgoingFlows);
//    }
//
//    // Erkennung von XOR-Gateways basierend auf den ausgehenden Bedingungen
//    public static boolean isXORGateway(Map<String, String> flows) {
//        // XOR-Pattern: Es gibt mehrere ausgehende Flows, aber jede hat eine eindeutige Bedingung
//        return flows.size() > 1 && flows.values().stream().distinct().count() == flows.size();
//    }
//}
//
