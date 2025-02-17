package com.example.Graphimplementierung.Workflowpatterns;

import com.example.Graphimplementierung.Grundstruktur.Nodes.*;

import java.util.HashSet;
import java.util.Set;

public class TaskPatternFinder {

    private static final Set<Edge> outputEdges = new HashSet<>();
    private Set<Node> gatewayProcessedNodes;

    // Hauptmethode zum Finden aller Task-Typen

    public void findAllTaskPatterns(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        // Fallback-Sicherheitsprüfung
        if (gatewayProcessedNodes == null) {
            gatewayProcessedNodes = new HashSet<>();
        }

        for (Node node : graph.getNodes()) {
            // Prüfe, ob der Knoten bereits in den Gateway-Prozessen verarbeitet wurde
            if (gatewayProcessedNodes.contains(node)) {
                System.out.println("Überspringe bereits verarbeiteten Knoten: " + cleanText(node.getName()));
                continue; // Überspringe diese Knoten
            }

            // StartEventNode erkennen und verarbeiten
            if (node instanceof StartEventNode startEventNode) {
                processOutgoingEdgesForStartEvent(graph, startEventNode, sbvrDataBuilder);
            }

            if (node instanceof IntermediateNode intermediateNode) {
                System.out.println("IntermediateNode gefunden: " + cleanText(intermediateNode.getName()));  // Überprüfen, ob der IntermediateNode erkannt wird
                processIntermediateElements(graph, intermediateNode, sbvrDataBuilder);
            }

            if (node instanceof TaskNode taskNode) {
                // Task-Varianten erkennen und verarbeiten
                processTaskNode(graph, sbvrDataBuilder, taskNode);
            }

            // EndEventNode erkennen und verarbeiten
            if (node instanceof EndEventNode endEventNode) {
                processIncomingEdgesForEndEvent(graph, endEventNode, sbvrDataBuilder);
            }
            gatewayProcessedNodes.add(node);
        }
    }

    private void processTaskNode(BPMNGraph graph, StringBuilder sbvrDataBuilder, TaskNode taskNode) {
            if (gatewayProcessedNodes.contains(taskNode)) {
                return;
            }
            String message = "Verarbeitete Task Nodes: \n";
            System.out.print(message);
        switch (taskNode.getActivityType()) {
            case "UserTask" -> {
//                String message = "\nUser Task gefunden: " + cleanText(taskNode.getName());
//                System.out.println(message);
//                sbvrDataBuilder.append(message).append("\n");
                processOutgoingEdgesForUserTask(graph, taskNode, sbvrDataBuilder);
                processIncomingEdges(graph, taskNode, sbvrDataBuilder);
            }
            case "ServiceTask" -> {
//                String message = "\nService Task gefunden: " + cleanText(taskNode.getName());
//                System.out.println(message);
//                sbvrDataBuilder.append(message).append("\n");
                processOutgoingEdgesForServiceTask(graph, taskNode, sbvrDataBuilder);
                processIncomingEdges(graph, taskNode, sbvrDataBuilder);
            }
            case "BusinessRuleTask" -> {
//                String message = "\nBusiness Rule Task gefunden: " + cleanText(taskNode.getName());
//                System.out.println(message);
//                sbvrDataBuilder.append(message).append("\n");
                processOutgoingEdgesForBusinessRuleTask(graph, taskNode, sbvrDataBuilder);
                processIncomingEdges(graph, taskNode, sbvrDataBuilder);
            }
            case "Task" -> {
//                String message = "\nTask gefunden: " + cleanText(taskNode.getName());
//                System.out.println(message);
//                sbvrDataBuilder.append(message).append("\n");
                processOutgoingEdgesForTask(graph, taskNode, sbvrDataBuilder);
                processIncomingEdges(graph, taskNode, sbvrDataBuilder);
            }
        }
    }



    private void processOutgoingEdgesForTask(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        for (Edge edge : graph.getEdges()) {
            // Prüfen, ob die Kante von der aktuellen Aufgabe (taskNode) ausgeht und keine spezielle Edge ist
            if (edge.getSource().equals(taskNode) && isNormalEdge(edge)) {
                // Prüfen, ob die Kante bereits besucht wurde
                if (outputEdges.contains(edge)) {
                    continue; // Weiter mit der nächsten Kante, ohne sie erneut zu verarbeiten
                }

                // Regel basierend auf der Bedingung erstellen
                Node targetNode = edge.getTarget();
                String condition = edge.getCondition();
                String sourceLane = edge.getSource().getLane() != null ? edge.getSource().getLane().getName() : "Unknown Lane";
                String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unknown Lane";

                // SBVR-Regel basierend auf der Bedingung erstellen
                String rule = (condition != null && !condition.isEmpty())
                        ? "It is obligatory that \"" + cleanText(targetLane) + "\" \"" + cleanText(targetNode.getName()) +
                        "\" after \"" + cleanText(sourceLane) + "\" \"" + cleanText(taskNode.getName()) +
                        "\" and if \"" + cleanText(condition) + ".\n"
                        : "It is obligatory that \"" + cleanText(targetLane) + "\" performs \"" + cleanText(targetNode.getName()) +
                        "\" after \"" + cleanText(sourceLane) + "\" performs \"" + cleanText(taskNode.getName()) + "\".\n";

                System.out.println(rule);
                sbvrDataBuilder.append(rule).append("\n");

                // Kante als besucht markieren
                outputEdges.add(edge);

                // Auslagerung der Loop-Bedingung in eine separate Methode
                handleLoopCondition(taskNode, edge, sbvrDataBuilder);
            }
        }
    }



    private void handleLoopCondition(TaskNode taskNode, Edge edge, StringBuilder sbvrDataBuilder) {
        if (edge.getCondition() != null && !edge.getCondition().isEmpty() && edge.getCondition().equals("no")) {
            String loopRule = "It is obligatory that the task " + cleanText(taskNode.getName()) +
                    " can repeat after encountering the condition '" + cleanText(edge.getCondition()) + "'.\n";
            System.out.println(loopRule);
            sbvrDataBuilder.append(loopRule).append("\n");
        }
    }

    /**
     * Cancel Activity und Interrupting Timer Event Pattern Implementierung
     * @param graph
     * @param taskNode
     * @param sbvrDataBuilder
     */
    public void processIntermediateElements(BPMNGraph graph, IntermediateNode taskNode, StringBuilder sbvrDataBuilder) {
        for (Edge edge : graph.getEdges()) {
            // Prüfen, ob die Kante ein IntermediateNode als Ziel hat und die Kante keine spezielle Kante ist
            if (edge.getTarget() instanceof IntermediateNode intermediateNode && edge.getSource().equals(taskNode) && isNormalEdge(edge)) {
                // Prüfen, ob die Kante bereits besucht wurde (für Loop-Erkennung)
                if (outputEdges.contains(edge)) {
                    continue;  // Weiter mit der nächsten Kante, ohne sie erneut zu verarbeiten
                }

                // Kante als besucht markieren
                outputEdges.add(edge);

                // Zählen der ausgehenden und eingehenden Kanten des IntermediateNodes
                long outgoingEdgesCount = graph.getEdges().stream()
                        .filter(e -> e.getSource().equals(intermediateNode))
                        .count();

                long incomingEdgesCount = graph.getEdges().stream()
                        .filter(e -> e.getTarget().equals(intermediateNode))
                        .count();

                // Debugging-Ausgabe: Zeige die Anzahl der ausgehenden und eingehenden Kanten an
                System.out.println("Outgoing edges count for " + intermediateNode.getName() + ": " + outgoingEdgesCount);
                System.out.println("Incoming edges count for " + intermediateNode.getName() + ": " + incomingEdgesCount);

                // Wenn genau eine eingehende und eine ausgehende Kante existieren
                if (outgoingEdgesCount == 1 && incomingEdgesCount == 1) {
                    Node sourceNode = edge.getSource();
                    Node targetNode = edge.getTarget();

                    String sourceLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";
                    String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                    // SBVR-Regel erzeugen
                    String rule = "It is obligatory that \"" + targetLane + "\" performs \"" + cleanText(intermediateNode.getName()) +
                            "\" after \"" + sourceLane + "\" performs \"" + cleanText(sourceNode.getName()) + "\", where the event interrupts the activity.\n";

                    // Ausgabe und Anhängen an den StringBuilder
                    System.out.println(rule);
                    sbvrDataBuilder.append(rule).append("\n");
                }
            }
        }
    }



    private void processIncomingEdges(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        for (Edge edge : graph.getEdges()) {
            // Prüfen, ob die Kante zum aktuellen TaskNode führt und keine spezielle Edge ist
            if (edge.getTarget().equals(taskNode) && isNormalEdge(edge)) {
                // Prüfen, ob die Kante bereits besucht wurde (für Loop-Erkennung)
                if (outputEdges.contains(edge)) {
                    continue;  // Weiter mit der nächsten Kante, ohne sie erneut zu verarbeiten
                }

                // Kante als besucht markieren
                outputEdges.add(edge);

                Node sourceNode = edge.getSource();
                Node targetNode = edge.getTarget();

                String sourceLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";
                String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                String rule = "It is obligatory that \"" + targetLane + "\" performs \"" + cleanText(taskNode.getName()) +
                        "\" after \"" + sourceLane + "\" performs \"" + cleanText(sourceNode.getName()) + "\".\n";
                System.out.println(rule);
                sbvrDataBuilder.append(rule).append("\n");
            }
        }
    }

    // Überprüft, ob die Kante eine normale Edge ist (keine DataEdge, MessageEdge oder GatewayEdge)
    private boolean isNormalEdge(Edge edge) {
        return !(edge instanceof DataEdge || edge instanceof MessageEdge || edge instanceof GatewayEdge);
    }


    private void processOutgoingEdgesForStartEvent(BPMNGraph graph, Node startEventNode, StringBuilder sbvrDataBuilder) {
        if (!"StartEvent".equals(startEventNode.getType())) {
            return; // Stelle sicher, dass es wirklich ein StartEvent ist
        }
        String rule = "It is obligatory that the Process starts with \"" + cleanText(startEventNode.getName()) + "\".\n";
        System.out.println(rule);
        sbvrDataBuilder.append(rule).append("\n");
    }




    // SBVR-Regeln für eingehende Kanten von EndEvents
    private void processIncomingEdgesForEndEvent(BPMNGraph graph, EndEventNode endEventNode, StringBuilder sbvrDataBuilder) {

        for (Edge edge : graph.getEdges()) {
            if (edge.getTarget().equals(endEventNode)) {
                if (!outputEdges.contains(edge)) {
                    String rule = "It is obligatory the Process ends with \"" + cleanText(endEventNode.getName()) + "\".\n";
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

                    String rule = "It is obligatory that \"" + targetLane + "\" \"" + cleanText(targetNode.getName()) +
                            "\" after \"" + sourceLane + "\" \""  + cleanText(taskNode.getName()) + "\" and if the service is automated completed.\n";
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

                    String rule = "It is obligatory that \"" + targetLane + "\" \"" + cleanText(targetNode.getName()) +
                            "\" is executed only if \"" + sourceLane + "\" \"" + cleanText(taskNode.getName()) +
                            "\" has been successfully completed";
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
                    String rule = "It is obligatory that \"" + targetLane + "\" \"" + cleanText(targetNode.getName()) +
                            "\" after \"" + sourceLane + "\" \"" + cleanText(sourceNode.getName()) + "\" and if the requirements for the activity have been checked.\n";
                    System.out.print(rule);
                    sbvrDataBuilder.append(rule).append("\n");
            }
        }
    }

    /**
     * Erkennung folgender Patterns:
     * Task to Enviroment - Push Orientied: Message Flow vom Task zum Pool
     * Enviroment to Task - Pull Orientied: Message Flow vom Task zum Pool und zurück vom Pool zum Task
     * Task to Enviroment - Pull Orientied: Message Flow vom Pool zum Task und zurück vom Task zum Pool
     * Enviroment to Task - Push Orientied: Message Flow vom Pool zum Task
     */
    public void processMessageEdges(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        // Set zum Verfolgen bereits verarbeiteter bidirektionaler Flüsse
        Set<String> processedEdges = new HashSet<>();

        for (Edge edge : graph.getEdges()) {
            String header = "SBVR-Regeln für MessageEdges:";

            // Überprüfe, ob die Kante eine MessageEdge ist
            if (edge instanceof MessageEdge) {
                Node sourceNode = edge.getSource();
                Node targetNode = edge.getTarget();

                // Hole die Lane-Informationen
                String sourceLane = sourceNode.getLane() != null ? sourceNode.getLane().getName() : "Unbekannte Lane";
                String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                // Überprüfe, ob es eine bidirektionale Kante gibt (vom Task zum Pool und zurück)
                boolean hasReverseEdge = false;
                for (Edge reverseEdge : graph.getEdges()) {
                    if (reverseEdge instanceof MessageEdge) {
                        Node reverseSourceNode = reverseEdge.getSource();
                        Node reverseTargetNode = reverseEdge.getTarget();
                        // Stelle sicher, dass der Fluss in der richtigen Reihenfolge ist
                        if (reverseSourceNode.equals(targetNode) && reverseTargetNode.equals(sourceNode)) {
                            hasReverseEdge = true;
                            break;
                        }
                    }
                }

                // 1. Task to Environment - Push Oriented: Message Flow vom Task zum Pool
                if (sourceNode instanceof TaskNode && targetNode instanceof ParticipantNode && !hasReverseEdge) {
                    // Ausgabe für das erkannte Pattern
                    System.out.println("Pattern erkannt: Task to Environment - Push Oriented (Message Flow vom Task zum Pool)");

                    // Generiere die SBVR-Regel für das Senden (Task sendet Nachricht an Pool)
                    String sendRule = "It is permitted that \"" + cleanText(sourceNode.getName()) + "\" sends a message to \"" +
                            cleanText(targetNode.getName()) + "\".\n";

                    // Ausgabe und Speichern der Regel für das Senden
                    System.out.println(sendRule);
                    sbvrDataBuilder.append(sendRule).append("\n");
                }

                // 2. Environment to Task - Pull Oriented: Message Flow vom Task zum Pool und zurück vom Pool zum Task
                if (hasReverseEdge && sourceNode instanceof TaskNode && targetNode instanceof ParticipantNode) {
                    // Ausgabe für das erkannte Pattern
                    System.out.println("Pattern erkannt: Environment to Task - Pull Oriented (Message Flow vom Task zum Pool und zurück vom Pool zum Task)");

                    // Generiere die SBVR-Regel für das Senden (Task sendet Nachricht an Pool und empfängt zurück)
                    String bidirectionalRule = "It is permitted that \"" + cleanText(sourceNode.getName()) + "\" sends and receives messages to and from \"" +
                            cleanText(targetNode.getName()) + "\".\n";

                    // Ausgabe und Speichern der Regel für den bidirektionalen Flow
                    System.out.println(bidirectionalRule);
                    sbvrDataBuilder.append(bidirectionalRule).append("\n");
                }

                // 3. Task to Environment - Pull Oriented: Message Flow vom Pool zum Task und zurück vom Task zum Pool
                if (sourceNode instanceof ParticipantNode && targetNode instanceof TaskNode && hasReverseEdge) {
                    // Ausgabe für das erkannte Pattern
                    System.out.println("Pattern erkannt: Task to Environment - Pull Oriented (Message Flow vom Pool zum Task und zurück vom Task zum Pool)");

                    // Generiere die SBVR-Regel für das Senden (Pool sendet Nachricht an Task und empfängt zurück)
                    String bidirectionalRule = "It is permitted that \"" + cleanText(sourceNode.getName()) + "\" sends and receives messages to and from \"" +
                            cleanText(targetNode.getName()) + "\".\n";

                    // Ausgabe und Speichern der Regel für den bidirektionalen Flow
                    System.out.println(bidirectionalRule);
                    sbvrDataBuilder.append(bidirectionalRule).append("\n");
                }

                // 4. Environment to Task - Push Oriented: Message Flow vom Pool zum Task
                if (sourceNode instanceof ParticipantNode && targetNode instanceof TaskNode && !hasReverseEdge) {
                    // Ausgabe für das erkannte Pattern
                    System.out.println("Pattern erkannt: Environment to Task - Push Oriented (Message Flow vom Pool zum Task)");

                    // Generiere die SBVR-Regel für das Senden (Pool sendet Nachricht an Task)
                    String sendRule = "It is permitted that \"" + cleanText(sourceNode.getName()) + "\" sends a message to \"" +
                            cleanText(targetNode.getName()) + "\".\n";

                    // Ausgabe und Speichern der Regel für das Senden
                    System.out.println(sendRule);
                    sbvrDataBuilder.append(sendRule).append("\n");
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
