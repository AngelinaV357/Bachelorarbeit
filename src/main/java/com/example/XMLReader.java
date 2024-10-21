package com.example;

import java.io.File;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

public class XMLReader {
    public static void main(String[] args) {
        File file = new File("C:\\Users\\pinki\\OneDrive\\Desktop\\Bachelor\\XML Dateien\\May_combine_ingredients.bpmn");
        
        // Read the BPMN model instance from the file
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
        System.out.println("BPMN-Datei erfolgreich eingelesen!");

        // Find all sequence flows
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
        
        // Iterate through sequence flows and print connections
        for (SequenceFlow flow : sequenceFlows) {
            FlowNode source = flow.getSource();
            FlowNode target = flow.getTarget();
            if (source instanceof Activity && target instanceof Activity) {
                String sourceName = ((Activity) source).getName();
                String targetName = ((Activity) target).getName();
                System.out.println("\"" + sourceName + "\" ist mit \"" + targetName + "\" verbunden.");
            }
        }
    }
}
