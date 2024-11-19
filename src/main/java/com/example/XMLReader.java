package com.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

public class XMLReader {
    public static void main(String[] args) {
        File file = new File("src/main/resources/Car Wash Process.bpmn");
        File outputFile = new File("src/main/resources/output.txt");
        File sbvr_output = new File("src/main/resources/sbvr_output.txt");

        try {
            BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
            System.out.println("BPMN-Datei erfolgreich eingelesen!");

            Collection<FlowNode> nodes = modelInstance.getModelElementsByType(FlowNode.class);
            Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
            Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);  // Lanes hinzufügen

            // Output-Builder für Standard- und SBVR-Ausgaben
            StringBuilder output = new StringBuilder();
            StringBuilder sbvrOutput = new StringBuilder();

            /// Verarbeitung der Lanes und Extraktion der Rollen
            Set<String> uniqueRoles = new HashSet<>();  // Set für einzigartige Rollen

            for (Lane lane : lanes) {
                String roleName = lane.getName();  // Name der Lane (z. B. "Cashier")
                if (roleName != null && !roleName.trim().isEmpty()) {
                    uniqueRoles.add(roleName);  // Rolle zum Set hinzufügen
                }
            }

            // Ausgabe der einzigartigen Rollen
            for (String role : uniqueRoles) {
                output.append("Rolle: \"").append(role).append("\"\n");
                sbvrOutput.append(SBVRTransformer.transformRoleToSBVR(role)).append("\n");  // Für die SBVR-Transformation
                System.out.println("Rolle: " + role);
            }

            // Verarbeitung der Sequenzflüsse
            for (SequenceFlow flow : sequenceFlows) {
                FlowNode source = flow.getSource();
                FlowNode target = flow.getTarget();

                if (source instanceof StartEvent) {
                    String message = "\"Start\" ist mit \"" + getName(target) + "\" verbunden.\n";
                    output.append(message);
                    sbvrOutput.append(SBVRTransformer.transformStartEventToSBVR((StartEvent) source)).append("\n");
                    System.out.println(message);
                } else if (source instanceof EndEvent) {
                    String message = "\"" + getName(source) + "\" ist mit \"Ende\" verbunden.\n";
                    output.append(message);
                    sbvrOutput.append(SBVRTransformer.transformEndEventToSBVR((EndEvent) source)).append("\n");
                    System.out.println(message);
                } else if (source instanceof ExclusiveGateway) {
                    String message = "XOR-Gateway: \"" + getName(source) + "\" ist mit \"" + getName(target)
                            + "\" verbunden. Bedingung: " + (flow.getName() != null ? flow.getName() : "unbekannt") + "\n";
                    output.append(message);
                    sbvrOutput.append(SBVRTransformer.transformXORGatewayToSBVR((ExclusiveGateway) source,
                                    Collections.singletonList(flow)))
                            .append("\n");
                    System.out.println(message);
                } else if (target instanceof ExclusiveGateway) {
                    String message = "\"" + getName(source) + "\" ist mit XOR-Gateway: \"" + getName(target) + "\" verbunden.\n";
                    output.append(message);
                    sbvrOutput.append(SBVRTransformer.transformSequenceFlowToSBVR(source, target)).append("\n");
                    System.out.println(message);
                } else if (source instanceof ParallelGateway) {
                    String message = "UND-Gateway: \"" + getName(source) + "\" ist mit \"" + getName(target) + "\" verbunden.\n";
                    output.append(message);
                    sbvrOutput.append(SBVRTransformer.transformANDGatewayToSBVR((ParallelGateway) source,
                                    Collections.singletonList(flow)))
                            .append("\n");
                    System.out.println(message);
                } else {
                    String message = "\"" + getName(source) + "\" ist mit \"" + getName(target) + "\" verbunden.\n";
                    output.append(message);
                    sbvrOutput.append(SBVRTransformer.transformSequenceFlowToSBVR(source, target)).append("\n");
                    System.out.println(message);
                }
            }

            // Ergebnisse in Dateien schreiben
            writeToFile(output.toString(), outputFile, "Standard-Ausgaben");
            writeToFile(sbvrOutput.toString(), sbvr_output, "SBVR-Ausgaben");

            // Sequenzfluss-Informationen wie in der ersten Datei ausgeben
            StringBuilder additionalOutput = new StringBuilder();
            List<SequenceFlow> startFlows = sequenceFlows.stream()
                    .filter(flow -> flow.getSource() instanceof StartEvent)
                    .collect(Collectors.toList());

            for (SequenceFlow flow : startFlows) {
                FlowNode target = flow.getTarget();
                String targetName = getName(target);
                String message = "\"Start\" ist mit \"" + targetName + "\" verbunden.\n";
                additionalOutput.append(message);
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
                    additionalOutput.append(message);
                    System.out.println(message);
                    classifyFlow(sourceName, targetName, object);
                } else if (target instanceof ExclusiveGateway) {
                    String message = "\"" + sourceName + "\" ist mit XOR-Gateway: \"" + targetName + "\" verbunden.\n";
                    additionalOutput.append(message);
                    System.out.println(message);
                    classifyFlow(sourceName, targetName, "unknown");
                } else {
                    String message = "\"" + sourceName + "\" ist mit \"" + targetName + "\" verbunden.\n";
                    additionalOutput.append(message);
                    System.out.println(message);
                    classifyFlow(sourceName, targetName, "unknown");
                }
            }

            // Zusätzliches Ergebnis in die Datei schreiben
            writeToFile(additionalOutput.toString(), outputFile, "Zusätzliche Standard-Ausgaben");

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

    private static void writeToFile(String content, File file, String type) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
            System.out.println(type + " wurden erfolgreich in die Datei geschrieben: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Fehler beim Schreiben in die Datei: " + e.getMessage());
        }
    }

    // Hilfsmethode zur Klassifizierung von Actor, Action und Object
    private static void classifyFlow(String actor, String action, String object) {
        // Klare Ausgabe für Actor, Action und Object
        System.out.println("Actor: " + actor + ", Action: " + action + ", Object: " + object);
    }
}
