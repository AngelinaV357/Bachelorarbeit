package com.example.XMLIteration.Task.main;

import com.example.XMLIteration.Task.Data.DataObjectAnalysis;
import com.example.XMLIteration.Task.Data.TestAnnotationAnalysis;
//import com.example.Data.analyzeMessageFlowsForActivities;
import com.example.XMLIteration.Task.Data.analyzeUserTaskTransformer;
import com.example.XMLIteration.Task.Event.IntermediateCatchEventAnalyzer;
import com.example.XMLIteration.Task.Event.IntermediateThrowEventAnalyzer;

import com.example.XMLIteration.Task.Gateways.ANDGatewayTransformer;
import com.example.XMLIteration.Task.Gateways.EventBasedGatewayTransformer;
import com.example.XMLIteration.Task.Gateways.ORGatewayTransformer;
import com.example.XMLIteration.Task.Gateways.XORGatewayTransformer;
import com.example.XMLIteration.Task.Hilfsmethoden;
import com.example.XMLIteration.Task.ActivityTransformer;
import com.example.XMLIteration.Task.EndEventTransformer;
import com.example.XMLIteration.Task.StartEventTransformer;
import com.example.XMLIteration.Task.SubProcessTransformer;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.io.*;
import java.util.*;

import static com.example.XMLIteration.Task.Data.analyzeBusinessTaskTransformer.analyzeBusinessTasks;
import static com.example.XMLIteration.Task.Data.analyzeSendTaskTransformer.analyzeSendTasks;
import static com.example.XMLIteration.Task.Data.analyzeServiceTaskTransformer.analyzeServiceTasks;

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
        // Holt alle Lane-Elemente, die für die Zuweisung von Rollen verwendet werden
        List<Lane> lanes = new ArrayList<>(modelInstance.getModelElementsByType(Lane.class)); // Umwandlung in eine Liste für den Indexzugriff

        // Verwende Sets, um Duplikate zu vermeiden
        Set<FlowNode> processedNodes = new HashSet<>();
        Set<String> processedTasks = new HashSet<>();

        // Zuerst das StartEvent verarbeiten
        for (SequenceFlow flow : sequenceFlows) {
            FlowNode source = flow.getSource();
            if (source instanceof StartEvent startEvent && !processedNodes.contains(source)) {
                String sourceRole = Hilfsmethoden.getRoleForNode(source, lanes);
                String targetRole = Hilfsmethoden.getRoleForNode(flow.getTarget(), lanes);

                Collection<Participant> participants = startEvent.getModelInstance().getModelElementsByType(Participant.class);

                sbvrOutput.append(new StartEventTransformer().processStartEvent(startEvent, sourceRole, targetRole, participants)).append("\n");
                processedNodes.add(source);
            }
        }

        // Danach alle anderen Sequenzflüsse und Knoten verarbeiten
        for (SequenceFlow flow : sequenceFlows) {
            FlowNode source = flow.getSource();
            if (processedNodes.contains(source)) {
                continue;  // Überspringe bereits verarbeitete Knoten
            }
            String sourceRole = Hilfsmethoden.getRoleForNode(source, lanes);
            String targetRole = Hilfsmethoden.getRoleForNode(flow.getTarget(), lanes);

            // Verarbeite reguläre Knoten
            if (source instanceof ExclusiveGateway exclusiveGateway) {
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
                sbvrOutput.append("");
            }

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

            processedNodes.add(source);
        }

        // Außerhalb der Schleife, da diese nicht direkt an Sequence Flows gebunden sein müssen
        // Zusätzliche Datenanalysen
        DataObjectAnalysis dataObjectAnalysis = new DataObjectAnalysis();
        dataObjectAnalysis.analyzeDataObjects(modelInstance, sbvrOutput);

        TestAnnotationAnalysis analysis = new TestAnnotationAnalysis();
        TestAnnotationAnalysis.analyzeTextAnnotations(modelInstance, sbvrOutput);

        // Event-Analysen
        IntermediateCatchEventAnalyzer.analyzeCatchEvents(modelInstance, sbvrOutput);
        IntermediateThrowEventAnalyzer.analyzeThrowEvents(modelInstance, sbvrOutput);

        // Weitere Task-Analysen
        analyzeSendTasks(modelInstance, sbvrOutput);
        analyzeUserTaskTransformer.analyzeUserTasks(modelInstance, sbvrOutput);
        analyzeServiceTasks(modelInstance, sbvrOutput);
        analyzeBusinessTasks(modelInstance, sbvrOutput);

//        analyzeMessageFlowsForActivities.analyzeActivities(modelInstance, sbvrOutput);

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
