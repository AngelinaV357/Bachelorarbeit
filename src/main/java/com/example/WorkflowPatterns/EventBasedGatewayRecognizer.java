package com.example.WorkflowPatterns;

import java.util.*;

public class EventBasedGatewayRecognizer {

    /**
     * Erkennung von Event-Based Gateway Patterns im gegebenen Graphen.
     *
     * @param graph       Der BPMN-Graph (Knoten und Kanten).
     * @param nodeTypes   Typen der Knoten (z. B. "event-based-gateway", "intermediate-event").
     * @param nodeNames   Namen der Knoten.
     * @param conditions  Bedingungen auf Sequenzflüssen.
     * @return Eine Liste von erkannten Event-Based Gateway Patterns.
     */
    public List<List<String>> detectEventBasedGatewayPatterns(
            Map<String, List<String>> graph,
            Map<String, String> nodeTypes,
            Map<String, String> nodeNames,
            Map<String, String> conditions) {

        List<List<String>> patterns = new ArrayList<>();

        // Iteriere über alle Knoten im Graphen
        for (String node : graph.keySet()) {
            // Prüfe, ob der Knoten ein Event-Based Gateway ist
            if ("event-based-gateway".equals(nodeTypes.get(node))) {

                List<String> pattern = new ArrayList<>();
                pattern.add(node); // Füge das Gateway selbst zum Pattern hinzu

                // Durchlaufe alle ausgehenden Kanten des Gateways
                for (String outgoingFlow : graph.get(node)) {
                    if ("intermediate-event".equals(nodeTypes.get(outgoingFlow)) ||
                            "end-event".equals(nodeTypes.get(outgoingFlow))) {
                        pattern.add(outgoingFlow); // Füge das Event oder End-Event zum Pattern hinzu
                    }
                }

                // Prüfe, ob mindestens ein gültiges Ziel gefunden wurde
                if (pattern.size() > 1) {
                    patterns.add(pattern);
                }
            }
        }

        return patterns;
    }

    /**
     * Generiert eine SBVR-Regel basierend auf dem erkannten Event-Based Gateway Pattern.
     *
     * @param pattern     Die Liste der Knoten im Pattern.
     * @param nodeNames   Die Namen der Knoten.
     * @param conditions  Die Bedingungen der Sequenzflüsse.
     * @return Die generierte SBVR-Regel als String.
     */
    public String generateSbvrRuleForEventBasedGateway(
            List<String> pattern,
            Map<String, String> nodeNames,
            Map<String, String> conditions) {

        if (pattern.size() < 2) {
            return "Ungültiges Pattern";
        }

        StringBuilder sbvrRule = new StringBuilder("It is permitted that ");

        // Hole den Namen des Gateways
        String gatewayName = nodeNames.getOrDefault(pattern.get(0), "Unnamed Gateway");

        // Verarbeite alle Ziele des Gateways
        for (int i = 1; i < pattern.size(); i++) {
            String targetNode = pattern.get(i);
            String targetName = nodeNames.getOrDefault(targetNode, "Unnamed Node");
            String condition = conditions.getOrDefault(targetNode, "No Condition");

            sbvrRule.append("\n- (" + targetName + ")");
            if (!"No Condition".equals(condition)) {
                sbvrRule.append(" if ").append(condition);
            }
        }

        sbvrRule.append(" after ").append(gatewayName).append(".");

        return sbvrRule.toString();
    }
}
