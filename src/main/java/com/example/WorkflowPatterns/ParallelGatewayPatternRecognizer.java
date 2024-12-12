package com.example.WorkflowPatterns;

import java.util.*;

public class ParallelGatewayPatternRecognizer {

    // Methode: Parallel-Gateway-Patterns erkennen
    public List<List<String>> detectParallelGatewayPatterns(Map<String, List<String>> graph, Map<String, String> nodeTypes, Set<String> processedNodes) {
        List<List<String>> parallelPatterns = new ArrayList<>();
        int parallelCount = 0;  // Zähler für erkannte Parallel-Gateways

        // Iteriere durch alle Knoten im Graphen
        for (String node : graph.keySet()) {
            // Prüfe, ob der Knoten bereits abgehakt ist
            if (processedNodes.contains(node)) {
                continue; // Überspringe den Knoten, wenn er bereits verarbeitet wurde
            }

            // Prüfe, ob der Knoten ein Parallel-Gateway ist
            if ("parallelGateway".equalsIgnoreCase(nodeTypes.get(node))) {
                // Hole eingehende und ausgehende Kanten
                List<String> incoming = findIncomingEdges(graph, node);
                List<String> outgoing = graph.get(node);

                // Überprüfe die Pattern-Bedingungen
                if (incoming.size() >= 1 && outgoing.size() >= 2) {
                    // Speichere das Pattern
                    List<String> pattern = new ArrayList<>();
                    pattern.add(node); // Parallel-Gateway
                    pattern.addAll(incoming); // Eingehende Knoten
                    pattern.addAll(outgoing); // Ausgehende Knoten
                    parallelPatterns.add(pattern);
                    parallelCount++;  // Inkrementiere den Zähler

                    // Markiere alle beteiligten Knoten als abgehakt
                    processedNodes.add(node); // Parallel-Gateway als abgehakt markieren
                    processedNodes.addAll(incoming); // Eingehende Knoten als abgehakt markieren
                    processedNodes.addAll(outgoing); // Ausgehende Knoten als abgehakt markieren
                }
            }
        }

        // Ausgabe der Anzahl der erkannten Parallel-Gateways
        System.out.println("Anzahl der erkannten Parallel-Gateways: " + parallelCount);
        return parallelPatterns;
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

    // Methode zur Generierung der SBVR-Regel für Parallel-Gateways
    public String generateSBVRRuleForParallelGateway(List<List<String>> parallelPatterns, Map<String, String> nodeNames, Map<String, String> nodeTypes, Map<String, String> conditions) {
        StringBuilder sbvrRule = new StringBuilder();

        for (List<String> pattern : parallelPatterns) {
            String parallelGateway = pattern.get(0);  // Parallel-Gateway
            List<String> incomingNodes = pattern.subList(1, pattern.size() / 2);  // Eingehende Knoten
            List<String> outgoingNodes = pattern.subList(pattern.size() / 2, pattern.size());  // Ausgehende Knoten

            sbvrRule.append("* SBVR-Regel für Parallel-Gateway: ").append(nodeNames.get(parallelGateway)).append(" *\n");

            // Unterscheidung zwischen Diverging und Converging Parallel-Gateway
            if (isDiverging(parallelGateway, nodeTypes)) {
                sbvrRule.append("Es ist notwendig, dass nach der AKTIVITÄT ").append(getNodeNames(incomingNodes.get(0), nodeNames))
                        .append(" die AKTIVITÄT ").append(getNodeNames(outgoingNodes.get(0), nodeNames))
                        .append(" ausgeführt wird, wenn die Bedingung '").append(conditions.get(incomingNodes.get(0) + "->" + outgoingNodes.get(0)))
                        .append("' für den Sequenzfluss erfüllt ist.\n");

                // Falls mehrere ausgehende Knoten vorhanden sind, zusätzliche Regeln erstellen
                for (int i = 1; i < outgoingNodes.size(); i++) {
                    sbvrRule.append("Es ist notwendig, dass nach der AKTIVITÄT ").append(getNodeNames(incomingNodes.get(0), nodeNames))
                            .append(" auch die AKTIVITÄT ").append(getNodeNames(outgoingNodes.get(i), nodeNames))
                            .append(" ausgeführt wird.\n");
                }

            } else if (isConverging(parallelGateway, nodeTypes)) {
                sbvrRule.append("Es ist notwendig, dass nach dem AKTIVITÄT ").append(getNodeNames(parallelGateway, nodeNames))
                        .append(" die folgenden Aktivitäten erfolgen: \n");

                for (String outgoingNode : outgoingNodes) {
                    sbvrRule.append(getNodeNames(outgoingNode, nodeNames)).append(" ");
                }
                sbvrRule.append("nach den folgenden Bedingungen:\n");

                for (String incomingNode : incomingNodes) {
                    sbvrRule.append("Wenn: ").append(getNodeNames(incomingNode, nodeNames)).append("\n");
                }
            }
            sbvrRule.append("\n");
        }

        return sbvrRule.toString();
    }


    // Methode zur Überprüfung, ob es sich um ein divergierendes Parallel-Gateway handelt
    private boolean isDiverging(String parallelGateway, Map<String, String> nodeTypes) {
        // Hier könnte eine spezifische Logik implementiert werden, z.B. anhand der Anzahl der ausgehenden Kanten
        return nodeTypes.get(parallelGateway).equalsIgnoreCase("diverging");
    }

    // Methode zur Überprüfung, ob es sich um ein konvergierendes Parallel-Gateway handelt
    private boolean isConverging(String parallelGateway, Map<String, String> nodeTypes) {
        // Hier könnte eine spezifische Logik implementiert werden, z.B. anhand der Anzahl der eingehenden Kanten
        return nodeTypes.get(parallelGateway).equalsIgnoreCase("converging");
    }

    // Hilfsmethode: Holt den Namen eines Knotens, falls vorhanden
    private String getNodeNames(String nodeId, Map<String, String> nodeNames) {
        return nodeNames.getOrDefault(nodeId, "Unbenannter Knoten");
    }
}
