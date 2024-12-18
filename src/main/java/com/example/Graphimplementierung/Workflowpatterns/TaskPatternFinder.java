package com.example.Graphimplementierung.Workflowpatterns;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Edge;
import com.example.Graphimplementierung.Grundstruktur.Nodes.ActivityNode;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Node;

import java.util.HashSet;
import java.util.Set;

public class TaskPatternFinder {

    private Set<Edge> outputEdges = new HashSet<>();

    // Hauptmethode zum Finden aller Task-Typen
    public void findAllTaskPatterns(BPMNGraph graph) {
        for (Node node : graph.getNodes()) {
            if (node instanceof ActivityNode) {
                ActivityNode activityNode = (ActivityNode) node;

                // Task erkennen und weiter verarbeiten
                if ("Task".equals(activityNode.getActivityType())) {
                    System.out.println("\nTask gefunden: " + cleanText(activityNode.getName()));
                    processOutgoingEdgesForTask(graph, activityNode);
                    processIncomingEdges(graph, activityNode);
                }

                // ServiceTask erkennen und weiter verarbeiten
                if ("ServiceTask".equals(activityNode.getActivityType())) {
                    System.out.println("\nService Task gefunden: " + cleanText(activityNode.getName()));
                    processOutgoingEdgesForServiceTask(graph, activityNode);
                    processIncomingEdges(graph, activityNode);
                }

                // UserTask erkennen und weiter verarbeiten
                if ("UserTask".equals(activityNode.getActivityType())) {
                    System.out.println("\nUser Task gefunden: " + cleanText(activityNode.getName()));
                    processOutgoingEdgesForUserTask(graph, activityNode);
                    processIncomingEdges(graph, activityNode);
                }

                // BusinessRuleTask erkennen und weiter verarbeiten
                if ("BusinessRuleTask".equals(activityNode.getActivityType())) {
                    System.out.println("\nBusiness Rule Task gefunden: " + cleanText(activityNode.getName()));
                    processOutgoingEdgesForBusinessRuleTask(graph, activityNode);
                    processIncomingEdges(graph, activityNode);
                }

                // IntermediateCatchEvent erkennen und weiter verarbeiten
                if ("IntermediateCatchEvent".equals(activityNode.getActivityType())) {
                    System.out.println("\nIntermediate Catch Event gefunden: " + cleanText(activityNode.getName()));
                    processOutgoingEdgesForIntermediateEvent(graph, activityNode, "Catch");
                    processIncomingEdges(graph, activityNode);
                }

                // IntermediateThrowEvent erkennen und weiter verarbeiten
                if ("IntermediateThrowEvent".equals(activityNode.getActivityType())) {
                    System.out.println("\nIntermediate Throw Event gefunden: " + cleanText(activityNode.getName()));
                    processOutgoingEdgesForIntermediateEvent(graph, activityNode, "Throw");
                    processIncomingEdges(graph, activityNode);
                }
            }
        }
    }

    private void processOutgoingEdgesForIntermediateEvent(BPMNGraph graph, ActivityNode eventNode, String eventType) {
        System.out.println("SBVR-Regeln für ausgehende Kanten (" + eventType + " Event):");
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(eventNode)) {
                if (!outputEdges.contains(edge)) {
                    Node targetNode = edge.getTarget();
                    String condition = edge.getCondition();

                    // Falls eine Bedingung für die Kante existiert
                    if (condition != null && !condition.isEmpty()) {
                        System.out.println("Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                                " nach " + cleanText(eventNode.getName()) + " ausgeführt wird, wenn die Bedingung '" +
                                cleanText(condition) + "' erfüllt ist.");
                    } else {
                        System.out.println("Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                                " nach " + cleanText(eventNode.getName()) + " ausgeführt wird.");
                    }
                    outputEdges.add(edge);
                }
            }
        }
    }


    // Spezifische Methode zur Verarbeitung der ausgehenden Kanten für alle Task-Typen
    private void processOutgoingEdgesForTask(BPMNGraph graph, ActivityNode taskNode) {
        System.out.println("SBVR-Regeln für ausgehende Kanten:");
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                if (!outputEdges.contains(edge)) {
                    Node targetNode = edge.getTarget();
                    String condition = edge.getCondition();
                    if (condition != null && !condition.isEmpty()) {
                        System.out.println("Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                                " nach " + cleanText(taskNode.getName()) + " ausgeführt wird, wenn die Bedingung '" +
                                cleanText(condition) + "' erfüllt ist.");
                    } else {
                        System.out.println("Es ist erlaubt, dass " + cleanText(targetNode.getName()) +
                                " nach " + cleanText(taskNode.getName()) + " ausgeführt wird.");
                    }
                    outputEdges.add(edge);
                }
            }
        }
    }

    // Spezifische Methode zur Verarbeitung der ausgehenden Kanten für ServiceTask
    private void processOutgoingEdgesForServiceTask(BPMNGraph graph, ActivityNode taskNode) {
        System.out.println("SBVR-Regeln für ausgehende Kanten (ServiceTask):");
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                if (!outputEdges.contains(edge)) {
                    Node targetNode = edge.getTarget();
                    System.out.println("Es ist erforderlich, dass " + cleanText(targetNode.getName()) +
                            " ausgeführt wird, wenn der Service erfolgreich abgeschlossen wurde.");
                    outputEdges.add(edge);
                }
            }
        }
    }

    // Spezifische Methode zur Verarbeitung der ausgehenden Kanten für UserTask
    private void processOutgoingEdgesForUserTask(BPMNGraph graph, ActivityNode taskNode) {
        System.out.println("SBVR-Regeln für ausgehende Kanten (UserTask):");
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                if (!outputEdges.contains(edge)) {
                    Node targetNode = edge.getTarget();
                    System.out.println("Es ist erforderlich, dass " + cleanText(targetNode.getName()) +
                            " ausgeführt wird, wenn der Benutzer seine Aufgabe abgeschlossen hat.");
                    outputEdges.add(edge);
                }
            }
        }
    }

    // Spezifische Methode zur Verarbeitung der ausgehenden Kanten für BusinessRuleTask
    private void processOutgoingEdgesForBusinessRuleTask(BPMNGraph graph, ActivityNode taskNode) {
        System.out.println("SBVR-Regeln für ausgehende Kanten (BusinessRuleTask):");
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                if (!outputEdges.contains(edge)) {
                    Node targetNode = edge.getTarget();
                    System.out.println("Es ist notwendig, " + cleanText(targetNode.getName()) +
                            " ausgeführt wird, wenn alle Anforderungen überprüft worden sind.");
                    outputEdges.add(edge);
                }
            }
        }
    }

    // Methode zur Verarbeitung der eingehenden Kanten
    private void processIncomingEdges(BPMNGraph graph, ActivityNode taskNode) {
        System.out.println("Regeln für eingehende Kanten:");
        for (Edge edge : graph.getEdges()) {
            if (edge.getTarget().equals(taskNode)) {
                if (!outputEdges.contains(edge)) {
                    Node sourceNode = edge.getSource();
                    System.out.println("Es ist erlaubt, dass " + cleanText(taskNode.getName()) +
                            " nach " + cleanText(sourceNode.getName()) + " ausgeführt wird.");
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
