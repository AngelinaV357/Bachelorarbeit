package com.example.WorkflowPatterns;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.io.File;
import java.util.*;

public class BpmnModelParser {

    // Methode: BPMN-Datei einlesen und Graph erstellen
    public Map<String, List<String>> parseBpmnFile(File file) throws Exception {
        Map<String, List<String>> graph = new HashMap<>();

        // BPMN-Modell laden
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);

        // Alle Sequence Flows durchgehen und Kanten im Graphen speichern
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
        for (SequenceFlow flow : sequenceFlows) {
            String source = flow.getSource().getId();
            String target = flow.getTarget().getId();

            // Hinzufügen zur Adjazenzliste
            graph.putIfAbsent(source, new ArrayList<>());
            graph.get(source).add(target);
        }

        return graph;
    }

    public Map<String, String> getConditionsForFlows(File file) throws Exception {
        Map<String, String> conditions = new HashMap<>();
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);

        // Alle SequenceFlows durchsuchen, um Bedingungen zu extrahieren
        for (SequenceFlow flow : modelInstance.getModelElementsByType(SequenceFlow.class)) {
            String flowId = flow.getSource().getId() + "->" + flow.getTarget().getId();
            String condition = flow.getConditionExpression() != null ? flow.getConditionExpression().getTextContent() : null;
            if (condition != null) {
                conditions.put(flowId, condition);
            }
        }

        return conditions;
    }

    public Map<String, String> getNodeNames(File file) throws Exception {
        Map<String, String> nodeNames = new HashMap<>();
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);

        // Alle Knoten im BPMN-Modell durchlaufen
        for (FlowNode flowNode : modelInstance.getModelElementsByType(FlowNode.class)) {
            String id = flowNode.getId();
            String name = flowNode.getName();

            // Namen speichern; wenn kein Name vorhanden ist, die ID als Fallback verwenden
            nodeNames.put(id, name != null ? name : id);
        }

        return nodeNames;
    }

    public Map<String, String> getNodeTypes(File file) throws Exception {
        Map<String, String> nodeTypes = new HashMap<>();
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);

        // Alle Elemente des Modells durchgehen und Typen zuordnen
        for (BaseElement element : modelInstance.getModelElementsByType(BaseElement.class)) {
            nodeTypes.put(element.getId(), element.getElementType().getTypeName());
        }

        return nodeTypes;
    }

    // New method to get Lanes from the BPMN file
    public Collection<Lane> getLanes(File bpmnFile) {
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(bpmnFile);
        return modelInstance.getModelElementsByType(Lane.class);  // Get all lanes in the BPMN model
    }

    // Method to get the role for a specific FlowNode (activity)
    public static String getRoleForNode(String nodeId, Collection<Lane> lanes) {
        for (Lane lane : lanes) {
            if (lane.getFlowNodeRefs().stream().anyMatch(node -> node.getId().equals(nodeId))) {
                return lane.getName();  // Return the role (Lane name)
            }
        }
        return "Unbekannte Rolle";  // If no role is found
    }
}

