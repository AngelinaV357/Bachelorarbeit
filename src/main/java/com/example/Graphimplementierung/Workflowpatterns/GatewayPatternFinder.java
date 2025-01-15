package com.example.Graphimplementierung.Workflowpatterns;

import com.example.Graphimplementierung.Grundstruktur.Nodes.*;

import java.util.*;

public class GatewayPatternFinder {

    // Set, um bereits ausgegebene Gateways nachzuverfolgen
    private final Set<String> processedGateways = new HashSet<>();
    private final Set<Node> gatewayCoveredTasks = new HashSet<>();

    public void findExclusiveGatewayPatterns(BPMNGraph graph, StringBuilder sbvrData) {
        for (Node node : graph.getNodes()) {
            if (node instanceof GatewayNode && "Exclusive".equals(((GatewayNode) node).getGatewayType())) {
                GatewayNode gatewayNode = (GatewayNode) node;

                if (!processedGateways.contains(gatewayNode.getId())) {
                    String message = "\nExclusive Gateway gefunden: " + cleanText(gatewayNode.getName());
                    System.out.println(message);
                    sbvrData.append(message).append("\n");

//                    // Zuerst die grundlegenden SBVR-Regeln ausgeben
//                    generateSBVRRules(graph, gatewayNode, sbvrData);

                    // Dann die spezifische Regel für das exklusive Gateway
                    generateExclusiveRule(graph, gatewayNode, sbvrData);
                    generateSBVRRules(graph, gatewayNode, sbvrData);

                    processedGateways.add(gatewayNode.getId());
                }
            }
        }
    }

    private void generateSBVRRules(BPMNGraph graph, GatewayNode gatewayNode, StringBuilder sbvrData) {
        String message = "SBVR-Regeln für " + cleanText(gatewayNode.getName()) + ":";
        System.out.println(message);
        sbvrData.append(message).append("\n");

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
                }
                // Bedingung für das Event-Based Gateway
                else if ("EventBased Gateway".equals(cleanText(gatewayNode.getName()))) {
                    // Suche die eingehende Kante, um die Quelle der vorherigen Aktivität zu finden
                    for (Edge incomingEdge : graph.getEdges()) {
                        if (incomingEdge.getTarget().equals(gatewayNode)) {
                            sourceNode = incomingEdge.getSource();
                            sourceActivityName = cleanText(sourceNode.getName());
                            break;
                        }
                    }
                }
                // Bedingung für das Parallele Gateway
                else if ("Parallel Gateway".equals(cleanText(gatewayNode.getName()))) {
                    // Suche die eingehende Kante, um die Quelle der vorherigen Aktivität zu finden
                    for (Edge incomingEdge : graph.getEdges()) {
                        if (incomingEdge.getTarget().equals(gatewayNode)) {
                            sourceNode = incomingEdge.getSource();
                            sourceActivityName = cleanText(sourceNode.getName());
                            break;
                        }
                    }
                }
                else {
                    // Regel für den Fall, dass das Gateway einen benutzerdefinierten Namen hat
                    if (condition != null && !condition.isEmpty()) {
                        sourceActivityName = cleanText(sourceNode.getName());
                    }
                }

                // Regel mit Bedingung erstellen
                if (condition != null && !condition.isEmpty()) {
                    message = "It is obligatory that \"" + targetLane + "\" \"" + cleanText(targetNode.getName()) +
                            "\" after \"" + sourceLane + "\" \"" + cleanText(gatewayNode.getName()) + "\" is \"" + cleanText(condition) + "\".\n";
                } else {
                    message = "It is obligatory that \"" + targetLane + "\" \"" + cleanText(targetNode.getName()) +
                            "\" after \"" + sourceLane + "\" \"" + sourceActivityName + "\".\n";
                }
                System.out.println(message);
                sbvrData.append(message).append("\n");
            }
        }
    }


    // Methode, um alle Tasks zu speichern, die durch Gateways abgedeckt sind
    private void extractGatewayTasks(BPMNGraph graph, GatewayNode gatewayNode, Set<Node> gatewayCoveredTasks) {
        // Extrahiere Tasks, die durch das Gateway abgedeckt werden, und markiere sie als bearbeitet
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(gatewayNode)) {
                Node targetNode = edge.getTarget();
                if (!gatewayCoveredTasks.contains(targetNode)) {
                    // Wenn der Task noch nicht bearbeitet wurde, füge ihn hinzu
                    gatewayCoveredTasks.add(targetNode);
                    System.out.println("Gateway deckt Task ab: " + cleanText(targetNode.getName()));
                }
            }
        }
    }


    private void generateExclusiveRule(BPMNGraph graph, GatewayNode gatewayNode, StringBuilder sbvrData) {
        String message = "Zusätzliche SBVR Regel für: " + cleanText(gatewayNode.getName()) + ":";
        System.out.println(message);
        sbvrData.append(message).append("\n");

        // Finde alle ausgehenden Kanten des exklusiven Gateways
        Set<Edge> outgoingEdges = new HashSet<>();
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(gatewayNode)) {
                outgoingEdges.add(edge);
            }
        }

        // Unterscheidung: Gateways mit 2 ausgehenden Kanten
        if (outgoingEdges.size() == 2) {
            // Liste der Aktivitäten und Lanes sammeln
            StringBuilder activities = new StringBuilder();
            StringBuilder lanes = new StringBuilder();
            String condition = "";
            String sourceActivityName = cleanText(gatewayNode.getName());  // Gateway Name als Quelle verwenden
            Node sourceNode = null;

            // Wenn der Name des Gateways der Platzhaltername ist, finde die Quelle der vorherigen Aktivität
            if ("Exclusive Gateway".equals(cleanText(gatewayNode.getName()))) {
                for (Edge incomingEdge : graph.getEdges()) {
                    if (incomingEdge.getTarget().equals(gatewayNode)) {
                        sourceNode = incomingEdge.getSource();
                        sourceActivityName = cleanText(sourceNode.getName());
                        break;
                    }
                }
            }

            // Gehe durch alle ausgehenden Kanten
            Iterator<Edge> edgeIterator = outgoingEdges.iterator();
            List<String> activityList = new ArrayList<>();
            List<String> laneList = new ArrayList<>();

            while (edgeIterator.hasNext()) {
                Edge edge = edgeIterator.next();
                Node targetNode = edge.getTarget();
                String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unbekannte Lane";
                String targetNodeName = cleanText(targetNode.getName());

                // Füge Lane und Aktivität zur Liste hinzu
                activityList.add("\"" + targetNodeName + "\"");
                laneList.add("\"" + targetLane + "\"");

                // Wenn es nicht das letzte Element ist, fügen wir "or" zwischen den Aktivitäten ein
                if (edgeIterator.hasNext()) {
                    activityList.add("or");
                    laneList.add("or");
                }
            }

            // Regel formulieren
            StringBuilder finalMessage = new StringBuilder("It is obligatory that ");
            for (int i = 0; i < activityList.size(); i++) {
                finalMessage.append(laneList.get(i)).append(" ").append(activityList.get(i));
                // Vermeide unnötige "or" am Ende der Nachricht
                if (i < activityList.size() - 1) {
                    finalMessage.append(" ");
                }
            }

            finalMessage.append(", but not both, after \"").append(sourceActivityName).append("\".\n");

            System.out.println(finalMessage.toString());
            sbvrData.append(finalMessage.toString()).append("\n");

        } else if (outgoingEdges.size() == 1) {
            // Gateways, die mehrere Kanten zusammenführen (Merge Gateways)
            // Finde alle eingehenden Kanten
            Set<Edge> incomingEdges = new HashSet<>();
            for (Edge edge : graph.getEdges()) {
                if (edge.getTarget().equals(gatewayNode)) {
                    incomingEdges.add(edge);
                }
            }

            // Wenn mehrere eingehende Kanten vorhanden sind, dann eine "Merge"-Regel generieren
            if (incomingEdges.size() > 1) {
                StringBuilder incomingActivities = new StringBuilder();
                StringBuilder incomingLanes = new StringBuilder();

                for (Edge edge : incomingEdges) {
                    Node sourceNode = edge.getSource();
                    String sourceLane = sourceNode.getLane() != null ? sourceNode.getLane().getName() : "Unbekannte Lane";
                    incomingActivities.append(cleanText(sourceNode.getName())).append(" and ");
                    incomingLanes.append(sourceLane).append(" ");
                }

                // Entferne das letzte " and "
                if (incomingActivities.length() > 4) {
                    incomingActivities.setLength(incomingActivities.length() - 4);
                }

                // Regel formulieren
                message = "It is obligatory that \"" + incomingLanes + "\" \"" + incomingActivities +
                        "\" are completed before the execution of \"" + cleanText(gatewayNode.getName()) + "\".\n";
                System.out.println(message);
                sbvrData.append(message).append("\n");
            }
        }
    }



    public void findParallelGatewayPatterns(BPMNGraph graph, StringBuilder sbvrData) {
        for (Node node : graph.getNodes()) {
            if (node instanceof GatewayNode && "Parallel".equals(((GatewayNode) node).getGatewayType())) {
                GatewayNode gatewayNode = (GatewayNode) node;

                if (!processedGateways.contains(gatewayNode.getId())) {
                    String message = "\nParallel Gateway gefunden: " + cleanText(gatewayNode.getName());
                    System.out.println(message);
                    sbvrData.append(message).append("\n");

                    // Gateway-Tasks extrahieren, aber nur Tasks, die noch nicht bearbeitet wurden
                    extractGatewayTasks(graph, gatewayNode, gatewayCoveredTasks);

                    // Generiere die Regeln für paralleles Gateway
                    generateParallelGatewayRules(graph, gatewayNode, sbvrData);
                    generateSBVRRules(graph, gatewayNode, sbvrData);

                    // Gateway als verarbeitet markieren
                    processedGateways.add(gatewayNode.getId());
                }
            }
        }
    }


    private void generateParallelGatewayRules(BPMNGraph graph, GatewayNode gatewayNode, StringBuilder sbvrData) {
        String message = "SBVR-Regeln für paralleles Gateway " + cleanText(gatewayNode.getName()) + ":";
        System.out.println(message);
        sbvrData.append(message).append("\n");

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
                message = "It is obligatory that " + activities +
                        " are executed simultaneous after " + sourceLane + " " + sourceActivityName + ".";
                System.out.println(message);
                sbvrData.append(message).append("\n");
            } else if (incomingEdges.size() > 0) {
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
                message = "It is obligatory that " + mergeActivities +
                        " merge into " + targetLane + " " + targetActivityName + " after.";
                System.out.println(message);
                sbvrData.append(message).append("\n");
            }
        } else {
            message = "No outgoing edges found for parallel gateway " + cleanText(gatewayNode.getName()) + ".";
            System.out.println(message);
            sbvrData.append(message).append("\n");
        }
    }


    public void findEventBasedGatewayPatterns(BPMNGraph graph, StringBuilder sbvrData) {
        for (Node node : graph.getNodes()) {
            if (node instanceof GatewayNode && "EventBased".equals(((GatewayNode) node).getGatewayType())) {
                GatewayNode gatewayNode = (GatewayNode) node;

                if (!processedGateways.contains(gatewayNode.getId())) {
                    String message = "\nEvent-Based Gateway gefunden: " + cleanText(gatewayNode.getName());
                    System.out.println(message);
                    sbvrData.append(message).append("\n");

                    // Gateway-Tasks extrahieren
                    extractGatewayTasks(graph, gatewayNode, gatewayCoveredTasks);
                    generateSBVRRules(graph, gatewayNode, sbvrData);

                    generateEventBasedGatewayRules(graph, gatewayNode, sbvrData);
                    processedGateways.add(gatewayNode.getId());
                }
            }
        }
    }


    private void generateEventBasedGatewayRules(BPMNGraph graph, GatewayNode gatewayNode, StringBuilder sbvrData) {
        String message = "SBVR-Regeln für Event-Based Gateway " + cleanText(gatewayNode.getName()) + ":";
        System.out.println(message);
        sbvrData.append(message).append("\n");

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
            message = "It is obligatory that one of the following events occurs: " + activities +
                    ", after " + sourceLane + " " + sourceActivityName + ".";
            System.out.println(message);
            sbvrData.append(message).append("\n");
        } else {
            message = "No outgoing edges found for Event-Based Gateway " + cleanText(gatewayNode.getName()) + ".";
            System.out.println(message);
            sbvrData.append(message).append("\n");
        }
    }


    // Bereinigt den Text von unerwünschten Umbrüchen und Leerzeichen
    private String cleanText(String text) {
        if (text != null) {
            return text.replaceAll("[\\r\\n\\t]", " ").trim();
        }
        return "";
    }
}

