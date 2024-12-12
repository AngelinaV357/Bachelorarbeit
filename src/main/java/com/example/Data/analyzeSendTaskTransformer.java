package com.example.Data;

import com.example.Hilfsmethoden;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import java.util.Collection;

import static com.example.Hilfsmethoden.*;
import static com.example.main.SBVRTransformerNEU.createSendTaskStatement;

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
            String taskName = Hilfsmethoden.sanitizeName(sendTask.getName());

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
}
