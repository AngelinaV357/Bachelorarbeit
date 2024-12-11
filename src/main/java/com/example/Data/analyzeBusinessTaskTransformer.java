package com.example.Data;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.FlowNode;

import java.util.Collection;

import static com.example.Hilfsmethoden.getRoleForNode;

public class analyzeBusinessTaskTransformer {
    public static void analyzeBusinessTasks(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Alle BusinessRuleTasks abrufen
        Collection<BusinessRuleTask> businessRuleTasks = modelInstance.getModelElementsByType(BusinessRuleTask.class);

        // Holt alle Lane-Elemente, die für die Zuweisung von Rollen verwendet werden
        Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);

        // Iteriere durch alle BusinessRuleTasks
        for (BusinessRuleTask businessRuleTask : businessRuleTasks) {
            // Namen des BusinessRuleTasks abrufen
            String taskName = sanitizeName(businessRuleTask.getName());

            // Rolle des aktuellen Tasks
            String sourceRole = getRoleForNode(businessRuleTask, lanes);

            // Finde die vorherige Aktivität und ihre Rolle
            String previousActivityName = getPreviousActivityName(businessRuleTask);
            String previousRole = getPreviousActivityRole(businessRuleTask, lanes);

            // Generiere die BusinessRule für den BusinessRuleTask
            String businessRuleStatement = createBusinessRule(previousRole, previousActivityName, sourceRole, taskName);

            // Füge das Statement zum sbvrOutput hinzu
            sbvrOutput.append(businessRuleStatement);
        }
    }

    /**
     * Bereinigt den Namen, entfernt unerwünschte Zeilenumbrüche oder Sonderzeichen.
     */
    private static String sanitizeName(String name) {
        if (name == null) {
            return "Unbenannt";
        }
        // Entfernt Zeilenumbrüche und überflüssige Leerzeichen
        return name.replaceAll("[\\r\\n]+", " ").trim();
    }

    /**
     * Erstellt die BusinessRule für einen BusinessRuleTask.
     */
    public static String createBusinessRule(String sourceRole, String sourceName, String targetRole, String targetName) {
        return "Es ist notwendig, dass '" + sourceRole + "' '" + sourceName + "' ausführt, bevor '" + targetRole + "' '" + targetName + "' ausführt und alle Anforderungen geprüft wurden.\n";
    }

    /**
     * Holt den Namen der vorherigen Aktivität aus dem SequenceFlow.
     */
    private static String getPreviousActivityName(BusinessRuleTask task) {
        for (SequenceFlow incomingFlow : task.getIncoming()) {
            FlowNode sourceNode = incomingFlow.getSource();

            if (sourceNode != null && sourceNode.getName() != null) {
                return sanitizeName(sourceNode.getName());
            }
        }
        return "Unbekannte Aktivität";
    }

    /**
     * Holt die Rolle der vorherigen Aktivität.
     */
    private static String getPreviousActivityRole(BusinessRuleTask task, Collection<Lane> lanes) {
        for (SequenceFlow incomingFlow : task.getIncoming()) {
            FlowNode sourceNode = incomingFlow.getSource();

            if (sourceNode != null) {
                return getRoleForNode(sourceNode, lanes);
            }
        }
        return "Unbekannte Rolle";
    }
}
