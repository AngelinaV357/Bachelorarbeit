package com.example;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.io.*;
import java.util.*;

import static com.example.SBVRTransformer.*;
import static com.example.SBVRTransformer.transformSequenceFlowToSBVR;

public class BPMNProcessor {
    // Liest die BPMN-Datei ein und gibt das BpmnModelInstance zurück
    static BpmnModelInstance readBpmnFile(File file) {
        System.out.println("BPMN-Datei wird eingelesen...");
        return Bpmn.readModelFromFile(file);
    }

    // Verarbeitet die Lanes und extrahiert die Rollen
    static void processLanes(BpmnModelInstance modelInstance, StringBuilder standardOutput, StringBuilder sbvrOutput) {
        Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);
        Set<String> uniqueRoles = new HashSet<>();

        for (Lane lane : lanes) {
            String roleName = lane.getName();
            if (roleName != null && !roleName.trim().isEmpty()) {
                uniqueRoles.add(roleName);
            }
        }

        for (String role : uniqueRoles) {
            standardOutput.append("Rolle: \"").append(role).append("\"\n");
            sbvrOutput.append(SBVRTransformer.transformRoleToSBVR(role)).append("\n");
            System.out.println("Rolle: " + role);
        }
    }

    // Verarbeitet die Sequenzflüsse
    static void processSequenceFlows(BpmnModelInstance modelInstance, StringBuilder standardOutput, StringBuilder sbvrOutput) {
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
        Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);

        for (SequenceFlow flow : sequenceFlows) {
            FlowNode source = flow.getSource();
            FlowNode target = flow.getTarget();
            String sourceRole = Hilfsmethoden.getRoleForNode(source, lanes);
            String targetRole = Hilfsmethoden.getRoleForNode(target, lanes);

            if (source instanceof StartEvent) {
                processStartEvent(source, target, standardOutput, sbvrOutput);
            } else if (source instanceof EndEvent) {
                processEndEvent(source, standardOutput, sbvrOutput);
            } else if (source instanceof ExclusiveGateway) {
                processXORGateway((ExclusiveGateway) source, standardOutput, sbvrOutput, sourceRole, lanes);
            } else if (source instanceof ParallelGateway) {
                processANDGateway((ParallelGateway) source, flow, lanes, standardOutput, sbvrOutput);
            } else if (source instanceof Activity) {
                processActivity(source, target, standardOutput, sbvrOutput);
            } else {
                processGenericFlow(source, target, sourceRole, targetRole, standardOutput, sbvrOutput);
            }
        }
    }

    // Verarbeitet StartEvents
    private static void processStartEvent(FlowNode source, FlowNode target, StringBuilder standardOutput, StringBuilder sbvrOutput) {
        String message = "\"Start\" ist mit \"" + Hilfsmethoden.getName(target) + "\" verbunden.\n";
        standardOutput.append(message);
        sbvrOutput.append(transformStartEventToSBVR((StartEvent) source)).append("\n");
        System.out.println(message);
    }

    // Verarbeitet EndEvents
    private static void processEndEvent(FlowNode source, StringBuilder standardOutput, StringBuilder sbvrOutput) {
        String message = "\"" + Hilfsmethoden.getName(source) + "\" ist mit \"Ende\" verbunden.\n";
        standardOutput.append(message);
        sbvrOutput.append(transformEndEventToSBVR((EndEvent) source)).append("\n");
        System.out.println(message);
    }

    // Verarbeitet XOR-Gateways
    private static void processXORGateway(ExclusiveGateway gateway, StringBuilder standardOutput, StringBuilder sbvrOutput, String sourceRole, Collection<Lane> lanes) {
        List<String> targetRoles = new ArrayList<>();
        List<SequenceFlow> outgoingFlows = new ArrayList<>(gateway.getOutgoing());

        for (SequenceFlow outgoingFlow : outgoingFlows) {
            targetRoles.add(Hilfsmethoden.getRoleForNode(outgoingFlow.getTarget(), lanes));
        }

        StringBuilder message = new StringBuilder("XOR-Gateway \"" + Hilfsmethoden.getName(gateway) + "\" hat folgende Bedingungen:\n");
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            message.append("- Bedingung: ").append(outgoingFlow.getName() != null ? outgoingFlow.getName() : "unbekannt").append("\n");
        }
        standardOutput.append(message);
        sbvrOutput.append(transformXORGatewayToSBVR(gateway, outgoingFlows, sourceRole, targetRoles, lanes, new HashSet<>())).append("\n");
        System.out.println(message);
    }

    // Verarbeitet AND-Gateways
    private static void processANDGateway(ParallelGateway gateway, SequenceFlow flow, Collection<Lane> lanes, StringBuilder standardOutput, StringBuilder sbvrOutput) {
        String message = "UND-Gateway: \"" + Hilfsmethoden.getName(gateway) + "\" ist mit \"" + Hilfsmethoden.getName(flow.getTarget()) + "\" verbunden.\n";
        standardOutput.append(message);
        sbvrOutput.append(transformANDGatewayToSBVR(gateway, Collections.singletonList(flow), new ArrayList<>(lanes))).append("\n");
        System.out.println(message);
    }

    // Verarbeitet allgemeine Aktivitäten
    private static void processActivity(FlowNode source, FlowNode target, StringBuilder standardOutput, StringBuilder sbvrOutput) {
        String message = "\"" + Hilfsmethoden.getName(source) + "\" ist mit \"" + Hilfsmethoden.getName(target) + "\" verbunden.\n";
        standardOutput.append(message);
        sbvrOutput.append(transformActivityToSBVR((Activity) source, new HashSet<>())).append("\n");
        System.out.println(message);
    }

    // Verarbeitet generische Verbindungen
    private static void processGenericFlow(FlowNode source, FlowNode target, String sourceRole, String targetRole, StringBuilder standardOutput, StringBuilder sbvrOutput) {
        String message = "\"" + Hilfsmethoden.getName(source) + "\" ist mit \"" + Hilfsmethoden.getName(target) + "\" verbunden.\n";
        standardOutput.append(message);
        sbvrOutput.append(transformSequenceFlowToSBVR(source, target, sourceRole, targetRole, new HashSet<>())).append("\n");
        System.out.println(message);
    }

    // Verarbeitet DataObjectReferenzen
    static void processDataObjects(BpmnModelInstance modelInstance, StringBuilder standardOutput, StringBuilder sbvrOutput) {
        Collection<DataObjectReference> dataObjectReferences = modelInstance.getModelElementsByType(DataObjectReference.class);
        for (DataObjectReference dataObjectReference : dataObjectReferences) {
            DataObject dataObject = dataObjectReference.getDataObject();
            if (dataObject != null) {
                String transformedDataObject = SBVRTransformer.transformDataObjectToSBVR(dataObject);
                standardOutput.append(transformedDataObject).append("\n");
                sbvrOutput.append(transformedDataObject).append("\n");
                System.out.println(transformedDataObject);
            }
        }
    }

    // Verarbeitet Startflows
    static void processStartFlows(BpmnModelInstance modelInstance, StringBuilder standardOutput) {
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
        List<SequenceFlow> startFlows = sequenceFlows.stream()
                .filter(flow -> flow.getSource() instanceof StartEvent)
                .toList();

        for (SequenceFlow flow : startFlows) {
            String startFlowInfo = "Start Flow: " + flow.getSource().getName() + " -> " + flow.getTarget().getName();
            standardOutput.append(startFlowInfo).append("\n");
            System.out.println(startFlowInfo);
        }
    }

    // Schreibt Text in eine Datei
    static void writeToFile(String content, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            System.out.println("In die Datei geschrieben: " + file.getPath());
        } catch (IOException e) {
            System.out.println("Fehler beim Schreiben der Datei: " + e.getMessage());
        }
    }
}
