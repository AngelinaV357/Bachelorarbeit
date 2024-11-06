package com.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

public class XMLReader {
    public static void main(String[] args) {
        File file = new File("src/main/resources/Car Wash Process.bpmn");
        File outputFile = new File("src/main/resources/output.txt");

        try {
            BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
            System.out.println("BPMN-Datei erfolgreich eingelesen!");

            // Alle Knoten und Sequenzflüsse finden
            Collection<FlowNode> nodes = modelInstance.getModelElementsByType(FlowNode.class);
            Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);

            // Adjazenzmatrix erzeugen
            Map<String, Integer> nodeIndexMap = new HashMap<>(); // Mapping von Node-ID zu Index
            int index = 0;
            for (FlowNode node : nodes) {
                nodeIndexMap.put(node.getId(), index++);
            }

            int[][] adjacencyMatrix = new int[nodes.size()][nodes.size()]; // Adjazenzmatrix initialisieren

            // Matrix mit Sequenzflüssen füllen
            for (SequenceFlow flow : sequenceFlows) {
                FlowNode source = flow.getSource();
                FlowNode target = flow.getTarget();
                Integer sourceIndex = nodeIndexMap.get(source.getId());
                Integer targetIndex = nodeIndexMap.get(target.getId());

                if (sourceIndex != null && targetIndex != null) {
                    adjacencyMatrix[sourceIndex][targetIndex] = 1;
                }
            }

            // Ausgabe der Adjazenzmatrix
            writeAdjacencyMatrixToFile(adjacencyMatrix, nodes, nodeIndexMap, "src/main/resources/adjacency_matrix.txt");

            // Sequenzfluss-Informationen wie bisher ausgeben
            StringBuilder output = new StringBuilder();
            List<SequenceFlow> startFlows = sequenceFlows.stream()
                    .filter(flow -> flow.getSource() instanceof StartEvent)
                    .collect(Collectors.toList());

            for (SequenceFlow flow : startFlows) {
                FlowNode target = flow.getTarget();
                String targetName = getName(target);
                String message = "\"Start\" ist mit \"" + targetName + "\" verbunden.\n";
                output.append(message);
                System.out.println(message);
                classifyFlow("Start", targetName, "unknown");
            }

            for (SequenceFlow flow : sequenceFlows) {
                FlowNode source = flow.getSource();
                FlowNode target = flow.getTarget();

                if (source instanceof StartEvent) {
                    continue;
                }

                String sourceName = getName(source);
                String targetName = getName(target);
                String object = flow.getName() != null ? flow.getName() : "unknown";

                if (source instanceof ExclusiveGateway) {
                    String message = "XOR-Gateway: \"" + sourceName + "\" ist mit \"" + targetName + "\" verbunden. Bedingung: " + object + "\n";
                    output.append(message);
                    System.out.println(message);
                    classifyFlow(sourceName, targetName, object);
                } else if (target instanceof ExclusiveGateway) {
                    String message = "\"" + sourceName + "\" ist mit XOR-Gateway: \"" + targetName + "\" verbunden.\n";
                    output.append(message);
                    System.out.println(message);
                    classifyFlow(sourceName, targetName, "unknown");
                } else {
                    String message = "\"" + sourceName + "\" ist mit \"" + targetName + "\" verbunden.\n";
                    output.append(message);
                    System.out.println(message);
                    classifyFlow(sourceName, targetName, "unknown");
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write(output.toString());
                System.out.println("Die Ausgaben wurden erfolgreich in die Datei geschrieben: " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Fehler beim Schreiben in die Datei: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    private static String getName(FlowNode node) {
        if (node instanceof Activity activity) {
            return activity.getName() != null ? activity.getName() : "Unbekannte Aktivität";
        } else if (node instanceof StartEvent) {
            return "Start";
        } else if (node instanceof EndEvent) {
            return "Ende";
        } else if (node instanceof ExclusiveGateway gateway) {
            return gateway.getName() != null ? gateway.getName() : "XOR-Gateway";
        }
        return "Unbekannt";
    }

    // Hilfsmethode zum Schreiben der Adjazenzmatrix in eine Datei
    private static void writeAdjacencyMatrixToFile(int[][] matrix, Collection<FlowNode> nodes, Map<String, Integer> nodeIndexMap, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Adjacency Matrix:\n");
            List<String> nodeNames = nodes.stream().map(XMLReader::getName).collect(Collectors.toList());

            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    writer.write(matrix[i][j] + " ");
                }
                writer.newLine();
            }
            System.out.println("Die Adjazenzmatrix wurde erfolgreich in die Datei geschrieben: " + filePath);
        } catch (IOException e) {
            System.out.println("Fehler beim Schreiben der Adjazenzmatrix in die Datei: " + e.getMessage());
        }
    }

    // Hilfsmethode zur Klassifizierung von Actor, Action und Object
    private static void classifyFlow(String actor, String action, String object) {
        // Klare Ausgabe für Actor, Action und Object
        System.out.println("Actor: " + actor + ", Action: " + action + ", Object: " + object);
    }
}
