package com.example.WorkflowPatterns;

import java.util.*;

public class BoundaryEventRuleGenerator {

    public List<String> detectAndGenerateBoundaryEventRules(Map<String, List<String>> graph, Map<String, String> nodeNames, Map<String, String> conditions) {
        List<String> boundaryEventRules = new ArrayList<>();

        // Iteriere durch den Graph und überprüfe auf Boundary-Events
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            String sourceActivity = entry.getKey();
            List<String> targetActivities = entry.getValue();

            // Überprüfe, ob das Ereignis ein nicht-unterbrechendes oder unterbrechendes Boundary-Event ist
            if (isBoundaryEvent(sourceActivity, targetActivities)) {
                // Generiere die Regeln für Boundary-Events
                boundaryEventRules.add(generatePermitRule(sourceActivity, targetActivities, nodeNames, conditions));
                boundaryEventRules.add(generateObligationRule(sourceActivity, targetActivities, nodeNames, conditions));
                boundaryEventRules.add(generateProhibitionRule(sourceActivity, targetActivities, nodeNames, conditions));
            }
        }

        return boundaryEventRules;
    }

    // Überprüfen, ob es sich um ein Boundary-Event handelt
    private boolean isBoundaryEvent(String sourceActivity, List<String> targetActivities) {
        // Hier könnte eine spezifische Überprüfung für Boundary-Events vorgenommen werden
        return true; // Für das Beispiel nehmen wir an, dass alle Ereignisse Boundary-Events sind
    }

    // Erlaubte Regel für Boundary-Event
    private String generatePermitRule(String sourceActivity, List<String> targetActivities, Map<String, String> nodeNames, Map<String, String> conditions) {
        StringBuilder rule = new StringBuilder();

        for (String targetActivity : targetActivities) {
            String sourceActivityName = nodeNames.get(sourceActivity);
            String targetActivityName = nodeNames.get(targetActivity);
            String condition = conditions.get(targetActivity); // Bedingung, falls vorhanden

            rule.append("Es ist erlaubt, dass ")
                    .append(targetActivityName)
                    .append(" empfangen wird, wenn ")
                    .append(sourceActivityName)
                    .append(" ausgeführt wird.");

            if (condition != null) {
                rule.append(" und wenn ").append(condition);
            }
            rule.append(". ");
        }

        return rule.toString();
    }

    // Verpflichtende Regel für Boundary-Event
    private String generateObligationRule(String sourceActivity, List<String> targetActivities, Map<String, String> nodeNames, Map<String, String> conditions) {
        StringBuilder rule = new StringBuilder();

        for (String targetActivity : targetActivities) {
            String sourceActivityName = nodeNames.get(sourceActivity);
            String targetActivityName = nodeNames.get(targetActivity);
            String condition = conditions.get(targetActivity); // Bedingung, falls vorhanden

            rule.append("Es ist verpflichtend, dass ")
                    .append(targetActivityName)
                    .append(" ausgeführt wird, nachdem ")
                    .append(sourceActivityName)
                    .append(" ausgeführt wurde.");

            if (condition != null) {
                rule.append(" und wenn ").append(condition);
            }
            rule.append(". ");
        }

        return rule.toString();
    }

    // Verbotsregel für unterbrechendes Boundary-Event
    private String generateProhibitionRule(String sourceActivity, List<String> targetActivities, Map<String, String> nodeNames, Map<String, String> conditions) {
        StringBuilder rule = new StringBuilder();

        for (String targetActivity : targetActivities) {
            String sourceActivityName = nodeNames.get(sourceActivity);
            String targetActivityName = nodeNames.get(targetActivity);

            rule.append("Es ist verboten, dass ")
                    .append(sourceActivityName)
                    .append(" nach ")
                    .append(targetActivityName)
                    .append(" ausgeführt wird.")
                    .append(". ");
        }

        return rule.toString();
    }
}
