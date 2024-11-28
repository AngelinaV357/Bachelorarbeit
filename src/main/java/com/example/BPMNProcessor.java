package com.example;

import com.example.Interfaces.*;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.io.*;
import java.util.*;

import static com.example.Hilfsmethoden.*;
import static com.example.SBVRTransformerNEU.*;

public class BPMNProcessor { //BPMN Modell verarbeiten
    // Liest die BPMN-Datei ein und gibt das BpmnModelInstance zurück
    static BpmnModelInstance readBpmnFile(File file) {
        System.out.println("BPMN-Datei wird eingelesen...");
        return Bpmn.readModelFromFile(file);
    }

    // Verarbeitet die Sequenzflüsse
    public static void processSequenceFlows(BpmnModelInstance modelInstance, StringBuilder standardOutput, StringBuilder sbvrOutput) {
        // Holt alle Sequenzflüsse (SequenceFlow) aus dem Modell
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
        // Holt alle Lane-Elemente, die für die Zuweisung von Rollen verwendet werden
        Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);
        Set<String> processedGateways = new HashSet<>();

        // Iteriert über alle Sequenzflüsse
        for (SequenceFlow flow : sequenceFlows) {
            // Bestimmt den Quell- und Zielknoten des Sequenzflusses
            FlowNode source = flow.getSource();
            FlowNode target = flow.getTarget();
            // Bestimmt die Rolle des Quell- und Zielknotens basierend auf den Lanes
            String sourceRole = Hilfsmethoden.getRoleForNode(source, lanes);
            String targetRole = Hilfsmethoden.getRoleForNode(target, lanes);

//            // Überprüft den Typ des Quellknotens und ruft die passende Verarbeitungsmethode auf
//            switch (source.getClass().getSimpleName()) {
//                case "StartEvent" ->
//                        sbvrOutput.append(new StartEventTransformer().transformFlowNode((StartEvent) source, sourceRole, targetRole, lanes)).append("\n");
//                case "EndEvent" ->
//                        sbvrOutput.append(transformEndEventToSBVR((EndEvent) source, sourceRole, targetRole, lanes)).append("\n");
//
//            }

            if (source instanceof StartEvent startEvent) {
                sbvrOutput.append(new StartEventTransformer().transformFlowNode(startEvent, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof EndEvent endEvent) {
                sbvrOutput.append(new EndEventTransformer().transformFlowNode(endEvent, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof ExclusiveGateway exclusiveGateway) {
                sbvrOutput.append(new XORGatewayTransformer().transformFlowNode(exclusiveGateway, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof ParallelGateway parallelGateway) {
                sbvrOutput.append(new ANDGatewayTransformer().transformFlowNode(parallelGateway, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof InclusiveGateway inclusiveGateway) {
                sbvrOutput.append(new ORGatewayTransformer().transformFlowNode(inclusiveGateway, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof EventBasedGateway eventBasedGateway) {
                sbvrOutput.append(new EventBasedTransformer().transformFlowNode(eventBasedGateway, sourceRole, targetRole, lanes));
//            } else if (source instanceof IntermediateCatchEvent intermediateCatchEvent) {
//                    String sbvrStatement = processAndTransformIntermediateThrowEvents(messageFlow, lanes, modelInstance, sbvrOutput);
//                    sbvrOutput.append(sbvrStatement).append("\n");
//                } else {
//                    System.out.println("Kein MessageFlow für das IntermediateCatchEvent gefunden.");
//                }
//            } else if (source instanceof IntermediateThrowEvent intermediateThrowEvent) {
                // Hole den MessageFlow für das IntermediateThrowEvent (dies muss entsprechend deinem Modell implementiert werden)
                //MessageFlow messageFlow = getMessageFlowForEvent((IntermediateCatchEvent) intermediateThrowEvent, modelInstance);
                // Rufe die Methode auf, um die SBVR-Transformation durchzuführen
                //processAndTransformIntermediateThrowEvents(messageFlow, lanes, modelInstance, sbvrOutput);
            } else if (source instanceof SubProcess) {
//                processSubProcesses((SubProcess) source, standardOutput, sbvrOutput);
            } else {
                // Für alle anderen Knoten wird ein generisches Logging eingefügt
                standardOutput.append("Nicht unterstützter Knoten-Typ: ").append(source.getClass().getSimpleName()).append("\n");
            }
        }
    }

        //    Throw event empfängt etwas von dem Participant; ausgemaltes Nachrichtenzeichen
        static String processAndTransformIntermediateThrowEvents(
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

        public static String processAndTransformIntermediateCatchEvents(MessageFlow messageFlow, Collection<Lane> lanes, BpmnModelInstance modelInstance) {
        // Hole die Namen des Senders und Empfängers
        String senderName = Hilfsmethoden.getName((FlowNode) messageFlow.getSource());
        String receiverName = Hilfsmethoden.getName((FlowNode) messageFlow.getTarget());

        // Hole die Teilnehmerrollen basierend auf der XML-Datenstruktur und dem BpmnModelInstance
        String senderRole = getParticipantRole(senderName, modelInstance);  // Verwendet getParticipantRole statt getParticipantName
        String receiverRole = getParticipantRole(receiverName, modelInstance);  // Verwendet getParticipantRole statt getParticipantName

        // Verwende die Methode createIntermediateCatchEventStatement zur Erstellung der SBVR-Regel
        String sbvrStatement = createIntermediateCatchEventStatement(senderRole, senderName, receiverRole, receiverName);

        // Ausgabe zur Konsole
        System.out.println(sbvrStatement);

        return sbvrStatement;  // Gibt die vollständige Ausgabe zurück, wenn benötigt
    }
}