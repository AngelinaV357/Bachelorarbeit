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
import com.example.Gateways.EventBasedGatewayTransformer;

import java.io.*;
import java.util.*;

import static com.example.Task.SubProcessTransformer.processSubProcessesAndMessageFlows;

public class BPMNProcessor { //BPMN Modell verarbeiten
    // Liest die BPMN-Datei ein und gibt das BpmnModelInstance zurück
    static BpmnModelInstance readBpmnFile(File file) {
        System.out.println("BPMN-Datei wird eingelesen...");
        return Bpmn.readModelFromFile(file);
    }

//    public static void processNodes(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
//        // Holt alle FlowNodes aus dem Modell
//        Collection<FlowNode> flowNodes = modelInstance.getModelElementsByType(FlowNode.class);
//        // Holt alle Sequenzflüsse aus dem Modell
//        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
//        // Holt alle Lane-Elemente, die für die Zuweisung von Rollen verwendet werden
//        Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);
//
//        // Verarbeite Subprozesse und Nachrichtenaustausch
////        processSubProcessesAndMessageFlows(modelInstance, sbvrOutput);
//
//        // Verarbeite Datenobjekte
//        DataObjectAnalysis dataObjectAnalysis = new DataObjectAnalysis();
//        dataObjectAnalysis.analyzeDataObjects(modelInstance, sbvrOutput);
//
//        // **Hier wird der Event-Based Gateway-Transformer aufgerufen**
//        EventBasedGatewayTransformer.EventGatewayTransformer(modelInstance, sbvrOutput);
//
//        for (FlowNode node : flowNodes) {
//            String sourceRole = Hilfsmethoden.getRoleForNode(node, lanes);
//
//            // Finde alle Sequenzflüsse, die vom aktuellen Knoten ausgehen
//            List<SequenceFlow> outgoingFlows = sequenceFlows.stream()
//                    .filter(flow -> flow.getSource().equals(node))
//                    .toList();
//
//            // Verarbeite jeden Sequenzfluss separat
//            for (SequenceFlow flow : outgoingFlows) {
//                FlowNode target = flow.getTarget();
//                String targetRole = Hilfsmethoden.getRoleForNode(target, lanes);
//
//                if (node instanceof StartEvent startEvent) {
//                    sbvrOutput.append(new StartEventTransformer().transformFlowNode(startEvent, sourceRole, targetRole, lanes)).append("\n");
//                } else if (node instanceof ExclusiveGateway exclusiveGateway) {
//                    sbvrOutput.append(new XORGatewayTransformer().transformFlowNode(exclusiveGateway, sourceRole, targetRole, lanes)).append("\n");
//                } else if (node instanceof ParallelGateway parallelGateway) {
//                    sbvrOutput.append(new ANDGatewayTransformer().transformFlowNode(parallelGateway, sourceRole, targetRole, lanes)).append("\n");
//                } else if (node instanceof InclusiveGateway inclusiveGateway) {
//                    sbvrOutput.append(new ORGatewayTransformer().transformFlowNode(inclusiveGateway, sourceRole, targetRole, lanes)).append("\n");
//                } else if (node instanceof Activity activity) {
//                    sbvrOutput.append(new ActivityTransformer().transformFlowNode(activity, sourceRole, targetRole, lanes)).append("\n");
//                } else if (node instanceof EndEvent endEvent) {
//                    sbvrOutput.append(new EndEventTransformer().transformFlowNode(endEvent, sourceRole, targetRole, lanes)).append("\n");
//                } else if (node instanceof SubProcess subProcess) {
//                    SubProcessTransformer.processSubProcessesForFlowNode(subProcess, sbvrOutput, lanes);
//                } else {
//                    sbvrOutput.append("Nicht unterstützter Knoten-Typ: ").append(node.getClass().getSimpleName()).append("\n");
//                }
//            }
//
//            // Falls der Knoten keine ausgehenden Sequenzflüsse hat, verarbeite ihn trotzdem
//            if (outgoingFlows.isEmpty() && !(node instanceof StartEvent)) {
//                if (node instanceof EndEvent endEvent) {
//                    sbvrOutput.append(new EndEventTransformer().transformFlowNode(endEvent, sourceRole, null, lanes)).append("\n");
//                } else if (node instanceof SubProcess subProcess) {
//                    SubProcessTransformer.processSubProcessesForFlowNode(subProcess, sbvrOutput, lanes);
//                }
//            }
//        }
//    }
//}

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


        processSubProcessesAndMessageFlows(modelInstance, sbvrOutput);

        DataObjectAnalysis dataObjectAnalysis = new DataObjectAnalysis();

        dataObjectAnalysis.analyzeDataObjects(modelInstance, sbvrOutput);

        for (SequenceFlow flow : sequenceFlows) {
            FlowNode source = flow.getSource();
            FlowNode target = flow.getTarget();
            String sourceRole = Hilfsmethoden.getRoleForNode(source, lanes);
            String targetRole = Hilfsmethoden.getRoleForNode(target, lanes);

            // Verarbeite Subprozesse (falls der Source-Knoten oder Target-Knoten ein Subprozess ist)
            if (source instanceof SubProcess subProcess) {
                SubProcessTransformer.processSubProcessesForFlowNode(subProcess, sbvrOutput, lanes); // Methode speziell für Subprozess-FlowNodes
            }


            // Verarbeite reguläre Knoten
            if (source instanceof StartEvent startEvent) {
                sbvrOutput.append(new StartEventTransformer().transformFlowNode(startEvent, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof ExclusiveGateway exclusiveGateway) {
                sbvrOutput.append(new XORGatewayTransformer().transformFlowNode(exclusiveGateway, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof ParallelGateway parallelGateway) {
                sbvrOutput.append(new ANDGatewayTransformer().transformFlowNode(parallelGateway, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof InclusiveGateway inclusiveGateway) {
                sbvrOutput.append(new ORGatewayTransformer().transformFlowNode(inclusiveGateway, sourceRole, targetRole, lanes)).append("\n");
//            }else if (source instanceof EventBasedGateway eventBasedGateway) {
//                EventGatewayTransformer((BpmnModelInstance) eventBasedGateway, sbvrOutput);
            } else if (source instanceof UserTask userTask) {
                sbvrOutput.append(new UserTaskTransformer().transformFlowNode(userTask, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof Activity activity) {
                sbvrOutput.append(new ActivityTransformer().transformFlowNode(activity, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof EndEvent endEvent) {
                sbvrOutput.append(new EndEventTransformer().transformFlowNode(endEvent, sourceRole, targetRole, lanes)).append("\n");
            } else {
                sbvrOutput.append("Nicht unterstützter Knoten-Typ: ").append(source.getClass().getSimpleName()).append("\n");
            }
        }
    }
}


