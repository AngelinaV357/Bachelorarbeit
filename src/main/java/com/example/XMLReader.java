package com.example;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

public class XMLReader {
    public static void main(String[] args) {
        File file = new File("C:\\Users\\pinki\\OneDrive\\Desktop\\Bachelor\\XML Dateien\\May_combine_ingredients.bpmn");

        try {
            BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
            System.out.println("BPMN-Datei erfolgreich eingelesen!");

            Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);

            // Verbindungen vom Start-Event ausgeben
            List<SequenceFlow> startFlows = sequenceFlows.stream()
                    .filter(flow -> flow.getSource() instanceof StartEvent)
                    .collect(Collectors.toList());

            for (SequenceFlow flow : startFlows) {
                FlowNode target = flow.getTarget();
                String targetName = getName(target);
                System.out.println("Start ist mit \"" + targetName + "\" verbunden.");
            }

            // Andere Sequenzflüsse ausgeben
            for (SequenceFlow flow : sequenceFlows) {
                FlowNode source = flow.getSource();
                FlowNode target = flow.getTarget();

                if (source instanceof StartEvent) {
                    continue;
                }

                String sourceName = getName(source);
                String targetName = getName(target);

                if (source instanceof ExclusiveGateway exclusiveGateway) {
                    // Bestimme die tatsächliche Bedingung aus der XML-Datei
                    String condition = getCondition(flow);

                    // Wenn die Bedingung spezifisch "JA" oder "NEIN" ist, dann gib dies aus
                    if (condition.toLowerCase().contains("yes") || condition.toLowerCase().contains("ja")) {
                        System.out.println("Exklusives Gateway \"" + sourceName + "\" (JA) ist mit \"" + targetName + "\" verbunden.");
                    } else if (condition.toLowerCase().contains("no") || condition.toLowerCase().contains("nein")) {
                        System.out.println("Exklusives Gateway \"" + sourceName + "\" (NEIN) ist mit \"" + targetName + "\" verbunden.");
                    } else {
                        // Wenn keine klare "JA" oder "NEIN" Bedingung vorliegt, gib die Bedingung direkt aus
                        System.out.println("Exklusives Gateway \"" + sourceName + "\" (" + condition + ") ist mit \"" + targetName + "\" verbunden.");
                    }
                } else {
                    System.out.println("\"" + sourceName + "\" ist mit \"" + targetName + "\" verbunden.");
                }
            }
        } catch (Exception e) {
            System.out.println("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    // Hilfsmethode zum Abrufen des Namens von Flussknoten
    private static String getName(FlowNode node) {
        if (node instanceof Activity activity) {
            return activity.getName();
        } else if (node instanceof StartEvent) {
            return "Start";
        } else if (node instanceof EndEvent) {
            return "Ende";
        } else if (node instanceof ExclusiveGateway) {
            return node.getName() != null && !node.getName().isEmpty() ? node.getName() : "Unbekanntes XOR-Gateway";
        }
        return "Unbekannt"; // Für andere Knoten, die keine Aktivität, Start- oder End-Ereignisse sind
    }

    // Hilfsmethode zum Abrufen der Bedingung für einen Sequenzfluss
    private static String getCondition(SequenceFlow flow) {
        ConditionExpression condition = flow.getConditionExpression();
        if (condition != null) {
            return condition.getTextContent(); // Rückgabe des Textinhalts der Bedingung
        }
        return "keine Bedingung"; // Falls keine Bedingung vorhanden ist
    }
}
