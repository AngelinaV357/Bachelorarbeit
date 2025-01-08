package com.example.Graphimplementierung.Workflowpatterns;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Edge;
import com.example.Graphimplementierung.Grundstruktur.Nodes.TaskNode;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Node;

import java.util.HashSet;
import java.util.Set;

public class SubProcessPatternFinder {

    private Set<Edge> outputEdges = new HashSet<>();

    public void findSubProcessPatterns(BPMNGraph graph) {
        for (Node node : graph.getNodes()) {
            if (node instanceof TaskNode && "SubProcess".equals(((TaskNode) node).getActivityType())) {
                TaskNode subProcessNode = (TaskNode) node;
                System.out.println("\nSubprozess gefunden: " + subProcessNode.getName());
                processOutgoingEdges(graph, subProcessNode);
                processIncomingEdges(graph, subProcessNode);
            }
        }
    }

    private void processOutgoingEdges(BPMNGraph graph, TaskNode subProcessNode) {
        System.out.println("Regeln für ausgehende Kanten:");
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(subProcessNode)) {
                if (!outputEdges.contains(edge)) {
                    Node targetNode = edge.getTarget();
                    String condition = edge.getCondition();
                    if (condition != null && !condition.isEmpty()) {
                        System.out.println("Es ist erlaubt, dass " + targetNode.getName() +
                                " nach " + subProcessNode.getName() + " ausgeführt wird, wenn die Bedingung '" +
                                condition + "' erfüllt ist.");
                    } else {
                        System.out.println("Es ist erlaubt, dass " + targetNode.getName() +
                                " nach " + subProcessNode.getName() + " ausgeführt wird.");
                    }
                    outputEdges.add(edge);
                }
            }
        }
    }

    private void processIncomingEdges(BPMNGraph graph, TaskNode subProcessNode) {
        System.out.println("Regeln für eingehende Kanten:");
        for (Edge edge : graph.getEdges()) {
            if (edge.getTarget().equals(subProcessNode)) {
                if (!outputEdges.contains(edge)) {
                    Node sourceNode = edge.getSource();
                    System.out.println("Es ist erlaubt, dass " + subProcessNode.getName() +
                            " nach " + sourceNode.getName() + " ausgeführt wird.");
                    outputEdges.add(edge);
                }
            }
        }
    }
}
