package com.example;

import java.io.File;
import java.util.Collection;

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
        File file = new File("C:\\Users\\pinki\\OneDrive\\Desktop\\Bachelor\\XML Dateien\\May_combine_ingredients.bpmn");

        //Einlesen der Datei mit Ausnahme, wenn es nicht korrekt eingelesen werden konnte
        try {
            // Read the BPMN model instance from the file
            BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
            System.out.println("BPMN-Datei erfolgreich eingelesen!");

            // Find all sequence flows
            Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
            
            // Iterate through sequence flows and print connections
            for (SequenceFlow flow : sequenceFlows) {
                FlowNode source = flow.getSource();
                FlowNode target = flow.getTarget();
                
                String sourceName = getName(source);
                String targetName = getName(target);
                
                // Ausgabe
                if (source instanceof ExclusiveGateway) {
                    System.out.println("XOR: \"" + sourceName + "\" ist mit \"" + targetName + "\" verbunden.");
                } else {
                    System.out.println("\"" + sourceName + "\" ist mit \"" + targetName + "\" verbunden.");
                }
            }
        } catch (Exception e) {
            System.out.println("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }

    // Helper method to get the name of flow nodes
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
}
