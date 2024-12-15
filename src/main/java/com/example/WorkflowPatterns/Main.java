package com.example.WorkflowPatterns;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        try {
            // Schritt 1: BPMN-Datei laden und Graphen erstellen
            File bpmnFile = new File("src/main/resources/May_combine_ingredients.bpmn");

            // Schritt 2: Konvertierung der BPMN-Datei in einen Graphen
            Graph<String, BpmnModelParser.LabeledEdge> bpmnGraph = BpmnModelParser.convertBpmnToGraph(bpmnFile);

            // Schritt 3: Node-Typen definieren (hier als Beispiel, du kannst dies anpassen)
            Map<String, String> nodeTypes = new HashMap<>();
            // Beispiel: Manuelle Zuweisung der Knotentypen
            // (Du kannst hier Logik hinzufügen, um die tatsächlichen Typen aus dem BPMN-Modell zu extrahieren)
            nodeTypes.put("Start", "startEvent");
            nodeTypes.put("End", "endEvent");
            nodeTypes.put("ExclusiveGateway", "exclusiveGateway");

            // Schritt 4: Erstelle das Set der verarbeiteten Knoten
            Set<String> processedNodes = new HashSet<>();

            // Schritt 5: XOR-Gateway-Pattern erkennen
            List<List<String>> xorPatterns = detectXorGatewayPatterns(bpmnGraph, nodeTypes, processedNodes);

            // Schritt 6: Ausgabe der erkannten XOR-Gateway-Patterns
            if (xorPatterns.isEmpty()) {
                System.out.println("Keine XOR-Gateway-Patterns gefunden.");
            } else {
                System.out.println("Erkannte XOR-Gateway-Patterns:");
                for (List<String> pattern : xorPatterns) {
                    System.out.println(pattern);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methode zur Erkennung von Diverging und Converging XOR-Gateways
    public static List<List<String>> detectXorGatewayPatterns(Graph<String, BpmnModelParser.LabeledEdge> graph,
                                                              Map<String, String> nodeTypes,
                                                              Set<String> processedNodes) {
        List<List<String>> xorPatterns = new ArrayList<>();

        // Iteriere über alle Knoten im Graphen
        for (String node : graph.vertexSet()) {
            // Überspringe bereits verarbeitete Knoten
            if (processedNodes.contains(node)) {
                continue;
            }

            // Überprüfen, ob der Knoten ein XOR-Gateway ist
            if ("exclusiveGateway".equalsIgnoreCase(nodeTypes.get(node))) {
                // Eingehende und ausgehende Kanten finden
                List<String> incoming = findIncomingEdges(graph, node);
                List<String> outgoing = getOutgoingEdges(graph, node);

                // Diverging XOR-Gateway: Ein eingehender Pfeil und mehrere ausgehende
                if (incoming.size() == 1 && outgoing.size() >= 2) {
                    List<String> pattern = new ArrayList<>();
                    pattern.add("Diverging XOR Gateway: " + node);
                    pattern.add(incoming.get(0));  // Eingehender Knoten
                    pattern.addAll(outgoing);      // Ausgehende Knoten
                    xorPatterns.add(pattern);
                    processedNodes.add(node);
                }

                // Converging XOR-Gateway: Mehrere eingehende Pfeile und ein ausgehender
                if (incoming.size() >= 2 && outgoing.size() == 1) {
                    List<String> pattern = new ArrayList<>();
                    pattern.add("Converging XOR Gateway: " + node);
                    pattern.addAll(incoming);      // Eingehende Knoten
                    pattern.add(outgoing.get(0));  // Ausgehender Knoten
                    xorPatterns.add(pattern);
                    processedNodes.add(node);
                }
            }
        }

        return xorPatterns;
    }

    // Hilfsfunktion: Finde eingehende Kanten für einen Knoten
    private static List<String> findIncomingEdges(Graph<String, BpmnModelParser.LabeledEdge> graph, String targetNode) {
        List<String> incomingEdges = new ArrayList<>();
        for (String sourceNode : graph.vertexSet()) {
            if (graph.containsEdge(sourceNode, targetNode)) {
                incomingEdges.add(sourceNode);
            }
        }
        return incomingEdges;
    }

    // Hilfsfunktion: Finde ausgehende Kanten für einen Knoten
    private static List<String> getOutgoingEdges(Graph<String, BpmnModelParser.LabeledEdge> graph, String node) {
        List<String> outgoingEdges = new ArrayList<>();
        for (DefaultEdge edge : graph.outgoingEdgesOf(node)) {
            outgoingEdges.add(graph.getEdgeTarget((BpmnModelParser.LabeledEdge) edge));
        }
        return outgoingEdges;
    }
}
