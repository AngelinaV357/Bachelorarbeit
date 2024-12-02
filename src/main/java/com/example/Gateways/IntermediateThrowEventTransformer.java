package com.example.Gateways;

import com.example.Hilfsmethoden;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;

import java.util.Collection;

import static com.example.Hilfsmethoden.getParticipantRole;
import static com.example.SBVRTransformerNEU.createIntermediateThrowEventStatement;

public class IntermediateThrowEventTransformer {
    public static String processAndTransformIntermediateThrowEvents(
            MessageFlow messageFlow, // Verwenden des MessageFlow-Parameters
            Collection<Lane> lanes,
            BpmnModelInstance modelInstance,
            StringBuilder sbvrOutput) {

        // Hole die Namen des Senders und Empfängers
        String senderName = Hilfsmethoden.getName((FlowNode) messageFlow.getSource());
        String receiverName = Hilfsmethoden.getName((FlowNode) messageFlow.getTarget());

        // Hole die Teilnehmerrollen basierend auf der XML-Datenstruktur und dem BpmnModelInstance
        String senderRole = getParticipantRole(senderName, modelInstance);
        String receiverRole = getParticipantRole(receiverName, modelInstance);

        // Hole den Nachrichtenname (falls vorhanden, ansonsten "unbekannte Nachricht")
        String messageName = messageFlow.getName() != null ? messageFlow.getName() : "unbekannte Nachricht";

        // Erstelle die SBVR-Regel mit der neuen Methode
        String sbvrStatement = createIntermediateThrowEventStatement(senderRole, senderName, receiverRole, receiverName, messageName);

        // Ausgabe in der Konsole
        System.out.println(sbvrStatement);

        // SBVR-Transformation hinzufügen
        sbvrOutput.append(sbvrStatement).append("\n");
        return senderName;
        }
    }