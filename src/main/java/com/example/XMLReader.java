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

        // Einlesen der Datei mit Ausnahme, wenn es nicht korrekt eingelesen werden konnte
        try {
            // Datei für das BPMN-Modell festlegen
            BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
            System.out.println("BPMN-Datei erfolgreich eingelesen!");

            // Alle Sequenzflüsse finden
            Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
            
            // Zuerst die Verbindungen vom Start-Event ausgeben
            List<SequenceFlow> startFlows = sequenceFlows.stream()
                .filter(flow -> flow.getSource() instanceof StartEvent)
                .collect(Collectors.toList());

            for (SequenceFlow flow : startFlows) {
                FlowNode target = flow.getTarget();
                String targetName = getName(target);
                System.out.println("Start ist mit \"" + targetName + "\" verbunden.");
            }

            // Dann alle anderen Sequenzflüsse ausgeben
            for (SequenceFlow flow : sequenceFlows) {
                FlowNode source = flow.getSource();
                FlowNode target = flow.getTarget();
                
                // Überspringe Verbindungen vom Start-Event, die bereits ausgegeben wurden
                if (source instanceof StartEvent) {
                    continue;
                }
                
                String sourceName = getName(source);
                String targetName = getName(target);
                
                // Ausgabe der Verbindungen
                if (source instanceof ExclusiveGateway) {
                    String condition = getCondition(flow);
                    System.out.println("XOR: \"" + sourceName + "\" (" + condition + ") ist mit \"" + targetName + "\" verbunden.");
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
            return "XOR-Gateway"; 
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