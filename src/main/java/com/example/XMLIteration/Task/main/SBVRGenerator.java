package com.example.XMLIteration.Task.main;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import java.io.File;

import static com.example.XMLIteration.Task.Hilfsmethoden.writeToFile;

public class SBVRGenerator {

    public static void main(String[] args) {
        File file = new File("src/main/resources/May_combine_ingredients.bpmn");
        File sbvrOutputFile = new File("src/main/resources/sbvr_output.txt");

        try {
            BpmnModelInstance modelInstance = BPMNProcessor.readBpmnFile(file);
            StringBuilder sbvrOutput = new StringBuilder();

            BPMNProcessor.processSequenceFlows(modelInstance, sbvrOutput);

            writeToFile(sbvrOutput.toString(), sbvrOutputFile);

        } catch (Exception e) {
            System.out.println("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }
}