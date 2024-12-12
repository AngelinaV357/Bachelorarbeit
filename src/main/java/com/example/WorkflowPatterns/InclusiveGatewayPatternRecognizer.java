package com.example.WorkflowPatterns;

import java.util.*;

public class InclusiveGatewayPatternRecognizer {

    // Methode zum Erkennen von Inclusive-Gateway-Patterns mit übersprungenen verarbeiteten Knoten
    public List<List<String>> detectInclusiveGatewayPatterns(Map<String, List<String>> graph, Map<String, String> nodeTypes, Set<String> processedNodes) {
        List<List<String>> inclusivePatterns = new ArrayList<>();

        // Divergierende Inclusive Gateways
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            String node = entry.getKey();

            // Wenn der Knoten bereits verarbeitet wurde, überspringen
            if (processedNodes.contains(node)) {
                continue;
            }

            List<String> outgoingFlows = entry.getValue();

            if (nodeTypes.get(node).equals("InclusiveGateway") && outgoingFlows.size() > 1) {
                List<String> divergingPattern = new ArrayList<>();
                divergingPattern.add(node);
                divergingPattern.addAll(outgoingFlows);
                inclusivePatterns.add(divergingPattern);

                // Markiere alle beteiligten Knoten als verarbeitet
                processedNodes.add(node);
                processedNodes.addAll(outgoingFlows);
            }
        }

        // Konvergierende Inclusive Gateways
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            String node = entry.getKey();
            List<String> incomingFlows = new ArrayList<>();

            // Finde eingehende Flüsse zu diesem Knoten
            for (Map.Entry<String, List<String>> checkEntry : graph.entrySet()) {
                for (String flow : checkEntry.getValue()) {
                    if (flow.equals(node)) {
                        incomingFlows.add(checkEntry.getKey());
                    }
                }
            }

            if (nodeTypes.get(node).equals("InclusiveGateway") && incomingFlows.size() > 1) {
                List<String> convergingPattern = new ArrayList<>();
                convergingPattern.add(node);
                convergingPattern.addAll(incomingFlows);
                inclusivePatterns.add(convergingPattern);

                // Markiere alle beteiligten Knoten als verarbeitet
                processedNodes.add(node);
                processedNodes.addAll(incomingFlows);
            }
        }

        return inclusivePatterns;
    }

    // Methode zur Generierung von SBVR-Regeln für Inclusive-Gateways
    public String generateSbvrRuleForInclusive(List<String> pattern, Map<String, String> nodeNames, Map<String, String> conditions) {
        StringBuilder sbvrRule = new StringBuilder();

        // Regel für divergierendes Inclusive-Gateway
        if (pattern.size() > 1) {
            sbvrRule.append("It is obligatory that ");
            sbvrRule.append(nodeNames.get(pattern.get(0)));
            sbvrRule.append(" after ");
            sbvrRule.append(nodeNames.get(pattern.get(1)));

            for (int i = 2; i < pattern.size(); i++) {
                sbvrRule.append(" and if ");
                sbvrRule.append(conditions.get(pattern.get(i)));
            }
            sbvrRule.append(".\n");
        }

        // Regel für konvergierendes Inclusive-Gateway
        if (pattern.size() > 1) {
            sbvrRule.append("It is obligatory that ");
            sbvrRule.append(nodeNames.get(pattern.get(0)));
            sbvrRule.append(" after ");

            for (int i = 1; i < pattern.size(); i++) {
                sbvrRule.append(nodeNames.get(pattern.get(i)));
                sbvrRule.append(" if ");
                sbvrRule.append(conditions.get(pattern.get(i)));
                if (i < pattern.size() - 1) {
                    sbvrRule.append(" and ");
                }
            }
            sbvrRule.append(".\n");
        }

        return sbvrRule.toString();
    }
}
