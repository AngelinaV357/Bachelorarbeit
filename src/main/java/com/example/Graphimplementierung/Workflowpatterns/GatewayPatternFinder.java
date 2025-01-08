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

    public void findExclusiveGatewayPatterns(BPMNGraph graph) {
        for (Node node : graph.getNodes()) {
            if (node instanceof GatewayNode && "Exclusive".equals(((GatewayNode) node).getGatewayType())) {
                GatewayNode gatewayNode = (GatewayNode) node;

                // Verhindern der doppelten Ausgabe desselben Gateways
                if (!processedGateways.contains(gatewayNode.getId())) {
                    System.out.println("\nExclusive Gateway gefunden: " + cleanText(gatewayNode.getName()));

                    // SBVR-Regeln ausgeben
                    generateSBVRRules(graph, gatewayNode);

                    // Gateway als verarbeitet markieren
                    processedGateways.add(gatewayNode.getId());
                }
            }
        }
    }

    public void findParallelGatewayPatterns(BPMNGraph graph) {
        for (Node node : graph.getNodes()) {
            if (node instanceof GatewayNode && "Parallel".equals(((GatewayNode) node).getGatewayType())) {
                GatewayNode gatewayNode = (GatewayNode) node;

                // Verhindern der doppelten Ausgabe desselben Gateways
                if (!processedGateways.contains(gatewayNode.getId())) {
                    System.out.println("\nParallel Gateway gefunden: " + cleanText(gatewayNode.getName()));

                    // SBVR-Regeln ausgeben
                    generateSBVRRules(graph, gatewayNode);

                    // Gateway als verarbeitet markieren
                    processedGateways.add(gatewayNode.getId());
                }
            }
        }
    }



    public void findEventBasedGatewayPatterns(BPMNGraph graph) {
        for (Node node : graph.getNodes()) {
            if (node instanceof GatewayNode && "EventBased".equals(((GatewayNode) node).getGatewayType())) {
                GatewayNode gatewayNode = (GatewayNode) node;

                // Verhindern der doppelten Ausgabe desselben Gateways
                if (!processedGateways.contains(gatewayNode.getId())) {
                    System.out.println("\nEvent-Based Gateway gefunden: " + cleanText(gatewayNode.getName()));

                    // SBVR-Regeln ausgeben
                    generateSBVRRules(graph, gatewayNode);

                    // Gateway als verarbeitet markieren
                    processedGateways.add(gatewayNode.getId());
                }
            }
        }
    }


    private void generateSBVRRules(BPMNGraph graph, GatewayNode gatewayNode) {
        System.out.println("SBVR-Regeln für " + gatewayNode.getName() + ":");

        // Ausgehende Kanten verarbeiten
        System.out.print("Ausgehende Kanten:\n");
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(gatewayNode)) {
                String condition = edge.getCondition();
                Node targetNode = edge.getTarget();

                if (condition != null && !condition.isEmpty()) {
                    System.out.println("Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                            " nach " + cleanText(gatewayNode.getName()) + " ausgeführt wird, wenn die Bedingung '" +
                            cleanText(condition) + "' erfüllt ist.");
                } else {
                    System.out.println("Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                            " nach " + cleanText(gatewayNode.getName()) + " ausgeführt wird.");
                }
            }
        }

        // Eingehende Kanten verarbeiten
        System.out.print("Eingehende Kanten:\n");
        for (Edge edge : graph.getEdges()) {
            if (edge.getTarget().equals(gatewayNode)) {
                Node sourceNode = edge.getSource();
                System.out.println("Es ist erlaubt, dass " + cleanText(gatewayNode.getName()) +
                        " nach " + cleanText(sourceNode.getName()) + " ausgeführt wird.");
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

