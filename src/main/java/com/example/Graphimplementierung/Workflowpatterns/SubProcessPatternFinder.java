package com.example.Graphimplementierung.Workflowpatterns;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Edge;
import com.example.Graphimplementierung.Grundstruktur.Nodes.TaskNode;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Node;
import com.example.Graphimplementierung.Grundstruktur.Parser.SBVRFileSaver;

import java.util.HashSet;
import java.util.Set;

public class SubProcessPatternFinder {

    private Set<Edge> outputEdges = new HashSet<>();

    public void findSubProcessPatterns(BPMNGraph graph, StringBuilder sbvrData) {
        for (Node node : graph.getNodes()) {
            if (node instanceof TaskNode && "SubProcess".equals(((TaskNode) node).getActivityType())) {
                TaskNode subProcessNode = (TaskNode) node;

                String message = "\nSubprozess gefunden: " + cleanText(subProcessNode.getName());
                System.out.println(message);
                sbvrData.append(message).append("\n");

                processOutgoingEdges(graph, subProcessNode, sbvrData);
                processIncomingEdges(graph, subProcessNode, sbvrData);
            }
        }
    }

    // Bearbeitet ausgehende Kanten und erstellt die zugehörigen SBVR-Regeln
    private void processOutgoingEdges(BPMNGraph graph, TaskNode subProcessNode, StringBuilder sbvrData) {
        // Durchläuft alle Kanten und erstellt Regeln für die ausgehenden Kanten
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(subProcessNode)) {
                if (!outputEdges.contains(edge)) {
                    Node targetNode = edge.getTarget();
                    String condition = edge.getCondition();

                    // Regel erstellen, wenn eine Bedingung für die Kante existiert
                    if (condition != null && !condition.isEmpty()) {
                        String message = "It is obligatory " + cleanText(targetNode.getName()) +
                                " after " + cleanText(subProcessNode.getName()) + " and after '" +
                                cleanText(condition) + "' .";
                        sbvrData.append(message).append("\n");
                    } else {
                        String message = "It is obligatory " + cleanText(targetNode.getName()) +
                                " that " + cleanText(subProcessNode.getName()) + " .";
                        sbvrData.append(message).append("\n");
                    }

                    outputEdges.add(edge);
                }
            }
        }
    }
    // Bearbeitet eingehende Kanten und erstellt die zugehörigen SBVR-Regeln
    private void processIncomingEdges(BPMNGraph graph, TaskNode subProcessNode, StringBuilder sbvrData) {
        // Durchläuft alle Kanten und erstellt Regeln für die eingehenden Kanten
        for (Edge edge : graph.getEdges()) {
            if (edge.getTarget().equals(subProcessNode)) {
                if (!outputEdges.contains(edge)) {
                    Node sourceNode = edge.getSource();

                    // Regel für eingehende Kante erstellen
                    String message = "Es ist erlaubt, dass " + cleanText(subProcessNode.getName()) +
                            " nach " + cleanText(sourceNode.getName()) + " ausgeführt wird.";
                    sbvrData.append(message).append("\n");

                    outputEdges.add(edge);
                }
            }
        }
    }

    private String cleanText(String text) {
        if (text != null) {
            return text.replaceAll("[\\r\\n\\t]", " ").trim();
        }
        return "";
    }

    // Main-Methode für Ausführung und Speicherung
    public static void main(String[] args) {
        BPMNGraph graph = new BPMNGraph(); // BPMNGraph-Instanz
        SubProcessPatternFinder subProcessPatternFinder = new SubProcessPatternFinder();
        StringBuilder sbvrData = new StringBuilder();

        // SubProcess-Patterns finden
        subProcessPatternFinder.findSubProcessPatterns(graph, sbvrData);

        // SBVR-Daten in eine Datei speichern
        String filePath = "subprocess_patterns.sbvr";
        SBVRFileSaver.saveSBVRToFile(sbvrData.toString(), filePath);
    }
}
