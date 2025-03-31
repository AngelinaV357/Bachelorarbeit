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

        // Durchlaufe alle Knoten im Graphen für Sequence Flows
        for (Node node : graph.getNodes()) {
            // Nur TaskNodes verarbeiten, die keine spezifischen Task-Typen sind (Sequence Pattern)
            if (node instanceof TaskNode taskNode && !gatewayProcessedNodes.contains(taskNode) && "Task".equals(taskNode.getActivityType())) {
                processOutgoingEdgesForSequence(graph, taskNode, sbvrDataBuilder);  // Sequence Flow Verarbeitung
                processIncomingEdgesForSequence(graph, taskNode, sbvrDataBuilder);  // Eingehende Sequence Flows

                // Markiere den TaskNode als verarbeitet
                gatewayProcessedNodes.add(taskNode);

                // Ausgabe der verarbeiteten Sequence TaskNode
                //String message = "Verarbeitete Sequence Task Nodes: " + cleanText(taskNode.getName()) + "\n";
                //System.out.print(message);
            }
        }

        // Schleifenmuster-Verarbeitung nach der Sequence Flow Verarbeitung
        processLoopPatterns(graph, sbvrDataBuilder);
    }

    private void processOutgoingEdgesForSequence(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        for (Edge edge : graph.getEdges()) {
            // Prüfen, ob die Kante von der aktuellen Aufgabe ausgeht
            if (edge.getSource().equals(taskNode) && isNormalEdge(edge)) {
                if (outputEdges.contains(edge)) {
                    continue; // Überspringe bereits besuchte Kanten
                }

                Node targetNode = edge.getTarget();
                String sourceLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";
                String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                String rule = "It is obligatory that \"" +  targetLane +  "\" performs \"" + cleanText(targetNode.getName()) +
                        "\" after \"" + sourceLane + "\" performs \"" + cleanText(taskNode.getName()) + "\".\n";
                System.out.println(rule);
                sbvrDataBuilder.append(rule).append("\n");

                outputEdges.add(edge);  // Kante als verarbeitet markieren
            }
        }
    }

    private void processIncomingEdgesForSequence(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        for (Edge edge : graph.getEdges()) {
            // Prüfen, ob die Kante zum aktuellen TaskNode führt
            if (edge.getTarget().equals(taskNode) && isNormalEdge(edge)) {
                if (outputEdges.contains(edge)) {
                    continue;  // Überspringe bereits besuchte Kanten
                }

                Node sourceNode = edge.getSource();
                String sourceLane = sourceNode.getLane() != null ? sourceNode.getLane().getName() : "Unbekannte Lane";
                String targetLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";

                String rule = "It is obligatory that \""  + targetLane + "\" performs \"" + cleanText(taskNode.getName())+
                        "\" after \"" +  sourceLane + "\" performs \"" +  cleanText(sourceNode.getName())  + ".\n";
                System.out.println(rule);
                sbvrDataBuilder.append(rule).append("\n");

                outputEdges.add(edge);  // Kante als verarbeitet markieren
            }
        }
    }


    private void processLoopPatterns(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        Set<String> detectedLoops = new HashSet<>(); // Set zur Vermeidung doppelter Erkennung

        // Über alle Knoten iterieren
        for (Node node : graph.getNodes()) {
            if (node instanceof TaskNode taskNode && !gatewayProcessedNodes.contains(taskNode)) {
                for (Edge edge : graph.getEdges()) {
                    if (edge.getSource().equals(taskNode) && isNormalEdge(edge)) {
                        Node targetNode = edge.getTarget();

                        // Prüfen, ob eine valide Schleife existiert
                        if (detectLoop(graph, targetNode)) {
                            // Erzeuge eine eindeutige Signatur für die erkannte Schleife
                            String loopSignature = taskNode.getName() + " -> " + targetNode.getName();
                            if (!detectedLoops.contains(loopSignature)) {
                                detectedLoops.add(loopSignature);

                                System.out.println("\nPattern erkannt: Structured Loop");
                                String loopRule = "\nIt is obligatory that the task \"" + cleanText(taskNode.getName()) +
                                        "\" can repeat after encountering \"" + cleanText(targetNode.getName()) + "\".\n";
                                System.out.println(loopRule);
                                sbvrDataBuilder.append(loopRule).append("\n");
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean detectLoop(BPMNGraph graph, Node startNode) {
        Set<Node> visited = new HashSet<>();
        return checkLoop(graph, startNode, visited);
    }

    private boolean checkLoop(BPMNGraph graph, Node currentNode, Set<Node> visited) {
        if (visited.contains(currentNode)) {
            System.out.println("Loop erkannt bei Knoten: " + currentNode.getName());
            return true;  // Schleife erkannt
        }

        visited.add(currentNode);  // Knoten als besucht markieren

        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(currentNode) && isNormalEdge(edge)) {
                if (checkLoop(graph, edge.getTarget(), visited)) {
                    return true;
                }
            }
        }

        visited.remove(currentNode);  // Nach der Prüfung entfernen
        return false;
    }


    /**
     * Block Data Pattern: bezieht sich auf das Konzept, bei dem Daten innerhalb eines Subprozesses gesammelt und dann in einem einzigen Block verarbeitet oder gespeichert werden,
     * bevor sie an den Hauptprozess oder andere Subprozesse weitergegeben werden
     * @param graph
     * @param sbvrDataBuilder
     */
    public void processSubProcess(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        if (gatewayProcessedNodes == null) {
            gatewayProcessedNodes = new HashSet<>();
        }

        // Durchlaufe alle Knoten im Graphen
        for (Node node : graph.getNodes()) {
            // Verarbeite nur Subprozesse
            if (node instanceof TaskNode taskNode && !gatewayProcessedNodes.contains(taskNode)) {

                // Überprüfen, ob der Knoten vom Typ Subprozess ist
                if ("SubProcess".equals(taskNode.getActivityType())) {
                    System.out.println("\nPattern erkannt: Block Data");
                    String sbvrRule = String.format(
                            "It is obligatory, that the data defined in the properties of the subprocess  is blocked until \"%s\".",
                            cleanText(taskNode.getName())
                    );
                    sbvrDataBuilder.append(sbvrRule).append("\n");

                    // Optional: Ausgabe zur Bestätigung der Regel
                    //System.out.println("SBVR-Regel für Subprozess: " + sbvrRule);
                }

                // Markiere den TaskNode als verarbeitet
                gatewayProcessedNodes.add(taskNode);

                // Ausgabe der verarbeiteten Subprozess-TaskNode
                //String message = "Verarbeitete Subprozess Task Node: " + cleanText(taskNode.getName()) + "\n";
                //System.out.print(message);
            }
        }
    }


    public void processIntermediateEvents(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        if (gatewayProcessedNodes == null) {
            gatewayProcessedNodes = new HashSet<>();
        }

        // Durchlaufe alle Knoten im Graphen
        for (Node node : graph.getNodes()) {
            // Überprüfen, ob der Knoten ein IntermediateNode ist und noch nicht verarbeitet wurde
            if (node instanceof TaskNode taskNode && !gatewayProcessedNodes.contains(taskNode)) {
                // Rufe detectIntermediateEvent auf, um Intermediate Events zu erkennen
                detectTriggerPatterns(graph, taskNode, sbvrDataBuilder);

                // Markiere den IntermediateNode als verarbeitet
                gatewayProcessedNodes.add(taskNode);

                // Ausgabe der verarbeiteten IntermediateNode
                //String message = "Verarbeitete Intermediate Event Nodes: " + cleanText(taskNode.getName()) + "\n";
                //System.out.print(message);
            }
        }
    }




    /**
     * Event-Based Trigger Pattern und Persistent Trigger Pattern:
     * - Event-Based Trigger Pattern, wenn nach einem Event-Based Gateway ein Intermediate Event folgt
     * - Persistent Trigger Pattern, wenn ein Intermediate Catch Event (Message) eintrifft
     * @param graph
     * @param taskNode
     * @param sbvrDataBuilder
     */
    public void detectTriggerPatterns(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
        if (gatewayProcessedNodes == null) {
            gatewayProcessedNodes = new HashSet<>();
        }

        // Durchlaufe alle Kanten im Graphen
        for (Edge edge : graph.getEdges()) {
            // Prüfen, ob die Kante den aktuellen TaskNode als Quelle hat
            if (edge.getSource().equals(taskNode)) {
                Node targetNode = edge.getTarget();

                // Event-Based Trigger Pattern: Prüfen, ob nach einem Event-Based Gateway ein Intermediate Event kommt
                if (targetNode instanceof IntermediateNode intermediateNode &&
                        "IntermediateCatchEvent".equals(intermediateNode.getEventType())) {

                    // Prüfen, ob das Intermediate Event ein Event-Based Gateway aktiviert
                    if ("EventBased".equals(intermediateNode.getEventSubType())) {
                        // Überprüfen, ob die Kante bereits besucht wurde (für Loop-Erkennung)
                        if (outputEdges.contains(edge)) {
                            continue;  // Weiter mit der nächsten Kante, ohne sie erneut zu verarbeiten
                        }

                        // Kante als besucht markieren
                        outputEdges.add(edge);

                        String sourceLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";
                        String targetLane = intermediateNode.getLane() != null ? intermediateNode.getLane().getName() : "Unbekannte Lane";

                        System.out.println("\nPattern erkannt: Event-Based Trigger");

                        // SBVR-Regel erzeugen: Event-Based Trigger
                        String rule = "It is obligatory that \"" + targetLane + "\" performs \"" + cleanText(intermediateNode.getName()) +
                                "\" is triggered after \"" + sourceLane + "\" performs \"" + cleanText(taskNode.getName()) + "\", where the event-based gateway is followed by the event.\n";

                        // Ausgabe und Anhängen an den StringBuilder
                        System.out.println(rule);
                        sbvrDataBuilder.append(rule).append("\n");
                    }
                }

                // Persistent Trigger Pattern: Prüfen, ob das Ziel ein Intermediate Catch Event vom Typ Message ist
                if (targetNode instanceof IntermediateNode intermediateNode &&
                        "IntermediateCatchEvent".equals(intermediateNode.getEventType()) &&
                        "Message".equals(intermediateNode.getEventSubType())) {

                    // Überprüfen, ob der Knoten bereits verarbeitet wurde
                    if (gatewayProcessedNodes.contains(taskNode)) {
                        continue; // Weiter mit der nächsten Kante, wenn der Knoten schon verarbeitet wurde
                    }

                    // Markiere den Knoten als verarbeitet
                    gatewayProcessedNodes.add(taskNode);

                    // Erstelle die SBVR-Regel für das Persistent Trigger Pattern
                    String sourceLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";
                    String targetLane = intermediateNode.getLane() != null ? intermediateNode.getLane().getName() : "Unbekannte Lane";

                    System.out.println("\nPattern erkannt: Persistent Trigger");

                    // SBVR-Regel erzeugen: Das Ereignis wird die Aufgabe „unterbrechen“, bis die Nachricht eintrifft.
                    String rule = "It is obligatory that \"" + targetLane + "\" \"" + cleanText(intermediateNode.getName()) +
                            "\" persists until \"" + sourceLane + "\" \"" + cleanText(taskNode.getName()) + ".\n";

                    // Ausgabe und Anhängen an den StringBuilder
                    System.out.println(rule);
                    sbvrDataBuilder.append(rule).append("\n");
                }
            }
        }
    }

//    public void processChainedExecution(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
//        if (gatewayProcessedNodes == null) {
//            gatewayProcessedNodes = new HashSet<>();
//        }
//
//        // Durchlaufe alle Knoten im Graphen
//        for (Node node : graph.getNodes()) {
//            // Nur TaskNodes verarbeiten, die keine spezifischen Task-Typen sind (für Chained Execution)
//            if (node instanceof TaskNode taskNode && !gatewayProcessedNodes.contains(taskNode) && "Task".equals(taskNode.getActivityType())) {
//                // Rufe die Methode für Chained Execution auf
//                processChainedExecution(graph, taskNode, sbvrDataBuilder);
//
//                // Markiere den TaskNode als verarbeitet
//                gatewayProcessedNodes.add(taskNode);
//
//                // Ausgabe der verarbeiteten Chained Execution TaskNode
//                String message = "Verarbeitete Chained Execution Task Nodes: " + cleanText(taskNode.getName()) + "\n";
//                System.out.print(message);
//            }
//        }
//    }
//
//
//    private void processChainedExecution(BPMNGraph graph, TaskNode taskNode, StringBuilder sbvrDataBuilder) {
//        // Variablen zur Verfolgung der Kette und der Lane
//        String currentLane = taskNode.getLane() != null ? taskNode.getLane().getName() : "Unbekannte Lane";
//        boolean isChainedExecution = true;
//        TaskNode previousTaskNode = taskNode;
//
//        // Iteriere über alle Kanten im Graphen
//        for (Edge edge : graph.getEdges()) {
//            // Prüfen, ob die Kante eine normale Edge ist und das Ziel die aktuelle TaskNode ist
//            if (edge.getSource().equals(previousTaskNode) && isNormalEdge(edge)) {
//                Node sourceNode = edge.getSource();
//                Node targetNode = edge.getTarget();
//
//                // Prüfen, ob die Kante bereits besucht wurde (für Loop-Erkennung)
//                if (outputEdges.contains(edge)) {
//                    continue;  // Weiter mit der nächsten Kante, ohne sie erneut zu verarbeiten
//                }
//
//                // Kante als besucht markieren
//                outputEdges.add(edge);
//
//                // Prüfen, ob die Tasks in der gleichen Lane sind
//                String sourceLane = sourceNode.getLane() != null ? sourceNode.getLane().getName() : "Unbekannte Lane";
//                String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";
//
//                // Wenn die Tasks nicht in der gleichen Lane sind, breche das Chained Execution-Muster ab
//                if (!sourceLane.equals(targetLane)) {
//                    isChainedExecution = false;
//                    break;
//                }
//
//                // Wenn das Chained Execution-Muster gültig ist, gib die Regel aus
//                String rule = "\nIt is obligatory that \"" + targetLane + "\" performs \"" + cleanText(targetNode.getName()) +
//                        "\" after \"" + sourceLane + "\" performs \"" + cleanText(sourceNode.getName()) + "\".\n";
//
//                // Ausgabe des Musters und Anhängen der Regel an den StringBuilder
//                System.out.println("\nPattern erkannt: Chained Execution");
//                System.out.println(rule);
//                sbvrDataBuilder.append(rule).append("\n");
//
//                // Setze das aktuelle Node als vorherige Node für die nächste Iteration
//                previousTaskNode = (TaskNode) targetNode;
//            }
//        }
//    }


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
    // Diese Methode ist jetzt ähnlich der `processIntermediateEvents` Methode und verarbeitet Boundary Events.
    public void processBoundaryEvent(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        if (gatewayProcessedNodes == null) {
            gatewayProcessedNodes = new HashSet<>();
        }

        // Durchlaufe alle Knoten im Graphen
        for (Node node : graph.getNodes()) {
            // Nur BoundaryEventNodes verarbeiten
            if (node instanceof BoundaryEventNode boundaryEventNode && !gatewayProcessedNodes.contains(boundaryEventNode)) {
                processOutgoingEdgesForBoundaryEvent(graph, boundaryEventNode, sbvrDataBuilder);

                // Markiere den BoundaryEventNode als verarbeitet
                gatewayProcessedNodes.add(boundaryEventNode);

                // Ausgabe der verarbeiteten Boundary Event Node
                //tring message = "Verarbeitetes Boundary Event: " + cleanText(boundaryEventNode.getName()) + "\n";
                //System.out.print(message);
            }
        }
    }

    private void processOutgoingEdgesForBoundaryEvent(BPMNGraph graph, BoundaryEventNode boundaryEventNode, StringBuilder sbvrDataBuilder) {
        for (Edge edge : graph.getEdges()) {
            // Prüfen, ob die Kante von dem BoundaryEventNode ausgeht
            if (edge.getSource().equals(boundaryEventNode) && isNormalEdge(edge)) {
                // Prüfen, ob die Kante bereits besucht wurde
                if (outputEdges.contains(edge)) {
                    continue; // Weiter mit der nächsten Kante, ohne sie erneut zu verarbeiten
                }

                // Zielknoten und Bedingung extrahieren
                Node targetNode = edge.getTarget();
                String condition = edge.getCondition();
                String sourceLane = (boundaryEventNode.getLane() != null) ? boundaryEventNode.getLane().getName() : null;
                String targetLane = (targetNode.getLane() != null) ? targetNode.getLane().getName() : null;

                String rule;

                System.out.println("Pattern erkannt: Cancel Activity");
                // Hier wird die Logik angepasst, um ein non-interrupting Boundary Event zu reflektieren
                if (sourceLane == null || targetLane == null) {
                    rule = (condition != null && !condition.isEmpty())
                            ? "It is obligatory that \"" + cleanText(targetNode.getName()) +
                            "\" is triggered by \"" + cleanText(boundaryEventNode.getName()) +
                            "\" and if \"" + cleanText(condition) + ".\n"
                            : "It is obligatory that \"" + cleanText(targetNode.getName()) +
                            "\" is triggered by \"" + cleanText(boundaryEventNode.getName()) + "\".\n";
                } else {
                    rule = (condition != null && !condition.isEmpty())
                            ? "It is obligatory that \"" + cleanText(targetLane) + "\" \"" + cleanText(targetNode.getName()) +
                            "\" is triggered by \"" + cleanText(sourceLane) + "\" \"" + cleanText(boundaryEventNode.getName()) +
                            "\" and if \"" + cleanText(condition) + ".\n"
                            : "It is obligatory that \"" + cleanText(targetLane) + "\" performs \"" + cleanText(targetNode.getName()) +
                            "\" triggered by \"" + cleanText(sourceLane) + "\" \"" + cleanText(boundaryEventNode.getName()) + "\".\n";
                }

                // Ausgabe der Regel und Hinzufügen zum StringBuilder
                System.out.println(rule);
                sbvrDataBuilder.append(rule).append("\n");

                // Kante als besucht markieren
                outputEdges.add(edge);
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
            //String header = "SBVR-Regeln für MessageEdges:";

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
                    String bidirectionalRule = "It is permitted that \"" + cleanText(sourceNode.getName()) + "\" first sends a messages to \"" +
                            cleanText(targetNode.getName()) + "\" and then receives a message from \"" + cleanText(targetNode.getName()) +"\"\n";

                    // Ausgabe und Speichern der Regel für den bidirektionalen Flow
                    System.out.println(bidirectionalRule);
                    sbvrDataBuilder.append(bidirectionalRule).append("\n");
                }

                // 3. Task to Environment - Pull Oriented: Message Flow vom Pool zum Task und zurück vom Task zum Pool
                if (sourceNode instanceof ParticipantNode && targetNode instanceof TaskNode && hasReverseEdge) {
                    // Ausgabe für das erkannte Pattern
                    System.out.println("Pattern erkannt: Task to Environment - Pull Oriented (Message Flow vom Pool zum Task und zurück vom Task zum Pool)");

                    // Generiere die SBVR-Regel für das Senden (Pool sendet Nachricht an Task und empfängt zurück)
                    String bidirectionalRule = "It is permitted that \"" + cleanText(sourceNode.getName()) + "\" first receives messages from \"" +
                            cleanText(targetNode.getName()) + "\" and then sends a message to \"" + cleanText(targetNode.getName()) +"\"\n";

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
