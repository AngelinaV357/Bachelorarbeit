package com.example.Data;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.bpmn.instance.Participant;

import java.util.Collection;

import static com.example.Hilfsmethoden.getRoleForNode;

public class analyzeUserTask {

    public static void analyzeUserTasks(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Alle UserTasks abrufen
        Collection<UserTask> userTasks = modelInstance.getModelElementsByType(UserTask.class);

        // Holt alle Lane-Elemente, die für die Zuweisung von Rollen verwendet werden
        Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);

        // Holt alle MessageFlows
        Collection<MessageFlow> messageFlows = modelInstance.getModelElementsByType(MessageFlow.class);

        // Holt alle Teilnehmer (Participants)
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);

        // Iteriere durch alle UserTasks
        for (UserTask userTask : userTasks) {
            // Namen des UserTasks abrufen
            String taskName = sanitizeName(userTask.getName());

            // Rolle des Benutzers ermitteln
            String userRole = getRoleForNode(userTask, lanes);

            // Generiere das Statement für den UserTask
            String userTaskStatement = createUserTaskStatement(taskName, userRole);

            // Füge das Statement zum sbvrOutput hinzu
            sbvrOutput.append(userTaskStatement);
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
     * Erzeugt eine SBVR-Aussage für den UserTask.
     */
    public static String createUserTaskStatement(String taskName, String role) {
        return "Es ist notwendig, dass die Aufgabe " + taskName + ", einer Ressource mit der Rolle " + role + " zugewiesen wird.\n";
    }
}
