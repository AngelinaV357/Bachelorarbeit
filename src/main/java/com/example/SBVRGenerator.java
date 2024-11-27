package com.example;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import java.io.File;
import java.util.Map;

import static com.example.Hilfsmethoden.writeToFile;

public class SBVRGenerator {

    public static void main(String[] args) {
        File file = new File("src/main/resources/Employee Onboarding.bpmn");
        File sbvrOutputFile = new File("src/main/resources/sbvr_output.txt");

        try {
            BpmnModelInstance modelInstance = BPMNProcessor.readBpmnFile(file);
            StringBuilder standardOutput = new StringBuilder();
            StringBuilder sbvrOutput = new StringBuilder();

            BPMNProcessor.processSequenceFlows(modelInstance, standardOutput, sbvrOutput);

            //Map<String, String> participants = Hilfsmethoden.getParticipantName(modelInstance);
            //BPMNProcessor.processLanes(modelInstance, standardOutput, sbvrOutput);
            // Weitere Verarbeitung von DataObjectReferences und zusätzlichen Sequenzflüssen
            //BPMNProcessor.processDataObjects(modelInstance, standardOutput, sbvrOutput);
            //BPMNProcessor.processStartFlows(modelInstance, sbvrOutput);
            //BPMNProcessor.processBusinessRuleTasks(modelInstance, standardOutput, sbvrOutput);
            //BPMNProcessor.processSubProcesses(modelInstance, standardOutput, sbvrOutput);
            //BPMNProcessor.processAndTransformIntermediateThrowEvents(modelInstance, standardOutput, sbvrOutput);
            //BPMNProcessor.processIntermediateCatchEvents(modelInstance, standardOutput, sbvrOutput, participants);

            writeToFile(sbvrOutput.toString(), sbvrOutputFile);

        } catch (Exception e) {
            System.out.println("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
        }
    }
}