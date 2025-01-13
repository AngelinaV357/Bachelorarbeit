package com.example.Graphimplementierung.Workflowpatterns;

import com.example.Graphimplementierung.Grundstruktur.Nodes.*;

import java.util.HashSet;
import java.util.Set;

public class TaskPatternFinder {

    private static Set<Edge> outputEdges = new HashSet<>();

    // Hauptmethode zum Finden aller Task-Typen
    public void findAllTaskPatterns(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        for (Node node : graph.getNodes()) {
            // StartEventNode erkennen und verarbeiten
            if (node instanceof StartEventNode startEventNode) {
                String message = "\nStart Event gefunden: " + cleanText(startEventNode.getName());
                System.out.println(message);
                sbvrDataBuilder.append(message).append("\n");
                processOutgoingEdgesForStartEvent(graph, startEventNode, sbvrDataBuilder);
            }

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

                // Task erkennen und verarbeiten
                if ("Task".equals(taskNode.getActivityType())) {
                    String message = "\nTask gefunden: " + cleanText(taskNode.getName());
                    System.out.println(message);
                    sbvrDataBuilder.append(message).append("\n");
                    processOutgoingEdgesForTask(graph, taskNode, sbvrDataBuilder);
                    processIncomingEdges(graph, taskNode, sbvrDataBuilder);
                }
            }

            // EndEventNode erkennen und verarbeiten
            if (node instanceof EndEventNode endEventNode) {
                String message = "\nEnd Event gefunden: " + cleanText(endEventNode.getName());
                System.out.println(message);
                sbvrDataBuilder.append(message).append("\n");
                processIncomingEdgesForEndEvent(graph, endEventNode, sbvrDataBuilder);
            }
        }
    }

    private void processOutgoingEdgesForTask(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                // Prüfen, ob die Kante eine Gateway-Kante mit einer zyklischen Bedingung ist
                if (outputEdges.contains(edge)) {
                    // Falls eine Kante als Loop markiert wird (bei spezifischen Bedingungen)
                    if (edge.getCondition() != null && !edge.getCondition().isEmpty() && edge.getCondition().equals("no")) {
                        System.out.println("Loop entdeckt: " + edge);
                        // Eine spezielle SBVR-Regel für den Loop ausgeben
                        String loopRule = "It is obligatory that the task " + cleanText(taskNode.getName()) +
                                " can repeat after encountering the condition '" + cleanText(edge.getCondition()) + "'.\n";
                        sbvrDataBuilder.append("Loop entdeckt: ").append(edge).append("\n");
                        sbvrDataBuilder.append(loopRule).append("\n");  // Regel für Loop hinzufügen
                    }
                    continue;  // Weiter mit der nächsten Kante, ohne sie erneut zu verarbeiten
                }

                // Kante als besucht markieren
                outputEdges.add(edge);

                Node targetNode = edge.getTarget();
                Node sourceNode = edge.getSource();
                String condition = edge.getCondition();
                String sourceLane = sourceNode.getLane() != null ? sourceNode.getLane().getName() : "Unbekannte Lane";
                String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                // Regel erstellen, basierend auf der Bedingung (falls vorhanden)
                String rule = (condition != null && !condition.isEmpty())
                        ? "It is obligatory that " + " " + cleanText(targetLane) + " "
                        + cleanText(targetNode.getName()) +
                        " after " + cleanText(taskNode.getName()) +
                        " and if '" + cleanText(condition) + ".\n"
                        : "It is obligatory that " + " " + cleanText(targetLane) + " "
                        + cleanText(targetNode.getName()) +
                        " after " + " " + cleanText(sourceLane) + " " + cleanText(taskNode.getName()) + " .\n";

                System.out.println(rule);
                sbvrDataBuilder.append(rule).append("\n");
            }
        }
    }


    private void processIncomingEdges(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        for (Edge edge : graph.getEdges()) {
            if (edge.getTarget().equals(taskNode)) {
                // Prüfen, ob die Kante bereits besucht wurde (für Loop-Erkennung)
                if (outputEdges.contains(edge)) {
                    System.out.println("Loop entdeckt: " + edge);
                    sbvrDataBuilder.append("Loop entdeckt: ").append(edge).append("\n");
                    continue;  // Weiter mit der nächsten Kante, ohne sie erneut zu verarbeiten
                }

                // Kante als besucht markieren
                outputEdges.add(edge);

                Node sourceNode = edge.getSource();
                Node targetNode = edge.getTarget();

                String sourceLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";
                String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                String rule = "It is obligatory that " + targetLane + " " + cleanText(taskNode.getName()) +
                        " after " + sourceLane + " " + cleanText(sourceNode.getName()) + ".\n";
                System.out.println(rule);
                sbvrDataBuilder.append(rule).append("\n");
            }
        }
    }


    // SBVR-Regeln für ausgehende Kanten von StartEvents
    private void processOutgoingEdgesForStartEvent(BPMNGraph graph, StartEventNode startEventNode, StringBuilder sbvrDataBuilder) {
        String rule = "It is obligatory that the Process starts with " + cleanText(startEventNode.getName()) + ".\n";
        System.out.println(rule);
        sbvrDataBuilder.append(rule).append("\n");
    }




    // SBVR-Regeln für eingehende Kanten von EndEvents
    private void processIncomingEdgesForEndEvent(BPMNGraph graph, EndEventNode endEventNode, StringBuilder sbvrDataBuilder) {

        for (Edge edge : graph.getEdges()) {
            if (edge.getTarget().equals(endEventNode)) {
                if (!outputEdges.contains(edge)) {
                    String rule = "It is obligatory the Process ends with " + cleanText(endEventNode.getName()) + ".\n";
                    System.out.println(rule);
                    sbvrDataBuilder.append(rule).append("\n");
                    outputEdges.add(edge);
                }
            }
        }
    }

    private void processOutgoingEdgesForServiceTask(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                    Node targetNode = edge.getTarget();
                    Node sourceNode = edge.getSource();

                    String sourceLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";
                    String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                    String rule = "It is obligatory that " + targetLane + " " + cleanText(targetNode.getName()) +
                            " after " + sourceLane + cleanText(taskNode.getName()) + " and if the service is automated completed.\n";
                    System.out.println(rule);
                    sbvrDataBuilder.append(rule).append("\n");
            }
        }
    }

    private void processOutgoingEdgesForUserTask(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                    Node targetNode = edge.getTarget();
                    Node sourceNode = edge.getSource();

                    String sourceLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";
                    String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                    String rule = "It is obligatory that " + targetLane + " " + cleanText(targetNode.getName()) +
                            " is executed only if " + sourceLane + " " + cleanText(taskNode.getName()) +
                            " has been successfully completed";
                    System.out.println(rule);
                    sbvrDataBuilder.append(rule).append("\n");
            }
        }
    }


    // Methode zur Verarbeitung der ausgehenden Kanten für BusinessRuleTask
    private void processOutgoingEdgesForBusinessRuleTask(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                    Node sourceNode = edge.getSource();
                    Node targetNode = edge.getTarget();

                    // Hole die Lane des Quell- und Zielknotens
                    String sourceLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";
                    String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                    // SBVR-Regel für BusinessRuleTask
                    String rule = "It is obligatory that " + targetLane + " " + cleanText(targetNode.getName()) +
                            " after " + sourceLane + " " + cleanText(sourceNode.getName()) + " and if the requirements for the activity have been checked.\n";
                    System.out.print(rule);
                    sbvrDataBuilder.append(rule).append("\n");
            }
        }
    }

    public void processMessageEdges(BPMNGraph graph, StringBuilder sbvrDataBuilder) {

        for (Edge edge : graph.getEdges()) {
            String header = "SBVR-Regeln für MessageEdges:";
            // Überprüfe, ob die Kante eine MessageEdge ist
            if (edge instanceof MessageEdge) {
                Node sourceNode = edge.getSource();
                Node targetNode = edge.getTarget();

                // Prüfe, ob die Quelle ein ParticipantNode ist
                boolean isSourceParticipant = sourceNode instanceof ParticipantNode;

                // Hole die Lane-Informationen
                String sourceLane = sourceNode.getLane() != null ? sourceNode.getLane().getName() : "Unbekannte Lane";
                String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                // Generiere die SBVR-Regel für das Senden
                String sendRule;
                if (isSourceParticipant) {
                    sendRule = "It is permitted that " + cleanText(sourceNode.getName()) + " sends a message to " +
                            cleanText(targetNode.getName()) + " .\n";
                } else {
                    sendRule = "It is permitted that " + sourceLane + " " + cleanText(sourceNode.getName()) +
                            " sends a message to " + cleanText(targetNode.getName()) + ".\n";
                }

                // Ausgabe und Speichern der Regel für das Senden
                System.out.println(sendRule);
                sbvrDataBuilder.append(sendRule).append("\n");

                // Generiere die SBVR-Regel für das Empfangen
                String receiveRule;
                if (isSourceParticipant) {
                    receiveRule = "It is permitted that " + cleanText(targetNode.getName()) + " receives a message from " +
                            cleanText(sourceNode.getName()) + ".\n";
                } else {
                    receiveRule = "It is permitted that " + cleanText(targetNode.getName()) +
                            " receives a message from " + sourceLane + " " + cleanText(sourceNode.getName()) + ".\n";
                }

                // Ausgabe und Speichern der Regel für das Empfangen
                System.out.println(receiveRule);
                sbvrDataBuilder.append(receiveRule).append("\n");
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
