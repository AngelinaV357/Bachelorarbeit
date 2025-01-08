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

    private void processOutgoingEdges(BPMNGraph graph, TaskNode subProcessNode, StringBuilder sbvrData) {
        String message = "Regeln für ausgehende Kanten:";
        System.out.println(message);
        sbvrData.append(message).append("\n");

        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(subProcessNode)) {
                if (!outputEdges.contains(edge)) {
                    Node targetNode = edge.getTarget();
                    String condition = edge.getCondition();

                    if (condition != null && !condition.isEmpty()) {
                        message = "Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                                " nach " + cleanText(subProcessNode.getName()) + " ausgeführt wird, wenn die Bedingung '" +
                                cleanText(condition) + "' erfüllt ist.";
                    } else {
                        message = "Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                                " nach " + cleanText(subProcessNode.getName()) + " ausgeführt wird.";
                    }

                    System.out.println(message);
                    sbvrData.append(message).append("\n");
                    outputEdges.add(edge);
                }
            }
        }
    }

    private void processIncomingEdges(BPMNGraph graph, TaskNode subProcessNode, StringBuilder sbvrData) {
        String message = "Regeln für eingehende Kanten:";
        System.out.println(message);
        sbvrData.append(message).append("\n");

        for (Edge edge : graph.getEdges()) {
            if (edge.getTarget().equals(subProcessNode)) {
                if (!outputEdges.contains(edge)) {
                    Node sourceNode = edge.getSource();

                    message = "Es ist erlaubt, dass " + cleanText(subProcessNode.getName()) +
                            " nach " + cleanText(sourceNode.getName()) + " ausgeführt wird.";
                    System.out.println(message);
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
