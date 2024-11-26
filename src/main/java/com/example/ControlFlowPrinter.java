package com.example;

import java.io.File;
import java.util.*;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import static com.example.Hilfsmethoden.getCondition;
import static com.example.Hilfsmethoden.getName;

public class ControlFlowPrinter {

    public static void main(String[] args) {
        File file = new File("src/main/resources/Employee Onboarding.bpmn");
        File outputFile = new File("src/main/resources/output.txt");

        try {
            // Datei für das BPMN-Modell festlegen
            BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
            System.out.println("BPMN-Datei erfolgreich eingelesen!");

            // Alle Sequenzflüsse finden
            Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);

            // Zuerst die Verbindungen vom Start-Event ausgeben
            List<SequenceFlow> startFlows = sequenceFlows.stream()
                    .filter(flow -> flow.getSource() instanceof StartEvent)
                    .toList();

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
}
