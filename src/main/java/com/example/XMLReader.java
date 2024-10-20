package com.example;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;

public class XMLReader {

    public static void main(String[] args) {
        // BPMN-Modell von Datei lesen
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File("C:\\Users\\pinki\\OneDrive\\Desktop\\Bachelor\\XML Dateien\\Car Wash Process.bpmn"));

        // Start-Event finden
        StartEvent startEvent = (StartEvent) modelInstance.getModelElementById("start");
        if (startEvent != null) {
            System.out.println("Start Event ID: " + startEvent.getId());
            System.out.println("Start Event Name: " + startEvent.getName());

            // Attribute bearbeiten
            startEvent.setId("new-start-id");
            startEvent.setName("New Start Event Name");

            // Änderungen anzeigen
            System.out.println("Updated Start Event ID: " + startEvent.getId());
            System.out.println("Updated Start Event Name: " + startEvent.getName());
        }

        // Alle Aufgaben finden
        Collection<UserTask> userTasks = modelInstance.getModelElementsByType(UserTask.class);
        for (UserTask task : userTasks) {
            System.out.println("User Task ID: " + task.getId());
        }

        // Sequenzfluss finden und Quell- und Zielelemente abrufen
        SequenceFlow sequenceFlow = (SequenceFlow) modelInstance.getModelElementById("start-task1");
        if (sequenceFlow != null) {
            FlowNode source = sequenceFlow.getSource();
            FlowNode target = sequenceFlow.getTarget();
            System.out.println("Source ID: " + source.getId());
            System.out.println("Target ID: " + target.getId());

            // Alle ausgehenden Sequenzflüsse des Quellknotens abrufen
            Collection<SequenceFlow> outgoingFlows = source.getOutgoing();
            for (SequenceFlow flow : outgoingFlows) {
                System.out.println("Outgoing Flow ID: " + flow.getId());
            }
        }

        // Beispiel für die Verwendung einer Hilfsmethode
        Collection<FlowNode> followingNodes = getFlowingFlowNodes(startEvent);
        for (FlowNode node : followingNodes) {
            System.out.println("Following Node ID: " + node.getId());
        }
    }

    // Hilfsmethode, um die folgenden Flussknoten zu erhalten
    public static Collection<FlowNode> getFlowingFlowNodes(FlowNode node) {
        Collection<FlowNode> followingFlowNodes = new ArrayList<>();
        for (SequenceFlow sequenceFlow : node.getOutgoing()) {
            followingFlowNodes.add(sequenceFlow.getTarget());
        }
        return followingFlowNodes;
    }
}
