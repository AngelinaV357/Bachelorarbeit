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
        } else if (node instanceof EventBasedGateway) {
            name = "EventBasedGateway";
        } else if (node instanceof IntermediateCatchEvent) {
            name = "IntermediateCatchEvent";
        } else if (node instanceof IntermediateThrowEvent) {
            name = "IntermediateThrowEvent";
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

    // Converts a BPMN file to a directed graph
    public static Graph<String, LabeledEdge> convertBpmnToGraph(File bpmnFile) throws Exception {
        // Load BPMN file
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(bpmnFile);

        // Initialize graph
        Graph<String, LabeledEdge> graph = new DefaultDirectedGraph<>(LabeledEdge.class);

        // Add all FlowNodes as vertices
        Collection<FlowNode> flowNodes = modelInstance.getModelElementsByType(FlowNode.class);
        Map<FlowNode, String> nodeLabels = new HashMap<>(); // Map to store FlowNode to label mapping

        for (FlowNode node : flowNodes) {
            String nodeLabel = getNodeLabel(node);
            graph.addVertex(nodeLabel);
            nodeLabels.put(node, nodeLabel);
        }

        // Add all Sequence Flows as edges
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
        for (SequenceFlow flow : sequenceFlows) {
            FlowNode sourceNode = flow.getSource();
            FlowNode targetNode = flow.getTarget();

            String sourceLabel = nodeLabels.get(sourceNode);
            String targetLabel = nodeLabels.get(targetNode);

            if (sourceLabel == null || targetLabel == null) {
                System.err.println("Warning: Missing vertex for source or target in sequence flow: " + flow.getId());
                continue;
            }

            String edgeLabel = (flow.getName() != null && !flow.getName().isEmpty()) ? flow.getName() : "";
            graph.addEdge(sourceLabel, targetLabel, new LabeledEdge(edgeLabel));
        }

        return graph;
    }


    // Exports the graph to DOT format
    public static String exportGraphToDot(Graph<String, LabeledEdge> graph) {
        // Initialize DOTExporter
        DOTExporter<String, LabeledEdge> exporter = new DOTExporter<>(v -> "\"" + v.replace("\n", " ") + "\"");

        // Set attributes for vertices
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v));
            return map;
        });

        // Set attributes for edges
        exporter.setEdgeAttributeProvider((e) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(e.getLabel()));
            return map;
        });

        // Export graph to a string
        StringWriter writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        return writer.toString();
    }

    public static void main(String[] args) {
        try {
            // Load BPMN file
            File bpmnFile = new File("src/main/resources/Receipt of Good.bpmn");

            // Convert to graph
            Graph<String, LabeledEdge> bpmnGraph = convertBpmnToGraph(bpmnFile);

            // Display graph details
            System.out.println("Graph Nodes: " + bpmnGraph.vertexSet());
            System.out.println("Graph Edges: " + bpmnGraph.edgeSet());

            // Export graph to DOT format and print
            String dotRepresentation = exportGraphToDot(bpmnGraph);
            System.out.println("Graph in DOT format:\n" + dotRepresentation);

            // Optional: Save DOT representation to a file
            try (FileWriter fileWriter = new FileWriter("bpmnGraph.dot")) {
                fileWriter.write(dotRepresentation);
                System.out.println("DOT file saved: bpmnGraph.dot");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
