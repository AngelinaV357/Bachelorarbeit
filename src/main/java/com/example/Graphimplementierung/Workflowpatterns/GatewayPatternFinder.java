package com.example.Graphimplementierung.Workflowpatterns;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Edge;
import com.example.Graphimplementierung.Grundstruktur.Nodes.ActivityNode;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Node;

import java.util.HashSet;
import java.util.Set;

public class GatewayPatternFinder {

    // Set, um bereits ausgegebene Gateways nachzuverfolgen
    private Set<String> processedGateways = new HashSet<>();

    public void findExclusiveGatewayPatterns(BPMNGraph graph, StringBuilder sbvrData) {
        for (Node node : graph.getNodes()) {
            if (node instanceof GatewayNode && "Exclusive".equals(((GatewayNode) node).getGatewayType())) {
                GatewayNode gatewayNode = (GatewayNode) node;

                if (!processedGateways.contains(gatewayNode.getId())) {
                    String message = "\nExclusive Gateway gefunden: " + cleanText(gatewayNode.getName());
                    System.out.println(message);
                    sbvrData.append(message).append("\n");

                    generateSBVRRules(graph, gatewayNode, sbvrData);
                    processedGateways.add(gatewayNode.getId());
                }
            }
        }
    }

    public void findParallelGatewayPatterns(BPMNGraph graph, StringBuilder sbvrData) {
        for (Node node : graph.getNodes()) {
            if (node instanceof GatewayNode && "Parallel".equals(((GatewayNode) node).getGatewayType())) {
                GatewayNode gatewayNode = (GatewayNode) node;

                if (!processedGateways.contains(gatewayNode.getId())) {
                    String message = "\nParallel Gateway gefunden: " + cleanText(gatewayNode.getName());
                    System.out.println(message);
                    sbvrData.append(message).append("\n");

                    generateSBVRRules(graph, gatewayNode, sbvrData);
                    processedGateways.add(gatewayNode.getId());
                }
            }
        }
    }



    public void findEventBasedGatewayPatterns(BPMNGraph graph, StringBuilder sbvrData) {
        for (Node node : graph.getNodes()) {
            if (node instanceof GatewayNode && "EventBased".equals(((GatewayNode) node).getGatewayType())) {
                GatewayNode gatewayNode = (GatewayNode) node;

                if (!processedGateways.contains(gatewayNode.getId())) {
                    String message = "\nEvent-Based Gateway gefunden: " + cleanText(gatewayNode.getName());
                    System.out.println(message);
                    sbvrData.append(message).append("\n");

                    generateSBVRRules(graph, gatewayNode, sbvrData);
                    processedGateways.add(gatewayNode.getId());
                }
            }
        }
    }

    private void generateSBVRRules(BPMNGraph graph, GatewayNode gatewayNode, StringBuilder sbvrData) {
        String message = "SBVR-Regeln für " + cleanText(gatewayNode.getName()) + ":";
        System.out.println(message);
        sbvrData.append(message).append("\n");

        // Ausgehende Kanten: Erzeuge Regeln basierend auf ausgehenden Kanten mit Bedingung
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(gatewayNode)) {
                String condition = edge.getCondition();
                Node targetNode = edge.getTarget();

                // Finde die Quelle der Kante, um die vorherige Aktivität zu erhalten
                Node sourceNode = edge.getSource();
                String sourceActivityName = "";  // Initialisierung
                // Suche die eingehende Kante, um die Quelle der vorherigen Aktivität zu finden
                for (Edge incomingEdge : graph.getEdges()) {
                    if (incomingEdge.getTarget().equals(gatewayNode)) {
                        sourceNode = incomingEdge.getSource();
                        sourceActivityName = cleanText(sourceNode.getName());
                        break;
                    }
                }

                // Regel mit Bedingung erstellen
                if (condition != null && !condition.isEmpty()) {
                    message = "It is obligatory that " + cleanText(targetNode.getName()) +
                            " after " + sourceActivityName + " and if " + cleanText(condition) + "";
                } else {
                    message = "It is obligatory that " + cleanText(targetNode.getName()) +
                            " after " + sourceActivityName;
                }
                System.out.println(message);
                sbvrData.append(message).append("\n");
            }
        }
    }


    // Bereinigt den Text von unerwünschten Umbrüchen und Leerzeichen
    private String cleanText(String text) {
        if (text != null) {
            return text.replaceAll("[\\r\\n\\t]", " ").trim();
        }
        return "";
    }
}

