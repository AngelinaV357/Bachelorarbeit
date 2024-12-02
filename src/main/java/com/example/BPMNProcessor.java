package com.example;

import com.example.Data.DataObjectAnalysis;
import com.example.Task.ActivityTransformer;
import com.example.Task.EndEventTransformer;
import com.example.Data.UserTaskTransformer;
import com.example.Gateways.ANDGatewayTransformer;
import com.example.Gateways.ORGatewayTransformer;
import com.example.Gateways.XORGatewayTransformer;
import com.example.Task.StartEventTransformer;
import com.example.Task.SubProcessTransformer;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.io.*;
import java.io.DataInput;
import java.util.*;

import static com.example.Task.SubProcessTransformer.processSubProcessesAndMessageFlows;

public class BPMNProcessor { //BPMN Modell verarbeiten
    // Liest die BPMN-Datei ein und gibt das BpmnModelInstance zurück
    static BpmnModelInstance readBpmnFile(File file) {
        System.out.println("BPMN-Datei wird eingelesen...");
        return Bpmn.readModelFromFile(file);
    }

    // Verarbeitet die Sequenzflüsse
    public static void processSequenceFlows(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Holt alle Sequenzflüsse (SequenceFlow) aus dem Modell
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
        // Holt alle MessageFlows
        Collection<MessageFlow> messageFlows = modelInstance.getModelElementsByType(MessageFlow.class);
        // Holt alle DataInputAssociations
        Collection<DataInputAssociation> dataInputAssociations = modelInstance.getModelElementsByType(DataInputAssociation.class);
        // Holt alle Lane-Elemente, die für die Zuweisung von Rollen verwendet werden
        Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);
        Set<String> processedGateways = new HashSet<>();

        // Verarbeite Subprozesse und Message Flows
        processSubProcessesAndMessageFlows(modelInstance, sbvrOutput);

        DataObjectAnalysis dataObjectAnalysis = new DataObjectAnalysis();

        // Füge einen Header für die DataObject-Analyse hinzu
        sbvrOutput.append("DataObject-Analyse:\n");

        // Rufe die Methode analyzeDataObjects auf und übergib sbvrOutput, um die Ergebnisse zu integrieren
        dataObjectAnalysis.analyzeDataObjects(modelInstance, sbvrOutput);

        for (SequenceFlow flow : sequenceFlows) {
            FlowNode source = flow.getSource();
            FlowNode target = flow.getTarget();
            String sourceRole = Hilfsmethoden.getRoleForNode(source, lanes);
            String targetRole = Hilfsmethoden.getRoleForNode(target, lanes);

            // Verarbeite Subprozesse (falls der Source-Knoten oder Target-Knoten ein Subprozess ist)
//            if (source instanceof SubProcess subProcess) {
//                SubProcessTransformer.processSubProcessesForFlowNode(subProcess, sbvrOutput, lanes); // Methode speziell für Subprozess-FlowNodes
//            }

//            // Hier integrieren wir die Aufrufe für DataObjectReference und DataInput
//            if (source instanceof DataObjectReference dataObjectReference) {
//                // Aufruf der Methode createDataReferenceSBVR
//                dataObjectAnalysis.createDataReferenceSBVR(dataObjectReference, sbvrOutput);
//            } else if (source instanceof DataInput dataInput) {
//                // Aufruf der Methode createDataInputSBVR
//                dataObjectAnalysis.createDataInputSBVR((org.camunda.bpm.model.bpmn.instance.DataInput) dataInput, sbvrOutput);
//            }

            // Verarbeite reguläre Knoten
//            if (source instanceof StartEvent startEvent) {
//                sbvrOutput.append(new StartEventTransformer().transformFlowNode(startEvent, sourceRole, targetRole, lanes)).append("\n");
//            } else if (source instanceof ExclusiveGateway exclusiveGateway) {
//                sbvrOutput.append(new XORGatewayTransformer().transformFlowNode(exclusiveGateway, sourceRole, targetRole, lanes)).append("\n");
//            } else if (source instanceof ParallelGateway parallelGateway) {
//                sbvrOutput.append(new ANDGatewayTransformer().transformFlowNode(parallelGateway, sourceRole, targetRole, lanes)).append("\n");
//            } else if (source instanceof InclusiveGateway inclusiveGateway) {
//                sbvrOutput.append(new ORGatewayTransformer().transformFlowNode(inclusiveGateway, sourceRole, targetRole, lanes)).append("\n");
//            } else if (source instanceof UserTask userTask) {
//                sbvrOutput.append(new UserTaskTransformer().transformFlowNode(userTask, sourceRole, targetRole, lanes)).append("\n");
//            } else if (source instanceof Activity activity) {
//                sbvrOutput.append(new ActivityTransformer().transformFlowNode(activity, sourceRole, targetRole, lanes)).append("\n");
//            } else if (source instanceof EndEvent endEvent) {
//                sbvrOutput.append(new EndEventTransformer().transformFlowNode(endEvent, sourceRole, targetRole, lanes)).append("\n");
//            } else {
//                sbvrOutput.append("Nicht unterstützter Knoten-Typ: ").append(source.getClass().getSimpleName()).append("\n");
//            }
        }
    }
}

//        // Verarbeitung der MessageFlows
//        for (MessageFlow messageFlow : messageFlows) {
//            // Die Methode zur Verarbeitung des MessageFlows aufrufen
//            String senderName = IntermediateThrowEventTransformer.processAndTransformIntermediateThrowEvents(messageFlow, lanes, modelInstance, sbvrOutput);
//        }
//// Verarbeitung der DataInputAssociations
//        for (DataInputAssociation dataInputAssociation : dataInputAssociations) {
//            Collection<ItemAwareElement> sources = dataInputAssociation.getSources();
//            ModelElementInstance targetElement = dataInputAssociation.getTarget();
//
//            // Verarbeite alle Quellen
//            for (ModelElementInstance sourceElement : sources) {
//                // Falls die Quelle ein Datenobjekt ist
//                if (sourceElement instanceof DataObjectReference dataObjectReference) {
//                    String dataName = dataObjectReference.getAttributeValue("name");
//
//                    // Verarbeite das Ziel, falls es ein FlowNode ist
//                    if (targetElement instanceof FlowNode targetNode) {
//                        String targetName = targetNode.getName();
//                        String targetRole = Hilfsmethoden.getRoleForNode(targetNode, lanes);
//
//                        // SBVR-Transformation für DataObjectReference
//                        sbvrOutput.append("Das Datenobjekt '")
//                                .append(dataName)
//                                .append("' wird für die Aktivität '")
//                                .append(targetName)
//                                .append("' verwendet (Rolle: ")
//                                .append(targetRole)
//                                .append(").")
//                                .append("\n");
//                    } else {
//                        // Logge das Ziel, falls es kein FlowNode ist
//                        sbvrOutput.append("DataInputAssociation mit Ziel-Typ: ")
//                                .append(targetElement.getClass().getSimpleName())
//                                .append("\n");
//                    }
//                } else {
//                    // Logge die Quelle, falls sie kein DataObjectReference ist
//                    sbvrOutput.append("DataInputAssociation mit Quell-Typ: ")
//                            .append(sourceElement.getClass().getSimpleName())
//                            .append("\n");
//                }
//            }
//        }
//    }
