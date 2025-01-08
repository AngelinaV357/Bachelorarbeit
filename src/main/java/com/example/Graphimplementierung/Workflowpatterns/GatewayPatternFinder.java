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

        // Ausgehende Kanten
        System.out.print("Ausgehende Kanten:\n");
        sbvrData.append("Ausgehende Kanten:\n");
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(gatewayNode)) {
                String condition = edge.getCondition();
                Node targetNode = edge.getTarget();

                if (condition != null && !condition.isEmpty()) {
                    message = "Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                            " nach " + cleanText(gatewayNode.getName()) + " ausgeführt wird, wenn die Bedingung '" +
                            cleanText(condition) + "' erfüllt ist.";
                } else {
                    message = "Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                            " nach " + cleanText(gatewayNode.getName()) + " ausgeführt wird.";
                }
                System.out.println(message);
                sbvrData.append(message).append("\n");
            }
        }

        // Eingehende Kanten
        System.out.print("Eingehende Kanten:\n");
        sbvrData.append("Eingehende Kanten:\n");
        for (Edge edge : graph.getEdges()) {
            if (edge.getTarget().equals(gatewayNode)) {
                Node sourceNode = edge.getSource();
                message = "Es ist erlaubt, dass " + cleanText(gatewayNode.getName()) +
                        " nach " + cleanText(sourceNode.getName()) + " ausgeführt wird.";
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

