package com.example;

import com.example.Data.DataObjectAnalysis;
import com.example.Data.analyzeMessageFlowsForActivities;
import com.example.Data.analyzeUserTaskTransformer;
import com.example.Event.IntermediateCatchEventAnalyzer;
import com.example.Event.IntermediateThrowEventAnalyzer;
import com.example.Gateways.*;
import com.example.Task.ActivityTransformer;
import com.example.Task.EndEventTransformer;
import com.example.Task.StartEventTransformer;
import com.example.Task.SubProcessTransformer;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.io.*;
import java.util.*;

import static com.example.Data.analyzeBusinessTaskTransformer.analyzeBusinessTasks;
import static com.example.Data.analyzeSendTaskTransformer.analyzeSendTasks;
import static com.example.Data.analyzeServiceTaskTransformer.analyzeServiceTasks;

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
        // Holt alle Teilnehmer
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);

        // Verwende Sets, um Duplikate zu vermeiden
        Set<FlowNode> processedNodes = new HashSet<>();
        Set<String> processedTasks = new HashSet<>();

        // Aufruf zur Analyse von Datenobjekten
//        DataObjectAnalysis dataObjectAnalysis = new DataObjectAnalysis();
//        dataObjectAnalysis.analyzeDataObjects(modelInstance, sbvrOutput);
//
//        // Event-Analyser
//        IntermediateCatchEventAnalyzer.analyzeCatchEvents(modelInstance, sbvrOutput);
//        IntermediateThrowEventAnalyzer.analyzeThrowEvents(modelInstance, sbvrOutput);
//
//        // Aufruf zur Analyse von SendTasks und weiteren Tasks
//        analyzeSendTasks(modelInstance, sbvrOutput);
//        analyzeUserTaskTransformer.analyzeUserTasks(modelInstance, sbvrOutput);
//        analyzeServiceTasks(modelInstance, sbvrOutput);
//        analyzeBusinessTasks(modelInstance, sbvrOutput);
//
//        analyzeMessageFlowsForActivities.analyzeActivities(modelInstance, sbvrOutput);
//
//        SubProcessTransformer.processSubProcessesAndMessageFlows(modelInstance, sbvrOutput);

        for (SequenceFlow flow : sequenceFlows) {
            FlowNode source = flow.getSource();
            FlowNode target = flow.getTarget();
            String sourceRole = Hilfsmethoden.getRoleForNode(source, lanes);
            String targetRole = Hilfsmethoden.getRoleForNode(target, lanes);

            // Überspringe bereits verarbeitete Knoten
            if (processedNodes.contains(source)) {
                continue;
            }

            processedNodes.add(source);  // Markiere diesen Knoten als verarbeitet

            // Verarbeite reguläre Knoten
            if (source instanceof StartEvent startEvent) {
                sbvrOutput.append(new StartEventTransformer().transformFlowNode(startEvent, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof ExclusiveGateway exclusiveGateway) {
                sbvrOutput.append(new XORGatewayTransformer().transformFlowNode(exclusiveGateway, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof ParallelGateway parallelGateway) {
                sbvrOutput.append(new ANDGatewayTransformer().transformFlowNode(parallelGateway, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof InclusiveGateway inclusiveGateway) {
                sbvrOutput.append(new ORGatewayTransformer().transformFlowNode(inclusiveGateway, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof EventBasedGateway eventBasedGateway) {
                EventBasedGatewayTransformer.EventGatewayTransformer(eventBasedGateway, sbvrOutput);
            } else if (source instanceof Activity activity) {
                sbvrOutput.append(new ActivityTransformer().transformFlowNode(activity, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof EndEvent endEvent) {
                sbvrOutput.append(new EndEventTransformer().transformFlowNode(endEvent, sourceRole, targetRole, lanes)).append("\n");
            } else {
                sbvrOutput.append("Nicht unterstützter Knoten-Typ: ").append(source.getClass().getSimpleName()).append("\n");
            }

            // Verarbeite spezielle Tasks, falls sie noch nicht bearbeitet wurden
            if (source instanceof UserTask userTask) {
                if (!processedTasks.contains(userTask.getId())) {
                    sbvrOutput.append(new ActivityTransformer().transformFlowNode(userTask, sourceRole, targetRole, lanes)).append("\n");
                    processedTasks.add(userTask.getId());
                }
            } else if (source instanceof ServiceTask serviceTask) {
                if (!processedTasks.contains(serviceTask.getId())) {
                    sbvrOutput.append(new ActivityTransformer().transformFlowNode(serviceTask, sourceRole, targetRole, lanes)).append("\n");
                    processedTasks.add(serviceTask.getId());
                }
            }
        }

        DataObjectAnalysis dataObjectAnalysis = new DataObjectAnalysis();
        dataObjectAnalysis.analyzeDataObjects(modelInstance, sbvrOutput);

        // Event-Analyser
        IntermediateCatchEventAnalyzer.analyzeCatchEvents(modelInstance, sbvrOutput);
        IntermediateThrowEventAnalyzer.analyzeThrowEvents(modelInstance, sbvrOutput);

        // Aufruf zur Analyse von SendTasks und weiteren Tasks
        analyzeSendTasks(modelInstance, sbvrOutput);
        analyzeUserTaskTransformer.analyzeUserTasks(modelInstance, sbvrOutput);
        analyzeServiceTasks(modelInstance, sbvrOutput);
        analyzeBusinessTasks(modelInstance, sbvrOutput);

        analyzeMessageFlowsForActivities.analyzeActivities(modelInstance, sbvrOutput);

        SubProcessTransformer.processSubProcessesAndMessageFlows(modelInstance, sbvrOutput);

        // Stelle sicher, dass EndEvents auch ohne ausgehende SequenceFlows berücksichtigt werden
        for (FlowNode node : modelInstance.getModelElementsByType(FlowNode.class)) {
            if (node instanceof EndEvent && !processedNodes.contains(node)) {
                String sourceRole = Hilfsmethoden.getRoleForNode(node, lanes);
                sbvrOutput.append(new EndEventTransformer().transformFlowNode((EndEvent) node, sourceRole, null, lanes)).append("\n");
            }
        }
    }

}


