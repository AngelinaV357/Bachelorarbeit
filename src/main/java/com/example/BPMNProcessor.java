package com.example;

import com.example.Interfaces.*;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.io.*;
import java.io.DataInput;
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

            if (source instanceof StartEvent startEvent) {
                sbvrOutput.append(new StartEventTransformer().transformFlowNode(startEvent, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof ExclusiveGateway exclusiveGateway) {
                sbvrOutput.append(new XORGatewayTransformer().transformFlowNode(exclusiveGateway, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof ParallelGateway parallelGateway) {
                sbvrOutput.append(new ANDGatewayTransformer().transformFlowNode(parallelGateway, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof InclusiveGateway inclusiveGateway) {
                sbvrOutput.append(new ORGatewayTransformer().transformFlowNode(inclusiveGateway, sourceRole, targetRole, lanes)).append("\n");
//            } else if (source instanceof EventBasedGateway eventBasedGateway) {
//                sbvrOutput.append(new EventBasedTransformer().transformFlowNode(eventBasedGateway, sourceRole, targetRole, lanes)).append("\n");
//            } else if (source instanceof IntermediateCatchEvent intermediateCatchEvent) {  // Neuer Block für IntermediateCatchEvent
//                sbvrOutput.append(new IntermediateCatchEventTransformer().transformFlowNode(intermediateCatchEvent, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof SendTask sendTask) {
                sbvrOutput.append(new SendTaskTransformer().transformFlowNode(sendTask, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof ServiceTask serviceTask) {
                sbvrOutput.append(new ServiceTaskTransformer().transformFlowNode(serviceTask, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof UserTask userTask) {
                sbvrOutput.append(new UserTaskTransformer().transformFlowNode(userTask, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof Activity activity) {  // Hier prüfen, ob es eine Aktivität ist
                sbvrOutput.append(new ActivityTransformer().transformFlowNode(activity, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof DataObjectReference dataObjectReference) {
                sbvrOutput.append(new DataObjectReferenceTransformer().transformFlowNode((FlowNode) dataObjectReference, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof DataObject dataObject) {
                sbvrOutput.append(new DataObjectTransformer().transformFlowNode((FlowNode) dataObject, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof DataInput dataInput) {
                sbvrOutput.append(new DataInputTransformer().transformFlowNode((FlowNode) dataInput, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof EndEvent endEvent) {
                sbvrOutput.append(new EndEventTransformer().transformFlowNode(endEvent, sourceRole, targetRole, lanes)).append("\n");
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

}