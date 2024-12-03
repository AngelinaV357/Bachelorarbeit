package com.example.Data;

import com.example.SBVRTransformerNEU;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.bpmn.instance.BaseElement;

import java.util.Collection;

import static com.example.Hilfsmethoden.getMessageFlowParticipantName;
import static com.example.Hilfsmethoden.getRoleForNode;

public class analyzeServiceTask {
    public static void analyzeServiceTasks(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Alle ServiceTasks abrufen
        Collection<ServiceTask> serviceTasks = modelInstance.getModelElementsByType(ServiceTask.class);

        // Holt alle Lane-Elemente, die für die Zuweisung von Rollen verwendet werden
        Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);

        // Holt alle MessageFlows
        Collection<MessageFlow> messageFlows = modelInstance.getModelElementsByType(MessageFlow.class);

        // Holt alle Teilnehmer (Participants)
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);

        // Iteriere durch alle ServiceTasks
        for (ServiceTask serviceTask : serviceTasks) {
            // Namen des ServiceTasks abrufen
            String taskName = sanitizeName(serviceTask.getName());

            // Rolle des Quellparticipants ermitteln (aus den Lanes)
            String sourceRole = getRoleForNode(serviceTask, lanes);

            // Suche den passenden MessageFlow, um den Empfänger zu ermitteln
            String targetRole = getMessageFlowParticipantNameFromServiceTask(serviceTask, messageFlows, participants);

            // Generiere das Statement für den ServiceTask
            String serviceTaskStatement = SBVRTransformerNEU.createServiceTaskStatement(taskName, sourceRole, targetRole);

            // Füge das Statement zum sbvrOutput hinzu
            sbvrOutput.append(serviceTaskStatement);
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
     * Hilfsmethode, um den Teilnehmernamen aus dem MessageFlow zu extrahieren
     */
    private static String getMessageFlowParticipantNameFromServiceTask(ServiceTask serviceTask, Collection<MessageFlow> messageFlows, Collection<Participant> participants) {
        // Iteriere durch alle MessageFlows, um den passenden Empfänger zu finden
        for (MessageFlow messageFlow : messageFlows) {
            BaseElement source = (BaseElement) messageFlow.getSource();
            BaseElement target = (BaseElement) messageFlow.getTarget();

            // Überprüfe, ob der ServiceTask die Quelle ist
            if (source.equals(serviceTask)) {
                // Finde den Teilnehmer des Ziels
                return getMessageFlowParticipantName(target, participants);
            }
        }
        return "Unbekannter Teilnehmer";
    }
}
