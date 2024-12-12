package com.example.WorkflowPatterns;

import java.util.*;

public class EventBasedGatewayPatternRecognizer {

    // Methode zum Erkennen von divergierenden Ereignis-basierten Gateways und Generierung von SBVR-Regeln
    public List<String> detectAndGenerateEventBasedGatewayRules(Map<String, List<String>> graph, Map<String, String> nodeNames, Map<String, String> conditions) {
        List<String> eventBasedRules = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            String sourceActivity = entry.getKey();
            List<String> targetActivities = entry.getValue();

            // Überprüfen, ob das Quell-Activity ein Ereignis-basiertes Gateway ist
            if (isEventBasedGateway(sourceActivity)) {
                // Durch alle Zielaktivitäten iterieren, die durch das Ereignis-basiertes Gateway divergenziert werden
                for (String targetActivity : targetActivities) {
                    String sourceActivityName = nodeNames.get(sourceActivity);
                    String targetActivityName = nodeNames.get(targetActivity);
                    String condition = conditions.get(targetActivity); // Bedingung, falls vorhanden

                    // SBVR-Regel für jede Divergenz
                    String rule = generateSbvrRuleForEventBasedGateway(sourceActivityName, targetActivityName, condition);
                    eventBasedRules.add(rule);
                }
            }
        }

        return eventBasedRules;
    }

    // Methode zur Überprüfung, ob eine Aktivität ein Ereignis-basiertes Gateway ist
    private boolean isEventBasedGateway(String activityId) {
        // Hier könnte eine Überprüfung basierend auf dem Aktivitätstyp erfolgen
        // Zum Beispiel könnte man eine Map von Knotenarten oder ähnliches verwenden
        return activityId.contains("EventBasedGateway");  // Beispielhafte Prüfung, anpassen je nach Modell
    }

    // Generierung der SBVR-Regel für ein divergierendes Ereignis-basiertes Gateway
    private String generateSbvrRuleForEventBasedGateway(String sourceActivityName, String targetActivityName, String condition) {
        StringBuilder sbvrRule = new StringBuilder();

        sbvrRule.append("Es ist erlaubt, dass '")
                .append(targetActivityName)
                .append("' ausgeführt wird, nachdem '")
                .append(sourceActivityName)
                .append("' ausgelöst wurde");

        if (condition != null && !condition.isEmpty()) {
            sbvrRule.append(" und wenn ")
                    .append(condition);
        }

        sbvrRule.append(".");

        return sbvrRule.toString();
    }
}
