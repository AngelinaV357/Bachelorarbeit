package com.example.Graphimplementierung.Workflowpatterns;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Edge;
import com.example.Graphimplementierung.Grundstruktur.Nodes.TaskNode;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Node;

import java.util.HashSet;
import java.util.Set;

public class TaskPatternFinder {

    private Set<Edge> outputEdges = new HashSet<>();

    // Hauptmethode zum Finden aller Task-Typen
    public void findAllTaskPatterns(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        for (Node node : graph.getNodes()) {
            if (node instanceof TaskNode) {
                TaskNode taskNode = (TaskNode) node;

                // UserTask erkennen und verarbeiten
                if ("UserTask".equals(taskNode.getActivityType())) {
                    String message = "\nUser Task gefunden: " + cleanText(taskNode.getName());
                    System.out.println(message);
                    sbvrDataBuilder.append(message).append("\n");
                    processOutgoingEdgesForUserTask(graph, taskNode, sbvrDataBuilder);
                    processIncomingEdges(graph, taskNode, sbvrDataBuilder);
                }

                // ServiceTask erkennen und verarbeiten
                if ("ServiceTask".equals(taskNode.getActivityType())) {
                    String message = "\nService Task gefunden: " + cleanText(taskNode.getName());
                    System.out.println(message);
                    sbvrDataBuilder.append(message).append("\n");
                    processOutgoingEdgesForServiceTask(graph, taskNode, sbvrDataBuilder);
                    processIncomingEdges(graph, taskNode, sbvrDataBuilder);
                }

                // BusinessRuleTask erkennen und verarbeiten
                if ("BusinessRuleTask".equals(taskNode.getActivityType())) {
                    String message = "\nBusiness Rule Task gefunden: " + cleanText(taskNode.getName());
                    System.out.println(message);
                    sbvrDataBuilder.append(message).append("\n");
                    processOutgoingEdgesForBusinessRuleTask(graph, taskNode, sbvrDataBuilder);
                    processIncomingEdges(graph, taskNode, sbvrDataBuilder);
                }

                // IntermediateCatchEvent erkennen und verarbeiten
                if ("IntermediateCatchEvent".equals(taskNode.getActivityType())) {
                    String message = "\nIntermediate Catch Event gefunden: " + cleanText(taskNode.getName());
                    System.out.println(message);
                    sbvrDataBuilder.append(message).append("\n");
                    processOutgoingEdgesForIntermediateEvent(graph, taskNode, "Catch", sbvrDataBuilder);
                    processIncomingEdges(graph, taskNode, sbvrDataBuilder);
                }

                // IntermediateThrowEvent erkennen und verarbeiten
                if ("IntermediateThrowEvent".equals(taskNode.getActivityType())) {
                    String message = "\nIntermediate Throw Event gefunden: " + cleanText(taskNode.getName());
                    System.out.println(message);
                    sbvrDataBuilder.append(message).append("\n");
                    processOutgoingEdgesForIntermediateEvent(graph, taskNode, "Throw", sbvrDataBuilder);
                    processIncomingEdges(graph, taskNode, sbvrDataBuilder);
                }

                // Task erkennen und verarbeiten
                if ("Task".equals(taskNode.getActivityType())) {
                    String message = "\nTask gefunden: " + cleanText(taskNode.getName());
                    System.out.println(message);
                    sbvrDataBuilder.append(message).append("\n");
                    processOutgoingEdgesForTask(graph, taskNode, sbvrDataBuilder);
                    processIncomingEdges(graph, taskNode, sbvrDataBuilder);
                }
            }
        }
    }

    private void processOutgoingEdgesForIntermediateEvent(BPMNGraph graph, TaskNode eventNode, String eventType, StringBuilder sbvrDataBuilder) {
        String header = "SBVR-Regeln für ausgehende Kanten (" + eventType + " Event):";
        System.out.println(header);
        sbvrDataBuilder.append(header).append("\n");

        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(eventNode)) {
                if (!outputEdges.contains(edge)) {
                    Node targetNode = edge.getTarget();
                    String condition = edge.getCondition();

                    String rule = (condition != null && !condition.isEmpty())
                            ? "Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                            " ausgeführt wird, nachdem " + cleanText(eventNode.getName()) +
                            " ausgeführt wird, wenn die Bedingung '" + cleanText(condition) + "' erfüllt ist."
                            : "Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                            " ausgeführt wird, nachdem " + cleanText(eventNode.getName()) + " ausgeführt wird.";

                    System.out.println(rule);
                    sbvrDataBuilder.append(rule).append("\n");
                    outputEdges.add(edge);
                }
            }
        }
    }

    private void processOutgoingEdgesForTask(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        String header = "SBVR-Regeln für ausgehende Kanten:";
        System.out.println(header);
        sbvrDataBuilder.append(header).append("\n");

        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                if (!outputEdges.contains(edge)) {
                    Node targetNode = edge.getTarget();
                    String condition = edge.getCondition();

                    String rule = (condition != null && !condition.isEmpty())
                            ? "Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                            " ausgeführt wird, nachdem " + cleanText(taskNode.getName()) +
                            " ausgeführt wird, wenn die Bedingung '" + cleanText(condition) + "' erfüllt ist."
                            : "Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                            " ausgeführt wird, nachdem " + cleanText(taskNode.getName()) + " ausgeführt wird.";

                    System.out.println(rule);
                    sbvrDataBuilder.append(rule).append("\n");
                    outputEdges.add(edge);
                }
            }
        }
    }

    private void processOutgoingEdgesForServiceTask(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        String header = "SBVR-Regeln für ausgehende Kanten (ServiceTask):";
        System.out.println(header);
        sbvrDataBuilder.append(header).append("\n");

        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                if (!outputEdges.contains(edge)) {
                    Node targetNode = edge.getTarget();
                    String rule = "Es ist erforderlich, dass " + cleanText(targetNode.getName()) +
                            " ausgeführt wird, wenn der Service erfolgreich abgeschlossen wurde.";
                    System.out.println(rule);
                    sbvrDataBuilder.append(rule).append("\n");
                    outputEdges.add(edge);
                }
            }
        }
    }

    private void processOutgoingEdgesForUserTask(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        String header = "SBVR-Regeln für ausgehende Kanten (UserTask):";
        System.out.println(header);
        sbvrDataBuilder.append(header).append("\n");

        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                if (!outputEdges.contains(edge)) {
                    Node targetNode = edge.getTarget();

                    String rule = "Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                            " nach " + cleanText(taskNode.getName()) + " ausgeführt wird.";
                    System.out.println(rule);
                    sbvrDataBuilder.append(rule).append("\n");
                    outputEdges.add(edge);
                }
            }
        }
    }

    private void processIncomingEdges(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        String header = "SBVR-Regeln für eingehende Kanten:";
        System.out.println(header);
        sbvrDataBuilder.append(header).append("\n");

        for (Edge edge : graph.getEdges()) {
            if (edge.getTarget().equals(taskNode)) {
                if (!outputEdges.contains(edge)) {
                    Node sourceNode = edge.getSource();

                    String rule = "Es ist erlaubt, dass " + cleanText(taskNode.getName()) +
                            " nach " + cleanText(sourceNode.getName()) + " ausgeführt wird.";
                    System.out.println(rule);
                    sbvrDataBuilder.append(rule).append("\n");
                    outputEdges.add(edge);
                }
            }
        }
    }

    // Methode zur Verarbeitung der ausgehenden Kanten für BusinessRuleTask
    private void processOutgoingEdgesForBusinessRuleTask(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        System.out.println("SBVR-Regeln für ausgehende Kanten (BusinessRuleTask):");

        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                if (!outputEdges.contains(edge)) {
                    Node targetNode = edge.getTarget();

                    // Hole die Lane des Quell- und Zielknotens
                    String sourceLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";
                    String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                    // SBVR-Regel für BusinessRuleTask
                    String rule = "Es ist notwendig, " + targetLane + " " + cleanText(targetNode.getName()) +
                            " ausgeführt wird, wenn alle Anforderungen überprüft worden sind.";
                    System.out.print(rule);
                    sbvrDataBuilder.append(rule).append("\n");
                    outputEdges.add(edge);
                }
            }
        }
    }

    // Methode zum Entfernen von Zeilenumbrüchen, Tabulatoren und überflüssigen Leerzeichen
    private static String cleanText(String text) {
        if (text != null) {
            // Entfernt Zeilenumbrüche, Carriage-Returns und Tabulatoren
            text = text.replaceAll("[\\r\\n\\t]+", " "); // Alle Zeilenumbrüche und Tabs durch ein einzelnes Leerzeichen ersetzen
            // Entfernt überflüssige Leerzeichen
            text = text.replaceAll(" +", " ").trim(); // Mehrfache Leerzeichen durch ein einziges ersetzen
        }
        return text;
    }
}
