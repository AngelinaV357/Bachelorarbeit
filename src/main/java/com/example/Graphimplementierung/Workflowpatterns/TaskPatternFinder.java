package com.example.Graphimplementierung.Workflowpatterns;

import com.example.Graphimplementierung.Grundstruktur.Nodes.*;

import java.util.HashSet;
import java.util.Set;

public class TaskPatternFinder {

    private static final Set<Edge> outputEdges = new HashSet<>();
    private Set<Node> gatewayProcessedNodes;

    // Hauptmethode zum Finden aller Task-Typen

//    public void findAllTaskPatterns(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
//        // Fallback-Sicherheitsprüfung
//        if (gatewayProcessedNodes == null) {
//            gatewayProcessedNodes = new HashSet<>();
//        }
//
//        for (Node node : graph.getNodes()) {
//            // Prüfe, ob der Knoten bereits in den Gateway-Prozessen verarbeitet wurde
//            if (gatewayProcessedNodes.contains(node)) {
//                System.out.println("Überspringe bereits verarbeiteten Knoten: " + cleanText(node.getName()));
//                continue; // Überspringe diese Knoten
//            }
//
//            // StartEventNode erkennen und verarbeiten
//            if (node instanceof StartEventNode startEventNode) {
//                processOutgoingEdgesForStartEvent(graph, startEventNode, sbvrDataBuilder);
//            }
//
//            if (node instanceof IntermediateNode intermediateNode) {
//                System.out.println("IntermediateNode gefunden: " + cleanText(intermediateNode.getName()));  // Überprüfen, ob der IntermediateNode erkannt wird
//                processIntermediateElements(graph, intermediateNode, sbvrDataBuilder);
//            }

//            if (node instanceof TaskNode taskNode) {
//                // Hier entscheidet der activityType des TaskNode, welche Methode ausgeführt wird
//                switch (taskNode.getActivityType()) {
//                    case "UserTask" -> {
//                        processOutgoingEdgesForUserTask(graph, taskNode, sbvrDataBuilder);
//                        processIncomingEdges(graph, taskNode, sbvrDataBuilder);
//                    }
//                    case "ServiceTask" -> {
//                        processOutgoingEdgesForServiceTask(graph, taskNode, sbvrDataBuilder);
//                        processIncomingEdges(graph, taskNode, sbvrDataBuilder);
//                    }
//                    case "BusinessRuleTask" -> {
//                        processOutgoingEdgesForBusinessRuleTask(graph, taskNode, sbvrDataBuilder);
//                        processIncomingEdges(graph, taskNode, sbvrDataBuilder);
//                    }
//                    case "Task" -> {
//                        // Hier wird der spezielle Task (Sequence) Prozess aufgerufen
//                        processSequenceTask(graph, sbvrDataBuilder, taskNode);
//                    }
//                    default -> {
//                        System.out.println("Unbekannter Task-Typ: " + taskNode.getActivityType());
//                    }
//                }

            // EndEventNode erkennen und verarbeiten
//            if (node instanceof EndEventNode endEventNode) {
//                processIncomingEdgesForEndEvent(graph, endEventNode, sbvrDataBuilder);
//            }
//            gatewayProcessedNodes.add(node);
//        }

    public void processTaskNode(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        if (gatewayProcessedNodes == null) {
            gatewayProcessedNodes = new HashSet<>();
        }

        // Durchlaufe alle Knoten im Graphen
        for (Node node : graph.getNodes()) {
            // Nur TaskNodes verarbeiten
            if (node instanceof TaskNode taskNode && !gatewayProcessedNodes.contains(taskNode)) {
                switch (taskNode.getActivityType()) {
                    case "UserTask" -> {
                        processOutgoingEdgesForUserTask(graph, taskNode, sbvrDataBuilder);
                        //processIncomingEdges(graph, taskNode, sbvrDataBuilder);
                    }
                    case "ServiceTask" -> {
                        processOutgoingEdgesForServiceTask(graph, taskNode, sbvrDataBuilder);
                        //processIncomingEdges(graph, taskNode, sbvrDataBuilder);
                    }
                    case "BusinessRuleTask" -> {
                        processOutgoingEdgesForBusinessRuleTask(graph, taskNode, sbvrDataBuilder);
                        //processIncomingEdges(graph, taskNode, sbvrDataBuilder);
                    }
                }

                // Markiere den TaskNode als verarbeitet
                gatewayProcessedNodes.add(taskNode);
            }
        }
    }


    public void processSequenceTask(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        if (gatewayProcessedNodes == null) {
            gatewayProcessedNodes = new HashSet<>();
        }

        // Durchlaufe alle Knoten im Graphen
        for (Node node : graph.getNodes()) {
            // Nur TaskNodes verarbeiten, die keine spezifischen Task-Typen sind (Sequence Pattern)
            if (node instanceof TaskNode taskNode && !gatewayProcessedNodes.contains(taskNode) && "Task".equals(taskNode.getActivityType())) {
                processOutgoingEdgesForTask(graph, taskNode, sbvrDataBuilder);
                processIncomingEdges(graph, taskNode, sbvrDataBuilder);

                // Markiere den TaskNode als verarbeitet
                gatewayProcessedNodes.add(taskNode);

                // Ausgabe der verarbeiteten Sequence TaskNode
                String message = "Verarbeitete Sequence Task Nodes: " + cleanText(taskNode.getName()) + "\n";
                System.out.print(message);
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
                String sourceLane = (taskNode.getLane() != null) ? taskNode.getLane().getName() : null;
                String targetLane = (targetNode.getLane() != null) ? targetNode.getLane().getName() : null;

                String rule;
                if (sourceLane == null || targetLane == null) {
                    rule = (condition != null && !condition.isEmpty())
                            ? "It is obligatory that \"" + cleanText(targetNode.getName()) +
                            "\" after \"" + cleanText(taskNode.getName()) +
                            "\" and if \"" + cleanText(condition) + ".\n"
                            : "It is obligatory that \"" + cleanText(targetNode.getName()) +
                            "\" after \"" + cleanText(taskNode.getName()) + "\".\n";
                } else {
                    rule = (condition != null && !condition.isEmpty())
                            ? "It is obligatory that \"" + cleanText(targetLane) + "\" \"" + cleanText(targetNode.getName()) +
                            "\" after \"" + cleanText(sourceLane) + "\" \"" + cleanText(taskNode.getName()) +
                            "\" and if \"" + cleanText(condition) + ".\n"
                            : "It is obligatory that \"" + cleanText(targetLane) + "\" performs \"" + cleanText(targetNode.getName()) +
                            "\" after \"" + cleanText(sourceLane) + "\" performs \"" + cleanText(taskNode.getName()) + "\".\n";
                }

                System.out.println(rule);
                sbvrDataBuilder.append(rule).append("\n");

                // Kante als besucht markieren
                outputEdges.add(edge);

                // Auslagerung der Loop-Bedingung in eine separate Methode
                detectLoopForTask(taskNode, edge, sbvrDataBuilder);
            }
        }
    }

    // Diese Methode erkennt, ob ein Loop vorliegt und gibt die entsprechende Regel aus
    private void detectLoopForTask(TaskNode taskNode, Edge edge, StringBuilder sbvrDataBuilder) {
        // Prüfen, ob die Kante zu einem bereits besuchten Task führt (Loop)
        if (outputEdges.contains(edge) && edge.getTarget().equals(taskNode)) {
            // Überprüfen der Bedingung, ob es sich um einen Loop handelt
            if (edge.getCondition() != null && !edge.getCondition().isEmpty() && edge.getCondition().equals("no")) {
                String loopRule = "It is obligatory that the task " + cleanText(taskNode.getName()) +
                        " can repeat after encountering the condition '" + cleanText(edge.getCondition()) + "'.\n";
                System.out.println(loopRule);
                sbvrDataBuilder.append(loopRule).append("\n");
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

                // Ausgabe für das erkannte Pattern
                System.out.println("\nPattern erkannt: Sequence");

                String rule = "\nIt is obligatory that \"" + targetLane + "\" performs \"" + cleanText(taskNode.getName()) +
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

    /**
     * Beschreibt das Pattern Commencement on Creation
     * @param graph
     * @param sbvrDataBuilder
     */
    public void processOutgoingEdgesForStartEvent(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        // Durch alle Knoten im Graph gehen und nach StartEventNodes suchen
        graph.getNodes().forEach(node -> {
            if (node instanceof StartEventNode) {
                StartEventNode startEventNode = (StartEventNode) node;

                // Sicherstellen, dass es wirklich ein StartEvent ist
                if ("StartEvent".equals(startEventNode.getType())) {
                    System.out.println("\nPattern erkannt: Commencement on Creation");
                    String rule = "It is obligatory that the Process starts with \"" + cleanText(startEventNode.getName()) + "\".\n";
                    System.out.println(rule);
                    sbvrDataBuilder.append(rule).append("\n");
                }
            }
        });
    }


    /**
     * Beschreibt das Pattern Explicit Termination: Der gesamte Prozess wird abgebrochen
     * @param graph
     * @param sbvrDataBuilder
     */
    public void processIncomingEdgesForEndEvent(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        // Set zur Vermeidung von Duplikaten
        HashSet<Edge> outputEdges = new HashSet<>();

        // Durch alle Knoten im Graph gehen und nach EndEventNodes suchen
        graph.getNodes().forEach(node -> {
            if (node instanceof EndEventNode) {
                EndEventNode endEventNode = (EndEventNode) node;

                // Alle Kanten im Graph durchgehen und nach den eingehenden Kanten suchen
                for (Edge edge : graph.getEdges()) {
                    if (edge.getTarget().equals(endEventNode)) {
                        if (!outputEdges.contains(edge)) {
                            System.out.println("Pattern erkannt: Explicit Termination");
                            String rule = "\nIt is obligatory the Process ends with \"" + cleanText(endEventNode.getName()) + "\".\n";
                            System.out.println(rule);
                            sbvrDataBuilder.append(rule).append("\n");
                            outputEdges.add(edge); // Kante hinzufügen, um Duplikate zu vermeiden
                        }
                    }
                }
            }
        });
    }


    /**
     * Automatic Execution: Automatische Ausführung ohne menschliche Intervention
     */
    private void processOutgoingEdgesForServiceTask(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                    Node targetNode = edge.getTarget();
                    Node sourceNode = edge.getSource();

                    String sourceLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";
                    String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                    System.out.println("Pattern erkannt: Automatic Execution");
                    String rule = "It is obligatory that \"" + targetLane + "\" \"" + cleanText(targetNode.getName()) +
                            "\" after \"" + sourceLane + "\" \""  + cleanText(taskNode.getName()) + "\" and if the service is automatically completed.\n";
                    System.out.println(rule);
                    sbvrDataBuilder.append(rule).append("\n");
            }
        }
    }

    /**
     * Role-Based Allocation: Eine Aufgabe wird eine Rolle zugewiesen, diese Rolle muss diese Aktivität ausführen
     */
    private void processOutgoingEdgesForUserTask(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                Node targetNode = edge.getTarget();
                Node sourceNode = edge.getSource();

                String sourceLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";
                String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                String pattern = "Pattern erkannt: Role-Based Allocation";
                String rule = "It is obligatory that \"" + targetLane + "\" \"" + cleanText(targetNode.getName()) +
                            "\" is executed only if \"" + sourceLane + "\" \"" + cleanText(taskNode.getName()) +
                            "\" has been completed successfully.";
                // Gib das Pattern und die Regel untereinander aus
                String finalOutput = pattern + "\n" + rule;

                // Ausgabe in der Konsole und Hinzufügen zum StringBuilder
                System.out.println(finalOutput);
                sbvrDataBuilder.append(finalOutput).append("\n");
            }
        }
    }


    /**
     * Beschreibt das Automatic Execution Pattern: dieser prüft automatisch die Anforderung der Aktivität
     * @param graph
     * @param taskNode
     * @param sbvrDataBuilder
     */
    private void processOutgoingEdgesForBusinessRuleTask(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(taskNode)) {
                    Node sourceNode = edge.getSource();
                    Node targetNode = edge.getTarget();

                    // Hole die Lane des Quell- und Zielknotens
                    String sourceLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";
                    String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                System.out.println("Pattern erkannt: Automatic Execution");
                    // SBVR-Regel für BusinessRuleTask
                    String rule = "\nIt is obligatory that \"" + targetLane + "\" \"" + cleanText(targetNode.getName()) +
                            "\" after \"" + sourceLane + "\" \"" + cleanText(sourceNode.getName()) + "\" and if the requirements for the activity have been validated.";
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
