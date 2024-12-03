package com.example;

import com.example.Data.DataObjectAnalysis;
import com.example.Event.IntermediateCatchEventAnalyzer;
import com.example.Event.IntermediateThrowEventAnalyzer;
import com.example.Gateways.*;
import com.example.Task.ActivityTransformer;
import com.example.Task.EndEventTransformer;
import com.example.Data.analyzeUserTask;
import com.example.Task.StartEventTransformer;
import com.example.Task.SubProcessTransformer;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.io.*;
import java.util.*;

import static com.example.Data.analyzeSendTask.analyzeSendTasks;
import static com.example.Data.analyzeServiceTask.analyzeServiceTasks;
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
        // Holt alle Teilnehmer
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);

        // Aufruf der Methode zur Verarbeitung von Subprozessen und MessageFlows
        processSubProcessesAndMessageFlows(modelInstance, sbvrOutput);

        // Aufruf zur Analyse von Datenobjekten
        DataObjectAnalysis dataObjectAnalysis = new DataObjectAnalysis();
        dataObjectAnalysis.analyzeDataObjects(modelInstance, sbvrOutput);

        // Aufruf zur Analyse der IntermediateCatchEvents
        IntermediateCatchEventAnalyzer.analyzeCatchEvents(modelInstance, sbvrOutput);

        IntermediateThrowEventAnalyzer.analyzeThrowEvents(modelInstance, sbvrOutput);

        // Aufruf zur Analyse von SendTasks
        analyzeSendTasks(modelInstance, sbvrOutput);
        analyzeUserTask.analyzeUserTasks(modelInstance, sbvrOutput);
        analyzeServiceTasks(modelInstance, sbvrOutput);

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
            } else if (source instanceof EventBasedGateway eventBasedGateway) {
                EventBasedGatewayTransformer.EventGatewayTransformer((BpmnModelInstance) eventBasedGateway, sbvrOutput);
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


