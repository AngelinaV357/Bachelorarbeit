package com.example.WorkflowPatterns;

import java.util.*;

public class XorGatewayPatternRecognizer {

    // Methode: XOR-Gateway-Patterns erkennen
    public List<List<String>> detectXorGatewayPatterns(Map<String, List<String>> graph, Map<String, String> nodeTypes, Set<String> processedNodes) {
        List<List<String>> xorPatterns = new ArrayList<>();
        int xorCount = 0;  // Zähler für erkannte XOR-Gateways

        // Iteriere durch alle Knoten im Graphen
        for (String node : graph.keySet()) {
            // Prüfe, ob der Knoten bereits abgehakt ist
            if (processedNodes.contains(node)) {
                continue; // Überspringe den Knoten, wenn er bereits verarbeitet wurde
            }

            // Prüfe, ob der Knoten ein XOR-Gateway ist
            if ("exclusiveGateway".equalsIgnoreCase(nodeTypes.get(node))) {
                // Hole eingehende und ausgehende Kanten
                List<String> incoming = findIncomingEdges(graph, node);
                List<String> outgoing = graph.get(node);

                // Überprüfe die Pattern-Bedingungen
                if (incoming.size() == 1 && outgoing.size() >= 2) {
                    // Speichere das Pattern
                    List<String> pattern = new ArrayList<>();
                    pattern.add(node); // XOR-Gateway
                    pattern.add(incoming.get(0)); // Eingehender Knoten
                    pattern.addAll(outgoing); // Ausgehende Knoten
                    xorPatterns.add(pattern);
                    xorCount++;  // Inkrementiere den Zähler

                    // Markiere alle beteiligten Knoten als abgehakt
                    processedNodes.add(node); // XOR-Gateway als abgehakt markieren
                    processedNodes.add(incoming.get(0)); // Eingehenden Knoten als abgehakt markieren
                    processedNodes.addAll(outgoing); // Ausgehende Knoten als abgehakt markieren
                }
            }
        }

        System.out.println("Anzahl der erkannten XOR-Gateways: " + xorCount); // Ausgabe der Anzahl
        return xorPatterns;
    }

    // Methode: SBVR-Regel für XOR-Gateways erstellen
    public String generateSbvrRuleForXor(List<String> pattern, Map<String, String> nodeNames, Map<String, String> conditions) {
        String xorGateway = nodeNames.get(pattern.get(0));  // XOR-Gateway
        String incomingNode = nodeNames.get(pattern.get(1));  // Eingehender Knoten
        List<String> outgoingNodes = pattern.subList(2, pattern.size());  // Ausgehende Knoten

        StringBuilder sbvrRule = new StringBuilder();
        sbvrRule.append("* SBVR-Regel für XOR-Gateway: ").append(xorGateway).append(" *\n");

        // Bestimme, ob es ein diverging oder converging XOR-Gateway ist
        if (outgoingNodes.size() > 1) {
            sbvrRule.append("Dies ist ein diverging XOR-Gateway.\n");
        } else {
            sbvrRule.append("Dies ist ein converging XOR-Gateway.\n");
        }

        // Wiederhole für alle ausgehenden Sequenzflüsse
        for (String outgoingNode : outgoingNodes) {
            String outgoingName = nodeNames.get(outgoingNode);  // Name des ausgehenden Knotens
            boolean hasCondition = hasConditionForFlow(incomingNode, outgoingNode, conditions);  // Bedingung für Sequenzfluss
            String condition = conditions.get(incomingNode + "->" + outgoingNode);  // Die Bedingung für diesen Sequenzfluss (sofern vorhanden)

            if (hasCondition) {
                // Bedingter Sequenzfluss (obligatorisch)
                sbvrRule.append("Es ist obligatorisch, dass ")
                        .append("die Aktivität ").append(outgoingName)
                        .append(" nach der Aktivität ").append(incomingNode)
                        .append(" ausgeführt wird, wenn die Bedingung '").append(condition)
                        .append("' für den Sequenzfluss erfüllt ist.")
                        .append("\n");
            } else {
                // Bedingungslos (erlaubt)
                sbvrRule.append("Es ist erlaubt, dass ")
                        .append("die Aktivität ").append(outgoingName)
                        .append(" nach der Aktivität ").append(incomingNode)
                        .append(" ausgeführt wird.")
                        .append("\n");
            }
        }

        return sbvrRule.toString();
    }

    // Prüft, ob ein Sequenzfluss zwischen zwei Knoten eine Bedingung hat
    private boolean hasConditionForFlow(String sourceNode, String targetNode, Map<String, String> conditions) {
        String flowId = sourceNode + "->" + targetNode;
        return conditions.containsKey(flowId);  // Überprüft, ob eine Bedingung existiert
    }

    // Hilfsfunktion: Finde eingehende Kanten für einen Knoten
    private List<String> findIncomingEdges(Map<String, List<String>> graph, String targetNode) {
        List<String> incomingEdges = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            if (entry.getValue().contains(targetNode)) {
                incomingEdges.add(entry.getKey());
            }
        }
        return incomingEdges;
    }
}
