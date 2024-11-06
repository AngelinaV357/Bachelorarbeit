package com.example;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

public class TreeXMLReader {
    public static void main(String[] args) {
        File file = new File("src/main/resources/May_combine_ingredients.bpmn");

        try {
            // Einlesen der BPMN-Datei
            BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
            System.out.println("BPMN-Datei erfolgreich eingelesen!");

            // Alle Sequenzflüsse finden
            Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);

            // Map für die Baumstruktur
            Map<FlowNode, List<FlowNode>> nodeMap = new HashMap<>();

            // Eltern-Kind-Beziehungen aufbauen
            for (SequenceFlow flow : sequenceFlows) {
                FlowNode source = flow.getSource();
                FlowNode target = flow.getTarget();

                // Füge Zielknoten zur Quellknotenliste hinzu
                nodeMap.computeIfAbsent(source, k -> new java.util.ArrayList<>()).add(target);
            }

            // Startknoten finden und Baum ausgeben
            FlowNode startNode = modelInstance.getModelElementsByType(FlowNode.class).stream()
                    .filter(node -> node instanceof org.camunda.bpm.model.bpmn.instance.StartEvent)
                    .findFirst()
                    .orElse(null);

            if (startNode != null) {
                System.out.println("Baumstruktur des Prozesses:");
                printTree(startNode, nodeMap, " ");
            } else {
                System.out.println("Kein Startknoten gefunden.");
            }

        } catch (Exception e) {
            System.out.println("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    // Methode zum rekursiven Ausgeben der Baumstruktur
    private static void printTree(FlowNode node, Map<FlowNode, List<FlowNode>> nodeMap, String indent) {
        System.out.println(indent + getName(node));

        // Rekursiv alle Kinderknoten ausgeben
        List<FlowNode> children = nodeMap.get(node);
        if (children != null) {
            for (FlowNode child : children) {
                printTree(child, nodeMap, indent + "  ");
            }
        }
    }

    // Hilfsmethode zum Abrufen des Namens von Prozessknoten
    private static String getName(FlowNode node) {
        if (node instanceof org.camunda.bpm.model.bpmn.instance.Activity activity) {
            return activity.getName();
        } else if (node instanceof org.camunda.bpm.model.bpmn.instance.StartEvent) {
            return "Start";
        } else if (node instanceof org.camunda.bpm.model.bpmn.instance.EndEvent) {
            return "Ende";
        } else if (node instanceof org.camunda.bpm.model.bpmn.instance.ExclusiveGateway gateway) {
            return gateway.getName() != null ? gateway.getName() : "XOR-Gateway";
        }
        return "Unbekannt";
    }
}
