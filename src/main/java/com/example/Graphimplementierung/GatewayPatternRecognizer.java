//package com.example.Graphimplementierung;
//
//import com.example.Graphimplementierung.Grundstruktur.BPMNGraph;
//import com.example.Graphimplementierung.Grundstruktur.Nodes.GatewayNode;
//import com.example.Graphimplementierung.Grundstruktur.Edge;
//import com.example.Graphimplementierung.Grundstruktur.Nodes.Node;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class GatewayPatternRecognizer {
//
//    // Prüft, ob das Gateway ein XOR-Gateway ist
//    private static boolean isXORGateway(GatewayNode gatewayNode) {
//        List<Edge> outgoingEdges = gatewayNode.getOutgoingEdges();
//
//        // XOR-Pattern prüfen: Mehrere ausgehende Kanten mit unterschiedlichen Bedingungen
//        Set<String> conditions = new HashSet<>();
//        for (Edge edge : outgoingEdges) {
//            String condition = edge.getCondition();
//            conditions.add(condition);  // Alle unterschiedlichen Bedingungen sammeln
//        }
//
//        // XOR-Pattern: Mehrere ausgehende Kanten, aber jede mit einer unterschiedlichen Bedingung
//        return outgoingEdges.size() > 1 && conditions.size() == outgoingEdges.size();
//    }
//
//    // Methode, um Gateway-Patterns zu erkennen
//    public static void GatewayPatternRecognizer(BPMNGraph graph) {
//        for (Node node : graph.getNodes()) {
//            if (node instanceof GatewayNode) {
//                GatewayNode gatewayNode = (GatewayNode) node;
//
//                // Erkennung des XOR-Gateway-Patterns
//                if ("XOR".equals(gatewayNode.getGatewayType()) && isXORGateway(gatewayNode)) {
//                    System.out.println("XOR Gateway erkannt: " + gatewayNode);
//                }
//
//                // Weitere Gateway-Pattern wie AND und OR können hier hinzugefügt werden
//                // Beispiel: isANDGateway(gatewayNode) oder isORGateway(gatewayNode)
//            }
//        }
//    }
//}
