package com.example.WorkflowPatterns;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.instance.*;

import java.io.File;
import java.util.*;

public class BpmnToGraph {

    public static void main(String[] args) {
        // Pfad zur BPMN-Datei
        String filePath = "C:\\Users\\pinki\\OneDrive\\Desktop\\Bachelor\\XML Dateien\\May_combine_ingredients.bpmn";

        // BPMN-Modell laden
        var modelInstance = Bpmn.readModelFromFile(new File(filePath));

        // Graph-Datenstruktur: Adjazenzliste (für Knotenbeziehungen)
        Map<String, List<String>> graph = new HashMap<>();
        // Map, die Sequenzflüsse mit ihren Bedingungen verknüpft
        Map<String, String> flowConditions = new HashMap<>();

        // Alle Sequence Flows durchgehen und Knoten/Kanten hinzufügen
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
        for (SequenceFlow flow : sequenceFlows) {
            String sourceId = flow.getSource().getId();
            String targetId = flow.getTarget().getId();

            // Bedingung des Sequenzflusses extrahieren, falls vorhanden
            String condition = flow.getConditionExpression() != null ? flow.getConditionExpression().getTextContent() : null;

            // Quelle und Ziel zu Adjazenzliste hinzufügen
            graph.putIfAbsent(sourceId, new ArrayList<>());
            graph.get(sourceId).add(targetId);

            // Wenn eine Bedingung existiert und das Ziel ein Gateway ist, speichern
            if (condition != null && isGateway(flow.getTarget())) {
                flowConditions.put(sourceId + "->" + targetId, condition);
            }
        }

        // Knoten mit Namen ausgeben (optional für lesbarere Ausgabe)
        Map<String, String> nodeNames = new HashMap<>();
        Collection<FlowNode> nodes = modelInstance.getModelElementsByType(FlowNode.class);
        for (FlowNode node : nodes) {
            nodeNames.put(node.getId(), node.getName() != null ? node.getName() : node.getId()); // Namen oder ID verwenden
        }

        // Graph im DOT-Format ausgeben
        System.out.println("Graph (DOT-Format):");
        System.out.println("digraph G {"); // Beginn des DOT-Formats

        // Knoten hinzufügen
        for (var entry : graph.entrySet()) {
            String sourceId = entry.getKey();
            String sourceName = nodeNames.get(sourceId);
            if (sourceName != null) {
                String nodeLabel = sourceName;
                String nodeType = getGatewayType(modelInstance.getModelElementById(sourceId));
                if (nodeType != null) {
                    nodeLabel += " (" + nodeType + ")";
                }
                System.out.println("  \"" + sourceName + "\" [label=\"" + nodeLabel + "\"];");
            }

            // Kanten hinzufügen
            for (String targetId : entry.getValue()) {
                String targetName = nodeNames.get(targetId);
                if (targetName != null) {
                    String edgeLabel = "";
                    String flowKey = sourceId + "->" + targetId;
                    if (flowConditions.containsKey(flowKey)) {
                        edgeLabel = " [label=\"" + flowConditions.get(flowKey) + "\"]";
                    }
                    System.out.println("  \"" + sourceName + "\" -> \"" + targetName + "\"" + edgeLabel + ";");
                }
            }
        }

        System.out.println("}"); // Ende des DOT-Formats
    }

    // Methode, um zu überprüfen, ob der Zielknoten ein Gateway ist
    private static boolean isGateway(FlowNode node) {
        return node instanceof Gateway;
    }

    // Methode, um den Typ des Gateways zu bestimmen
    private static String getGatewayType(FlowNode node) {
        if (node instanceof ExclusiveGateway) {
            return "Exclusive Gateway";
        } else if (node instanceof ParallelGateway) {
            return "Parallel Gateway";
        } else if (node instanceof InclusiveGateway) {
            return "Inclusive Gateway";
        } else if (node instanceof EventBasedGateway) {
            return "Event-Based Gateway";
        } else {
            return null;
        }
    }
}
