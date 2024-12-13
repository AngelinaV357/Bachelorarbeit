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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class BpmnModelParser {

    // Konvertiert eine BPMN-Datei in einen gerichteten Graphen
    public static Graph<String, DefaultEdge> convertBpmnToGraph(File bpmnFile) throws Exception {
        // BPMN-Datei laden
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(bpmnFile);

        // Graph initialisieren
        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        // Alle FlowNodes hinzufügen
        Collection<FlowNode> flowNodes = modelInstance.getModelElementsByType(FlowNode.class);
        for (FlowNode node : flowNodes) {
            String nodeId = node.getId();
            String nodeName = node.getName() != null ? node.getName() : nodeId;
            graph.addVertex(nodeName);
        }

        // Alle Sequence Flows hinzufügen
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
        for (SequenceFlow flow : sequenceFlows) {
            String sourceName = flow.getSource().getName() != null ? flow.getSource().getName() : flow.getSource().getId();
            String targetName = flow.getTarget().getName() != null ? flow.getTarget().getName() : flow.getTarget().getId();
            graph.addEdge(sourceName, targetName);
        }

        return graph;
    }

    // Exportiert den Graphen in das DOT-Format
    public static String exportGraphToDot(Graph<String, DefaultEdge> graph) {
        // DOTExporter initialisieren
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>(v -> "\"" + v + "\"");

        // Setzen von Attributen für die Knoten
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            return map;
        });

        // Exportieren des Graphen in einen String
        StringWriter writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        return writer.toString();
    }


    public static void main(String[] args) throws Exception {
        // BPMN-Datei einlesen
        File bpmnFile = new File("src/main/resources/May_combine_ingredients.bpmn");

        // Konvertierung durchführen
        Graph<String, DefaultEdge> bpmnGraph = convertBpmnToGraph(bpmnFile);

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
