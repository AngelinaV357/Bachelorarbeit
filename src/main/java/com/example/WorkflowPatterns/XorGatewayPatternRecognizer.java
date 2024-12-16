package com.example.WorkflowPatterns;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

public class XorGatewayPatternRecognizer {

    // Methode: XOR-Gateway-Patterns erkennen
    public List<List<String>> detectXorGatewayPatterns(Graph<String, DefaultEdge> graph, Map<String, String> nodeTypes, Set<String> processedNodes) {
        List<List<String>> xorPatterns = new ArrayList<>();
        int xorCount = 0;  // Zähler für erkannte XOR-Gateways

        // Iteriere direkt über die Knoten im Graphen
        for (String node : graph.vertexSet()) {
            // Prüfe, ob der Knoten bereits abgehakt ist
            if (processedNodes.contains(node)) {
                continue; // Überspringe den Knoten, wenn er bereits verarbeitet wurde
            }

            // Prüfe, ob der Knoten ein XOR-Gateway ist
            if ("exclusiveGateway".equalsIgnoreCase(nodeTypes.get(node))) {
                // Hole eingehende und ausgehende Kanten
                List<String> incoming = findIncomingEdges(graph, node);
                List<String> outgoing = getOutgoingEdges(graph, node);

                // Erkennung von diverging XOR-Gateway
                if (incoming.size() == 1 && outgoing.size() >= 2) {
                    List<String> pattern = new ArrayList<>();
                    pattern.add(node); // XOR-Gateway
                    pattern.add(incoming.get(0)); // Eingehender Knoten
                    pattern.addAll(outgoing); // Ausgehende Knoten
                    xorPatterns.add(pattern);
                    xorCount++;
                }

                // Erkennung von converging XOR-Gateway
                if (incoming.size() >= 2 && outgoing.size() == 1) {
                    List<String> pattern = new ArrayList<>();
                    pattern.add(node); // XOR-Gateway
                    pattern.addAll(incoming); // Eingehende Knoten
                    pattern.add(outgoing.get(0)); // Ausgehender Knoten
                    xorPatterns.add(pattern);
                    xorCount++;
                }
            }
        }

        System.out.println("Anzahl der erkannten XOR-Gateways: " + xorCount); // Ausgabe der Anzahl
        return xorPatterns;
    }

    // Methode: SBVR-Regel für XOR-Gateways erstellen
    public String generateSBVRRule(List<String> pattern, Map<String, String> nodeNames, Map<String, String> conditions) {
        StringBuilder sbvrRule = new StringBuilder();

        // Zuerst alle Aktivitäten und deren Kanten ausgeben
        sbvrRule.append("Aktivitäten und Kanten:\n");
        for (String nodeName : nodeNames.keySet()) {
            sbvrRule.append(nodeNames.get(nodeName)).append("\n");
        }

        sbvrRule.append("Kanten:\n");
        for (Map.Entry<String, String> entry : conditions.entrySet()) {
            sbvrRule.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
        }

        // Überprüfen, ob das XOR Gateway ein diverging oder converging ist
        if (pattern.size() > 3) { // Diverging XOR-Gateway
            String incomingNode = nodeNames.get(pattern.get(1));
            List<String> outgoingNodes = pattern.subList(2, pattern.size());
            sbvrRule.append("Dies ist ein diverging XOR-Gateway.\n");

            for (String outgoingNode : outgoingNodes) {
                String outgoingName = nodeNames.get(outgoingNode);
                boolean hasCondition = hasConditionForFlow(incomingNode, outgoingNode, conditions);
                String condition = conditions.get(incomingNode + "->" + outgoingNode);

                if (hasCondition) {
                    sbvrRule.append("Es ist obligatorisch, dass die Aktivität ").append(outgoingName)
                            .append(" nach der Aktivität ").append(incomingNode)
                            .append(" ausgeführt wird, wenn die Bedingung '").append(condition).append("' erfüllt ist.\n");
                } else {
                    sbvrRule.append("Es ist erlaubt, dass die Aktivität ").append(outgoingName)
                            .append(" nach der Aktivität ").append(incomingNode).append(" ausgeführt wird.\n");
                }
            }
        } else { // Converging XOR-Gateway
            List<String> incomingNodes = pattern.subList(1, pattern.size() - 1);
            String outgoingNode = nodeNames.get(pattern.get(pattern.size() - 1));
            sbvrRule.append("Dies ist ein converging XOR-Gateway.\n");

            for (String incomingNode : incomingNodes) {
                String incomingName = nodeNames.get(incomingNode);
                boolean hasCondition = hasConditionForFlow(incomingNode, outgoingNode, conditions);
                String condition = conditions.get(incomingNode + "->" + outgoingNode);

                if (hasCondition) {
                    sbvrRule.append("Es ist obligatorisch, dass die Aktivität ").append(outgoingNode)
                            .append(" nach der Aktivität ").append(incomingName)
                            .append(" ausgeführt wird, wenn die Bedingung '").append(condition).append("' erfüllt ist.\n");
                } else {
                    sbvrRule.append("Es ist erlaubt, dass die Aktivität ").append(outgoingNode)
                            .append(" nach der Aktivität ").append(incomingName).append(" ausgeführt wird.\n");
                }
            }
        }

        return sbvrRule.toString();
    }

    private boolean hasConditionForFlow(String incomingNode, String outgoingNode, Map<String, String> conditions) {
        return conditions.containsKey(incomingNode + "->" + outgoingNode);
    }


    // Hilfsfunktion: Finde eingehende Kanten für einen Knoten
    private List<String> findIncomingEdges(Graph<String, DefaultEdge> graph, String targetNode) {
        List<String> incomingEdges = new ArrayList<>();
        for (String sourceNode : graph.vertexSet()) {
            if (graph.containsEdge(sourceNode, targetNode)) {
                incomingEdges.add(sourceNode);
            }
        }
        return incomingEdges;
    }

    // Hilfsfunktion: Finde ausgehende Kanten für einen Knoten
    private List<String> getOutgoingEdges(Graph<String, DefaultEdge> graph, String node) {
        List<String> outgoingEdges = new ArrayList<>();
        for (DefaultEdge edge : graph.outgoingEdgesOf(node)) {
            outgoingEdges.add(graph.getEdgeTarget(edge));
        }
        return outgoingEdges;
    }


}
