package com.example.WorkflowPatterns;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.dot.DOTImporter; // Korrekte Import-Anweisung
import org.jgrapht.nio.ImportException;
import org.jgrapht.util.SupplierUtil;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

public class GraphParser {

    // Hilfsmethode zur Erkennung des XOR-Gateways
    public static void detectXORGatewaysAndGenerateSBVR(Graph<String, DefaultEdge> graph, Map<String, String> nodeNames, Map<String, String> conditions) {
        Set<String> visitedNodes = new HashSet<>(); // Set, um bereits besuchte Knoten zu verfolgen

        for (String node : graph.vertexSet()) {
            if (visitedNodes.contains(node)) {
                continue; // Wenn der Knoten bereits besucht wurde, überspringen
            }

            String label = nodeNames.get(node); // Label des Knotens abrufen

            // Überprüfen, ob der Knoten das XOR-Gateway ist
            if (label != null && label.equals("ExclusiveGateway")) {
                // Markieren, dass dieser Knoten besucht wurde
                visitedNodes.add(node);

                // Erkennen, ob es sich um ein divergierendes oder konvergierendes XOR-Gateway handelt
                List<String> outgoingNodes = new ArrayList<>();
                List<String> incomingNodes = new ArrayList<>();

                // Kanten analysieren
                for (DefaultEdge edge : graph.outgoingEdgesOf(node)) {
                    outgoingNodes.add(graph.getEdgeTarget(edge)); // Hinzufügen ausgehender Knoten
                }
                for (DefaultEdge edge : graph.incomingEdgesOf(node)) {
                    incomingNodes.add(graph.getEdgeSource(edge)); // Hinzufügen eingehender Knoten
                }

                // Überprüfen, ob es ein divergierendes oder konvergierendes Gateway ist
                StringBuilder sbvrRule = new StringBuilder();
                if (outgoingNodes.size() > 1) { // Divergierend
                    sbvrRule.append("Dies ist ein diverging XOR-Gateway.\n");

                    // Bedingungen und Aktivitäten für divergierende Kanten ausgeben
                    for (String outgoingNode : outgoingNodes) {
                        boolean hasCondition = hasConditionForFlow(node, outgoingNode, conditions);
                        String condition = conditions.get(node + "->" + outgoingNode);
                        sbvrRule.append("Es ist erlaubt, dass die Aktivität ").append(outgoingNode)
                                .append(" nach der Aktivität ").append(node).append(" ausgeführt wird.");
                        if (hasCondition) {
                            sbvrRule.append(" Wenn die Bedingung '").append(condition).append("' erfüllt ist.");
                        }
                        sbvrRule.append("\n");
                    }
                } else if (incomingNodes.size() > 1) { // Konvergent
                    sbvrRule.append("Dies ist ein converging XOR-Gateway.\n");

                    // Bedingungen und Aktivitäten für konvergierende Kanten ausgeben
                    for (String incomingNode : incomingNodes) {
                        boolean hasCondition = hasConditionForFlow(incomingNode, node, conditions);
                        String condition = conditions.get(incomingNode + "->" + node);
                        sbvrRule.append("Es ist erlaubt, dass die Aktivität ").append(node)
                                .append(" nach der Aktivität ").append(incomingNode).append(" ausgeführt wird.");
                        if (hasCondition) {
                            sbvrRule.append(" Wenn die Bedingung '").append(condition).append("' erfüllt ist.");
                        }
                        sbvrRule.append("\n");
                    }
                }

                // Ausgabe der SBVR-Regeln
                System.out.println(sbvrRule.toString());
            }
        }

        // Abgeschlossene Aktivitäten ausgeben
        for (String node : graph.vertexSet()) {
            if (visitedNodes.contains(node)) {
                continue; // Bereits besuchte Knoten überspringen
            }
            // Überprüfen, ob die Aktivität keine ausgehenden Kanten hat
            if (graph.outgoingEdgesOf(node).isEmpty()) {
                System.out.println("Die Aktivität '" + nodeNames.get(node) + "' ist abgeschlossen.");
            }
        }
    }

    // Hilfsmethode, um zu überprüfen, ob eine Bedingung für den Fluss existiert
    private static boolean hasConditionForFlow(String fromNode, String toNode, Map<String, String> conditions) {
        return conditions.containsKey(fromNode + "->" + toNode); // Bedingung im Map vorhanden
    }

    public static void main(String[] args) {
        // Create the graph with a supplier for vertices and edges
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(SupplierUtil.createStringSupplier(), DefaultEdge::new, false);

        try {
            // Import the DOT file
            FileReader dotFile = new FileReader("src/main/ /bpmnGraph.dot");
            DOTImporter<String, DefaultEdge> importer = new DOTImporter<>();
            importer.importGraph(graph, dotFile);

            // Output the graph to the console
            System.out.println("Graph loaded from DOT file:");
            System.out.println(graph);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



