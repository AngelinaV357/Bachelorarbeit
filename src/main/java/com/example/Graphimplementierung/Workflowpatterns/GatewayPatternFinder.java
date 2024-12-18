package com.example.Graphimplementierung.Workflowpatterns;

import com.example.Graphimplementierung.Grundstruktur.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Edge;
import com.example.Graphimplementierung.Grundstruktur.Nodes.ActivityNode;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Node;

import java.util.HashSet;
import java.util.Set;

public class GatewayPatternFinder {

    // Set, um bereits ausgegebene Kanten nachzuverfolgen
    private Set<Edge> outputEdges = new HashSet<>();

    // Set, um bereits ausgegebene Gateways nachzuverfolgen
    private Set<String> processedGateways = new HashSet<>();

    public void findExclusiveGatewayPatterns(BPMNGraph graph) {
        for (Node node : graph.getNodes()) {
            if (node instanceof ActivityNode && "ExclusiveGateway".equals(((ActivityNode) node).getActivityType())) {
                ActivityNode gatewayNode = (ActivityNode) node;

                // Verhindern der doppelten Ausgabe desselben Gateways
                if (!processedGateways.contains(gatewayNode.getName())) {
                    System.out.println("\nExclusive Gateway gefunden: " + cleanText(gatewayNode.getName()));
                    processOutgoingEdges(graph, gatewayNode);
                    processIncomingEdges(graph, gatewayNode);
                    processedGateways.add(gatewayNode.getName());  // Gateway als verarbeitet markieren
                }
            }
        }
    }

    public void findParallelGatewayPatterns(BPMNGraph graph) {
        for (Node node : graph.getNodes()) {
            if (node instanceof ActivityNode && "ParallelGateway".equals(((ActivityNode) node).getActivityType())) {
                ActivityNode gatewayNode = (ActivityNode) node;

                // Verhindern der doppelten Ausgabe desselben Gateways
                if (!processedGateways.contains(gatewayNode.getName())) {
                    System.out.println("\nParallel Gateway gefunden: " + cleanText(gatewayNode.getName()));
                    processOutgoingEdges(graph, gatewayNode);
                    processIncomingEdges(graph, gatewayNode);
                    processedGateways.add(gatewayNode.getName());  // Gateway als verarbeitet markieren
                }
            }
        }
    }

    public void findEventBasedGatewayPatterns(BPMNGraph graph) {
        for (Node node : graph.getNodes()) {
            if (node instanceof ActivityNode && "EventBasedGateway".equals(((ActivityNode) node).getActivityType())) {
                ActivityNode gatewayNode = (ActivityNode) node;

                // Verhindern der doppelten Ausgabe desselben Gateways
                if (!processedGateways.contains(gatewayNode.getName())) {
                    System.out.println("\nEventBased Gateway gefunden: " + cleanText(gatewayNode.getName()));
                    processOutgoingEdges(graph, gatewayNode);
                    processIncomingEdges(graph, gatewayNode);
                    processedGateways.add(gatewayNode.getName());  // Gateway als verarbeitet markieren
                }
            }
        }
    }

    // Hilfsmethode zur Verarbeitung ausgehender Kanten eines Gateways
    private void processOutgoingEdges(BPMNGraph graph, ActivityNode gatewayNode) {
        System.out.println("Regeln für ausgehende Kanten:");
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(gatewayNode)) {
                if (!outputEdges.contains(edge)) {
                    Node targetNode = edge.getTarget();
                    String condition = edge.getCondition();
                    if (condition != null && !condition.isEmpty()) {
                        System.out.println("Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                                " nach " + cleanText(gatewayNode.getName()) + " ausgeführt wird, wenn die Bedingung '" +
                                cleanText(condition) + "' erfüllt ist.");
                    } else {
                        System.out.println("Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                                " nach " + cleanText(gatewayNode.getName()) + " ausgeführt wird.");
                    }
                    outputEdges.add(edge);
                }
            }
        }
    }

    // Hilfsmethode zur Verarbeitung eingehender Kanten eines Gateways
    private void processIncomingEdges(BPMNGraph graph, ActivityNode gatewayNode) {
        System.out.println("Regeln für eingehende Kanten:");
        for (Edge edge : graph.getEdges()) {
            if (edge.getTarget().equals(gatewayNode)) {
                if (!outputEdges.contains(edge)) {
                    Node sourceNode = edge.getSource();
                    System.out.println("Es ist erlaubt, dass " + cleanText(gatewayNode.getName()) +
                            " nach " + cleanText(sourceNode.getName()) + " ausgeführt wird.");
                    outputEdges.add(edge);
                }
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

