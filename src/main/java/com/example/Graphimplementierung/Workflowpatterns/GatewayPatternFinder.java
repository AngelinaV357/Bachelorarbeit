package com.example.Graphimplementierung.Workflowpatterns;

import com.example.Graphimplementierung.Grundstruktur.Nodes.*;

import java.util.*;

public class GatewayPatternFinder {

    // Set, um bereits ausgegebene Gateways nachzuverfolgen
    private static final Set<String> processedGateways = new HashSet<>();
    private static final Set<Node> gatewayCoveredTasks = new HashSet<>();

    /**
     * Hier entweder generateSBVRRules oder generateExklusiveChoice auskommentieren, je nach dem ob man zuerst Databased Pattern oder Exklusive Choice ausgeben möchte
     * Merge Pattern wird immer ausgegeben
     * @param graph
     * @param sbvrData
     */
    public void findExclusiveGatewayPatterns(BPMNGraph graph, StringBuilder sbvrData) {
        for (Node node : graph.getNodes()) {
            if (node instanceof GatewayNode && "Exclusive".equals(((GatewayNode) node).getGatewayType())) {
                GatewayNode gatewayNode = (GatewayNode) node;

                if (!processedGateways.contains(gatewayNode.getId())) {
                    // Generiere die SBVR-Regel für das Gateway
                    generateExclusiveChoiceSplit(graph, gatewayNode, sbvrData);
                    //generateSBVRRules(graph, gatewayNode, sbvrData);
                    generateExclusiveChoiceMerge(graph, gatewayNode, sbvrData);

                    // Extrahiere die abgedeckten Tasks nach der Regelgenerierung
                    extractGatewayTasks(graph, gatewayNode);

                    // Gib die abgedeckten Tasks nach der Ausgabe der SBVR-Regel aus
                    for (Node task : gatewayCoveredTasks) {
                        //.out.println("Gateway deckt Task ab: " + cleanText(task.getName()));
                    }

                    processedGateways.add(gatewayNode.getId());
                }
            }
        }
    }


    /**
     * Data Based Routing Pattern: Es beschreibt die konkrete Regel einer Entscheidung mit Bedingungen
     * @param graph
     * @param gatewayNode
     * @param sbvrData
     */
    private static void generateSBVRRules(BPMNGraph graph, GatewayNode gatewayNode, StringBuilder sbvrData) {

        boolean hasCondition = false;

        // Finde alle ausgehenden Kanten und prüfe, ob die Tasks bereits abgedeckt sind
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(gatewayNode)) {
                String condition = edge.getCondition();
                Node targetNode = edge.getTarget();

                // Vermeide doppelte Tasks
                if (targetNode instanceof TaskNode && gatewayCoveredTasks.contains(targetNode)) {
                    continue; // Task wurde bereits durch das Gateway abgedeckt, überspringe diesen Task
                }

                // Wenn es sich um einen Task handelt, füge ihn zu den abgedeckten Tasks hinzu
                if (targetNode instanceof TaskNode) {
                    gatewayCoveredTasks.add(targetNode);
                }

                // Finde die Quelle der Kante, um die vorherige Aktivität zu erhalten
                String sourceActivityName = "";
                Node sourceNode = edge.getSource();

                String sourceLane = sourceNode.getLane() != null ? sourceNode.getLane().getName() : "Unbekannte Lane";
                String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";

                // Bedingung für das exklusive Gateway
                if ("Exclusive Gateway".equals(cleanText(gatewayNode.getName()))) {
                    // Suche die eingehende Kante, um die Quelle der vorherigen Aktivität zu finden
                    for (Edge incomingEdge : graph.getEdges()) {
                        if (incomingEdge.getTarget().equals(gatewayNode)) {
                            sourceNode = incomingEdge.getSource();
                            sourceActivityName = cleanText(sourceNode.getName());
                            break;
                        }
                    }
                } else {
                    // Regel für den Fall, dass das Gateway einen benutzerdefinierten Namen hat
                    if (condition != null && !condition.isEmpty()) {
                        sourceActivityName = cleanText(sourceNode.getName());
                    }
                }

                // Wenn eine Bedingung existiert, soll die Ausgabe erfolgen
                if (condition != null && !condition.isEmpty()) {
                    // Bedingung existiert, daher Ausgabe erzeugen
                    if (!hasCondition) {
                        //String message = "SBVR-Regeln für " + cleanText(gatewayNode.getName()) + ":";
                        //System.out.println(message);
                        //sbvrData.append(message).append("\n");
                        hasCondition = true; // Markieren, dass eine Bedingung gefunden wurde
                    }

                    String patternMessage = "Pattern erkannt: Data Based Routing";
                    System.out.println(patternMessage);
                    sbvrData.append(patternMessage).append("\n");

                    // Regel mit Bedingung erstellen
                    String message = "It is obligatory that \"" + targetLane + "\" performs \"" + cleanText(targetNode.getName()) +
                            "\" after \"" + sourceLane + "\" performs \"" + cleanText(gatewayNode.getName()) + "\" and if \"" + cleanText(condition) + "\".\n";
                    System.out.println(message);
                    sbvrData.append(message).append("\n");
                }
            }
        }
        // Falls keine Bedingungen gefunden wurden, wird keine Ausgabe erzeugt
    }



    // Methode, um alle Tasks zu speichern, die durch Gateways abgedeckt sind
    private static void extractGatewayTasks(BPMNGraph graph, GatewayNode gatewayNode) {
        // Extrahiere Tasks, die durch das Gateway abgedeckt werden, und markiere sie als bearbeitet
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(gatewayNode)) {
                Node targetNode = edge.getTarget();
                if (!gatewayCoveredTasks.contains(targetNode)) {
                    // Wenn der Task noch nicht bearbeitet wurde, füge ihn hinzu
                    gatewayCoveredTasks.add(targetNode);
                }
            }
        }
    }


    /**
     * Exklusive Choice Pattern (XOR-Split): Eine Entscheidung wird getroffen, welcher von mehreren Pfaden basierend auf einer Bedingung weiterverfolgt wird.
     * @param graph
     * @param gatewayNode
     * @param sbvrData
     */
    private void generateExclusiveChoiceSplit(BPMNGraph graph, GatewayNode gatewayNode, StringBuilder sbvrData) {
        Set<Edge> outgoingEdges = new HashSet<>();
        Set<Edge> incomingEdges = new HashSet<>();

        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(gatewayNode)) {
                outgoingEdges.add(edge);
            }
            if (edge.getTarget().equals(gatewayNode)) {
                incomingEdges.add(edge);
            }
        }

        // XOR-Split (Exclusive Choice)
        if (outgoingEdges.size() > 1) {
            List<String> activityList = new ArrayList<>();
            String sourceActivityName = cleanText(gatewayNode.getName());
            Node sourceNode = null;

            if (gatewayNode.getGatewayType().equalsIgnoreCase("Exclusive")) {
                for (Edge incomingEdge : incomingEdges) {
                    sourceNode = incomingEdge.getSource();
                    sourceActivityName = cleanText(sourceNode.getName());
                    break;
                }
            }

            for (Edge edge : outgoingEdges) {
                Node targetNode = edge.getTarget();
                String targetNodeName = cleanText(targetNode.getName());
                activityList.add("\"" + targetNodeName + "\"");
            }

            // Ausgabe des erkannten Patterns
            String patternMessage = "Pattern erkannt: Exclusive Choice (XOR-Split)\n";
            System.out.println(patternMessage);
            sbvrData.append(patternMessage);

            String finalMessage = "It is obligatory that " + String.join(" or ", activityList) + ", but not both, after \"" + sourceActivityName + "\".";
            System.out.println(finalMessage);
            sbvrData.append(finalMessage).append("\n");
        }
    }

    /**
     * Simple Merge Pattern (XOR-Merge): Die Aktivitäten führen in ein XOR-Gateway hinein, aber nur eine ist aktiv.
     * Multi Merge
     * @param graph
     * @param gatewayNode
     * @param sbvrData
     */
    private void generateExclusiveChoiceMerge(BPMNGraph graph, GatewayNode gatewayNode, StringBuilder sbvrData) {
        Set<Edge> outgoingEdges = new HashSet<>();
        Set<Edge> incomingEdges = new HashSet<>();

        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(gatewayNode)) {
                outgoingEdges.add(edge);
            }
            if (edge.getTarget().equals(gatewayNode)) {
                incomingEdges.add(edge);
            }
        }

        // XOR-Merge (Simple Merge)
        if (incomingEdges.size() > 1 && outgoingEdges.size() == 1) {
            List<String> activityList = new ArrayList<>();
            String targetActivityName = cleanText(gatewayNode.getName());
            Node targetNode = outgoingEdges.iterator().next().getTarget(); // Es gibt nur einen ausgehenden Pfad
            String targetNodeName = cleanText(targetNode.getName());

            for (Edge edge : incomingEdges) {
                Node sourceNode = edge.getSource();
                String sourceNodeName = cleanText(sourceNode.getName());
                activityList.add("\"" + sourceNodeName + "\"");
            }

            // Ausgabe des erkannten Patterns
            String patternMessage = "\nPattern erkannt: Simple Merge (XOR-Merge)";
            System.out.println(patternMessage);
            sbvrData.append(patternMessage);

            String finalMessage = "It is obligatory that exactly one of " + String.join(", ", activityList) + " has occurred before \"" + targetNodeName + "\".";
            System.out.println(finalMessage);
            sbvrData.append(finalMessage).append("\n");
        }
    }


    public void findParallelGatewayPatterns(BPMNGraph graph, StringBuilder sbvrData) {
        for (Node node : graph.getNodes()) {
            if (node instanceof GatewayNode && "Parallel".equals(((GatewayNode) node).getGatewayType())) {
                GatewayNode gatewayNode = (GatewayNode) node;

                if (!processedGateways.contains(gatewayNode.getId())) {
//                    String message = "\nParallel Gateway gefunden: " + cleanText(gatewayNode.getName());
//                    System.out.println(message);
//                    sbvrData.append(message).append("\n");

                    // Generiere die Regeln für paralleles Gateway
                    generateParallelGatewayRules(graph, gatewayNode, sbvrData);
                    // Gateway-Tasks extrahieren
                    extractGatewayTasks(graph, gatewayNode);

                    processedGateways.add(gatewayNode.getId());
                }
            }
        }
    }


    /**
     * Parallel Split: Eine Aktivität wird in mehrere parallele Pfade aufgeteilt, alle parallelen Aktivitäten werden gleichzeitig ausgeführt
     * General And Join: Alle eingehenden Pfade werden synchronisiert und müssen abgeschlossen sein, bevor der Pfad fortgesetzt wird
     * @param graph
     * @param gatewayNode
     * @param sbvrData
     */
    private void generateParallelGatewayRules(BPMNGraph graph, GatewayNode gatewayNode, StringBuilder sbvrData) {
        // Finde alle ausgehenden Kanten des parallelen Gateways
        Set<Edge> outgoingEdges = new HashSet<>();
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(gatewayNode)) {
                outgoingEdges.add(edge);
            }
        }

        // Finde alle eingehenden Kanten (nur für Merge-Gateways relevant)
        Set<Edge> incomingEdges = new HashSet<>();
        for (Edge edge : graph.getEdges()) {
            if (edge.getTarget().equals(gatewayNode)) {
                incomingEdges.add(edge);
            }
        }

        // Regel erstellen, wenn ausgehende Aktivitäten vorhanden sind
        if (!outgoingEdges.isEmpty()) {
            StringBuilder activities = new StringBuilder();
            String sourceActivityName = "";
            String sourceLane = "Unbekannte Lane";

            // Falls es mehr als eine ausgehende Kante gibt, parallele Ausführung
            if (outgoingEdges.size() > 1) {
                // Ausgabe des erkannten Patterns
                String patternMessage = "\nPattern erkannt: Parallel Split";
                System.out.println(patternMessage);
                sbvrData.append(patternMessage);

                // Finde die Quellaktivität, die das Gateway einleitet
                for (Edge incomingEdge : graph.getEdges()) {
                    if (incomingEdge.getTarget().equals(gatewayNode)) {
                        Node sourceNode = incomingEdge.getSource();
                        sourceActivityName = cleanText(sourceNode.getName());
                        sourceLane = sourceNode.getLane() != null ? sourceNode.getLane().getName() : "Unbekannte Lane";
                        break;
                    }
                }

                // Füge Zielknoten aller ausgehenden Kanten hinzu
                for (Edge edge : outgoingEdges) {
                    Node targetNode = edge.getTarget();
                    activities.append(cleanText(targetNode.getName())).append(" and ");
                }

                // Entferne das letzte " and " von der Aktivitätsliste
                if (activities.length() > 5) {
                    activities.setLength(activities.length() - 5); // Entferne das letzte " and "
                }

                // Regel formulieren für parallele Ausführung
                String message = "It is obligatory that " + activities +
                        " are executed simultaneously after " + sourceLane + " " + sourceActivityName + ".";
                System.out.println(message);
                sbvrData.append(message).append("\n");
            } else if (incomingEdges.size() > 0) {
                // Ausgabe des erkannten Patterns
                String patternMessage = "\nPattern erkannt: General And Join";
                System.out.println(patternMessage);
                sbvrData.append(patternMessage);

                // Merge-Gateway: Regel umkehren
                StringBuilder mergeActivities = new StringBuilder();

                // Finde die Quellaktivität(en) vor dem Merge-Gateway
                for (Edge incomingEdge : incomingEdges) {
                    Node sourceNode = incomingEdge.getSource();
                    mergeActivities.append(cleanText(sourceNode.getName())).append(" and ");
                }

                // Entferne das letzte " and " von der Liste der Quellaktivitäten
                if (mergeActivities.length() > 5) {
                    mergeActivities.setLength(mergeActivities.length() - 5);
                }

                // Zielaktivität nach dem Merge-Gateway
                String targetActivityName = "";
                String targetLane = "Unbekannte Lane";
                for (Edge outgoingEdge : outgoingEdges) {
                    Node targetNode = outgoingEdge.getTarget();
                    targetActivityName = cleanText(targetNode.getName());
                    targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";
                    break;
                }

                // Regel formulieren für Merge und anschließend ausgeführte Aktivitäten
                String message = "It is obligatory that " + mergeActivities +
                        " merge into " + targetLane + " " + targetActivityName + " after.";
                System.out.println(message);
                sbvrData.append(message).append("\n");
            }
        } else {
            String message = "No outgoing edges found for parallel gateway " + cleanText(gatewayNode.getName()) + ".";
            System.out.println(message);
            sbvrData.append(message).append("\n");
        }
    }

    public void findInclusiveGatewayPatterns(BPMNGraph graph, StringBuilder sbvrData) {
        for (Node node : graph.getNodes()) {
            if (node instanceof GatewayNode && "Inclusive".equals(((GatewayNode) node).getGatewayType())) {
                GatewayNode gatewayNode = (GatewayNode) node;

                if (!processedGateways.contains(gatewayNode.getId())) {
                    generateInclusiveGatewayRules(graph, gatewayNode, sbvrData);
                    processedGateways.add(gatewayNode.getId());
                }
            }
        }
    }

    /**
     * Multi Choice Split: OR Merge
     * Strucutured Synchronizing Merge: OR Join
     * @param graph
     * @param gatewayNode
     * @param sbvrData
     */
    private void generateInclusiveGatewayRules(BPMNGraph graph, GatewayNode gatewayNode, StringBuilder sbvrData) {
        Set<Edge> outgoingEdges = new HashSet<>();
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(gatewayNode)) {
                outgoingEdges.add(edge);
            }
        }

        Set<Edge> incomingEdges = new HashSet<>();
        for (Edge edge : graph.getEdges()) {
            if (edge.getTarget().equals(gatewayNode)) {
                incomingEdges.add(edge);
            }
        }

        if (!outgoingEdges.isEmpty()) {
            StringBuilder activities = new StringBuilder();
            String sourceActivityName = "";
            String sourceLane = "Unbekannte Lane";

            if (outgoingEdges.size() > 1) {
                String patternMessage = "\nPattern erkannt: Multi Choice";
                System.out.println(patternMessage);
                sbvrData.append(patternMessage);

                for (Edge incomingEdge : graph.getEdges()) {
                    if (incomingEdge.getTarget().equals(gatewayNode)) {
                        Node sourceNode = incomingEdge.getSource();
                        sourceActivityName = cleanText(sourceNode.getName());
                        sourceLane = sourceNode.getLane() != null ? sourceNode.getLane().getName() : "Unbekannte Lane";
                        break;
                    }
                }

                for (Edge edge : outgoingEdges) {
                    Node targetNode = edge.getTarget();
                    activities.append(cleanText(targetNode.getName())).append(" or ");
                }

                if (activities.length() > 4) {
                    activities.setLength(activities.length() - 4);
                }

                String message = "It is obligatory that " + activities +
                        " are executed after " + sourceLane + " " + sourceActivityName + "depending on the conditions.";
                System.out.println(message);
                sbvrData.append(message).append("\n");
            } else if (incomingEdges.size() > 0) {
                String patternMessage = "\nPattern erkannt: Structured Synchronizing Merge";
                System.out.println(patternMessage);
                sbvrData.append(patternMessage);

                StringBuilder mergeActivities = new StringBuilder();

                for (Edge incomingEdge : incomingEdges) {
                    Node sourceNode = incomingEdge.getSource();
                    mergeActivities.append(cleanText(sourceNode.getName())).append(" or ");
                }

                if (mergeActivities.length() > 4) {
                    mergeActivities.setLength(mergeActivities.length() - 4);
                }

                String targetActivityName = "";
                String targetLane = "Unbekannte Lane";
                for (Edge outgoingEdge : outgoingEdges) {
                    Node targetNode = outgoingEdge.getTarget();
                    targetActivityName = cleanText(targetNode.getName());
                    targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";
                    break;
                }

                String message = "It is obligatory that " + mergeActivities +
                        " merge into " + targetLane + " " + targetActivityName + " after all active paths have been completed.";
                System.out.println(message);
                sbvrData.append(message).append("\n");
            }
        } else {
            String message = "No outgoing edges found for inclusive gateway " + cleanText(gatewayNode.getName()) + ".";
            System.out.println(message);
            sbvrData.append(message).append("\n");
        }
    }


    /**
     * Deferred Choice: ermöglicht es eine Entscheidung später im Prozess zu treffen, basierend auf einer Bedingung, die erst später geprüft wird
     */
    public void findEventBasedGatewayPatterns(BPMNGraph graph, StringBuilder sbvrData) {
        for (Node node : graph.getNodes()) {
            if (node instanceof GatewayNode gatewayNode && "EventBased".equals(((GatewayNode) node).getGatewayType())) {

                if (!processedGateways.contains(gatewayNode.getId())) {
                    String message = "\nEvent-Based Gateway gefunden: " + cleanText(gatewayNode.getName());
                    System.out.println(message);
                    sbvrData.append(message).append("\n");

                    // Gateway-Tasks extrahieren
                    extractGatewayTasks(graph, gatewayNode);

                    generateEventBasedGatewayRules(graph, gatewayNode, sbvrData);
                    processedGateways.add(gatewayNode.getId());
                }
            }
        }
    }


    private void generateEventBasedGatewayRules(BPMNGraph graph, GatewayNode gatewayNode, StringBuilder sbvrData) {
        String patternMessage = "Pattern erkannt: Deferred Choice";
        System.out.println(patternMessage);
        sbvrData.append(patternMessage);

        // Finde alle ausgehenden Kanten des Event-Based Gateways
        Set<Edge> outgoingEdges = new HashSet<>();
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(gatewayNode)) {
                outgoingEdges.add(edge);
            }
        }

        // Regel erstellen, wenn ausgehende Aktivitäten vorhanden sind
        if (!outgoingEdges.isEmpty()) {
            StringBuilder activities = new StringBuilder();
            String sourceActivityName = "";
            String sourceLane = "Unbekannte Lane";

            // Wenn das Gateway den Standardnamen "Event-Based Gateway" hat, die vorherige Aktivität finden
            if ("Event-Based Gateway".equals(cleanText(gatewayNode.getName()))) {
                for (Edge incomingEdge : graph.getEdges()) {
                    if (incomingEdge.getTarget().equals(gatewayNode)) {
                        Node sourceNode = incomingEdge.getSource();
                        sourceActivityName = cleanText(sourceNode.getName());
                        sourceLane = sourceNode.getLane() != null ? sourceNode.getLane().getName() : "Unbekannte Lane";
                        break;
                    }
                }
            } else {
                // Für benutzerdefinierte Gateway-Namen die erste eingehende Aktivität verwenden
                for (Edge edge : outgoingEdges) {
                    Node sourceNode = edge.getSource();
                    sourceActivityName = cleanText(sourceNode.getName());
                    sourceLane = sourceNode.getLane() != null ? sourceNode.getLane().getName() : "Unbekannte Lane";
                    break;
                }
            }

            // Füge Zielknoten aller ausgehenden Kanten hinzu
            for (Edge edge : outgoingEdges) {
                Node targetNode = edge.getTarget();
                activities.append(cleanText(targetNode.getName())).append(" or ");
            }

            // Entferne das letzte " or " von der Aktivitätsliste
            if (activities.length() > 4) {
                activities.setLength(activities.length() - 4);
            }

            // Regel formulieren
            String message = "It is obligatory that one of the following events occurs: " + activities +
                    ", after " + sourceLane + " " + sourceActivityName + ".";
            System.out.println(message);
            sbvrData.append(message).append("\n");
        } else {
            String message = "No outgoing edges found for Event-Based Gateway " + cleanText(gatewayNode.getName()) + ".";
            System.out.println(message);
            sbvrData.append(message).append("\n");
        }
    }


    // Bereinigt den Text von unerwünschten Umbrüchen und Leerzeichen
    private static String cleanText(String text) {
        if (text != null) {
            return text.replaceAll("[\\r\\n\\t]", " ").trim();
        }
        return "";
    }
}

