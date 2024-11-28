package com.example.Interfaces;

import com.example.Hilfsmethoden;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.IntermediateCatchEvent;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;

import java.util.Collection;
import java.util.Iterator;

import static com.example.Hilfsmethoden.getParticipantRole;
import static com.example.SBVRTransformerNEU.createIntermediateCatchEventStatement;

public class IntermediateCatchEventTransformer implements FlowNodeTransformer {

    @Override
    public String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes) {
        // Überprüfen, ob der FlowNode ein IntermediateCatchEvent ist
        if (node instanceof IntermediateCatchEvent) {
            // Hole die EventDefinitions des IntermediateCatchEvents
            Collection<?> eventDefinitions = ((IntermediateCatchEvent) node).getEventDefinitions();

            // Stelle sicher, dass es eine MessageEventDefinition gibt
            Iterator<?> iterator = eventDefinitions.iterator();
            if (iterator.hasNext()) {
                Object definition = iterator.next();

                // Prüfen, ob es sich um eine MessageEventDefinition handelt
                if (definition instanceof MessageEventDefinition) {
                    MessageEventDefinition messageEventDefinition = (MessageEventDefinition) definition;

                    // Sicherstellen, dass eine Message vorhanden ist
                    if (messageEventDefinition.getMessage() != null) {
                        String senderName = messageEventDefinition.getMessage().getName();
                        String receiverName = senderName; // Empfänger hier als Beispiel

                        // Hole die Teilnehmerrollen basierend auf den Lanes
                        String senderRole = getParticipantRole(senderName, (BpmnModelInstance) lanes);
                        String receiverRole = getParticipantRole(receiverName, (BpmnModelInstance) lanes);

                        // Erstelle die SBVR-Regel mit den extrahierten Daten
                        String sbvrStatement = createIntermediateCatchEventStatement(senderRole, senderName, receiverRole, receiverName);

                        // Ausgabe der SBVR-Regel zur Konsole
                        System.out.println(sbvrStatement);

                        return sbvrStatement; // Gibt die vollständige SBVR-Regel zurück
                    } else {
                        System.out.println("Warnung: Message in MessageEventDefinition ist null.");
                        return "Warnung: Keine Nachricht in der MessageEventDefinition definiert.";
                    }
                } else {
                    System.out.println("Warnung: EventDefinition ist keine MessageEventDefinition.");
                    return "Warnung: EventDefinition ist keine MessageEventDefinition.";
                }
            } else {
                return "Es wurde keine EventDefinition gefunden.";
            }
        } else {
            // Falls es kein IntermediateCatchEvent ist
            return "Der angegebene FlowNode ist kein IntermediateCatchEvent.";
        }
    }
}