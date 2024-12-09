package com.example.Data;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.bpmn.instance.BaseElement;

import java.util.Collection;

import static com.example.Hilfsmethoden.getMessageFlowParticipantName;
import static com.example.Hilfsmethoden.getRoleForNode;

public class analyzeSendTaskTransformer {
    public static void analyzeSendTasks(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Alle SendTasks abrufen
        Collection<SendTask> sendTasks = modelInstance.getModelElementsByType(SendTask.class);

        // Holt alle Lane-Elemente, die für die Zuweisung von Rollen verwendet werden
        Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);

        // Holt alle MessageFlows
        Collection<MessageFlow> messageFlows = modelInstance.getModelElementsByType(MessageFlow.class);

        // Holt alle Teilnehmer (Participants)
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);

        // Iteriere durch alle SendTasks
        for (SendTask sendTask : sendTasks) {
            // Namen des SendTasks abrufen
            String taskName = sanitizeName(sendTask.getName());

            // Rolle des Senders ermitteln
            String senderRole = getRoleForNode(sendTask, lanes);

            // Suche den passenden MessageFlow, um den Empfänger zu ermitteln
            String targetName = getMessageFlowParticipantNameFromSendTask(sendTask, messageFlows, participants);

            // Generiere das Statement für den SendTask
            String sendTaskStatement = createSendTaskStatement(taskName, senderRole, targetName);

            // Füge das Statement zum sbvrOutput hinzu
            sbvrOutput.append(sendTaskStatement);
        }
        sbvrOutput.append("\n");
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
     * Erstellt die Aussage für einen SendTask
     */
    public static String createSendTaskStatement(String taskName, String senderRole, String receiverRole) {
        return "Es ist notwendig, dass die Ressource mit der Rolle '" + senderRole + "' die Nachricht '" + taskName + "' an die Ressource mit der Rolle '" + receiverRole + "' sendet.\n";
    }

    /**
     * Hilfsmethode, um den Teilnehmernamen aus dem MessageFlow zu extrahieren
     */
    private static String getMessageFlowParticipantNameFromSendTask(SendTask sendTask, Collection<MessageFlow> messageFlows, Collection<Participant> participants) {
        // Iteriere durch alle MessageFlows, um den passenden Empfänger zu finden
        for (MessageFlow messageFlow : messageFlows) {
            BaseElement source = (BaseElement) messageFlow.getSource();
            BaseElement target = (BaseElement) messageFlow.getTarget();

            // Überprüfe, ob der SendTask die Quelle ist
            if (source.equals(sendTask)) {
                // Finde den Teilnehmer des Ziels
                return getMessageFlowParticipantName(target, participants);
            }
        }
        return "Unbekannter Teilnehmer";
    }
}
