package com.example.WorkflowPatterns;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class BpmnModelParser {

    // Custom Edge class to hold labels
    public static class LabeledEdge extends DefaultEdge {
        private final String label;

        public LabeledEdge(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    // Counter to ensure unique node labels
    private static final Map<String, Integer> nameCounts = new HashMap<>();

    private static String ensureUniqueName(String name) {
        if (!nameCounts.containsKey(name)) {
            nameCounts.put(name, 0);
            return name;
        } else {
            int count = nameCounts.get(name) + 1;
            nameCounts.put(name, count);
            return name + " (" + count + ")";
        }
    }

    // Utility to clean and ensure unique names
    private static String getNodeLabel(FlowNode node) {
        String name = node.getName();

        // Clean name
        if (name != null && !name.isEmpty()) {
            name = name.replace("\n", " ").trim();
        } else if (node instanceof ParallelGateway) {
            name = "ParallelGateway";
        } else if (node instanceof InclusiveGateway) {
            name = "InclusiveGateway";
        } else if (node instanceof ExclusiveGateway) {
            name = "ExclusiveGateway";
        } else if (node instanceof EndEvent) {
            name = "EndEvent";
        } else if (node instanceof StartEvent) {
            name = "StartEvent";
        } else {
            name = "Unnamed_Node";
        }

        // Ensure the name is unique
        return ensureUniqueName(name);
    }

    // Konvertiert eine BPMN-Datei in einen gerichteten Graphen
    public static Graph<String, LabeledEdge> convertBpmnToGraph(File bpmnFile) throws Exception {
        // BPMN-Datei laden
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(bpmnFile);

        // Graph initialisieren
        Graph<String, LabeledEdge> graph = new DefaultDirectedGraph<>(LabeledEdge.class);

        // Alle FlowNodes hinzufügen
        Collection<FlowNode> flowNodes = modelInstance.getModelElementsByType(FlowNode.class);
        for (FlowNode node : flowNodes) {
            String nodeLabel = getNodeLabel(node);
            graph.addVertex(nodeLabel);
        }

        // Alle Sequence Flows hinzufügen
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
        for (SequenceFlow flow : sequenceFlows) {
            String sourceLabel = getNodeLabel(flow.getSource());
            String targetLabel = getNodeLabel(flow.getTarget());

            String edgeLabel = flow.getName() != null && !flow.getName().isEmpty() ? flow.getName() : "";
            graph.addEdge(sourceLabel, targetLabel, new LabeledEdge(edgeLabel));
        }

        return graph;
    }

    // Exportiert den Graphen in das DOT-Format
    public static String exportGraphToDot(Graph<String, LabeledEdge> graph) {
        // DOTExporter initialisieren
        DOTExporter<String, LabeledEdge> exporter = new DOTExporter<>(v -> "\"" + v.replace("\n", " ") + "\"");

        // Setzen von Attributen für die Knoten
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v));
            return map;
        });

        // Setzen von Attributen für die Kanten
        exporter.setEdgeAttributeProvider((e) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(e.getLabel()));
            return map;
        });

        // Exportieren des Graphen in einen String
        StringWriter writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        return writer.toString();
    }

    public static void main(String[] args) throws Exception {
        // BPMN-Datei einlesen
        File bpmnFile = new File("src/main/resources/May_combinr_ingred.bpmn");

        // Konvertierung durchführen
        Graph<String, LabeledEdge> bpmnGraph = convertBpmnToGraph(bpmnFile);

        // Graph anzeigen
        System.out.println("Graph-Knoten: " + bpmnGraph.vertexSet());
        System.out.println("Graph-Kanten: " + bpmnGraph.edgeSet());

        // Graph exportieren und in DOT-Format ausgeben
        String dotRepresentation = exportGraphToDot(bpmnGraph);
        System.out.println("Graph im DOT-Format:\n" + dotRepresentation);

        // Optional: Speichern der DOT-Repräsentation in einer Datei
        try (FileWriter fileWriter = new FileWriter("bpmnGraph.dot")) {
            fileWriter.write(dotRepresentation);
            System.out.println("DOT-Datei wurde gespeichert: bpmnGraph.dot");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
