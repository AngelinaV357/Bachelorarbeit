package com.example.Task;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.Collection;

public class SubProcessTransformer {

    public static void processSubProcessesAndMessageFlows(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Hole alle Subprozesse, Nachrichtenflüsse und Teilnehmer aus dem Modell
        Collection<SubProcess> subProcesses = modelInstance.getModelElementsByType(SubProcess.class);
        Collection<MessageFlow> messageFlows = modelInstance.getModelElementsByType(MessageFlow.class);
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);

        // Iteriere durch alle Subprozesse
        for (SubProcess subProcess : subProcesses) {
            // Name des Subprozesses (mit Fallback)
            String subProcessName = subProcess.getName() != null ? subProcess.getName() : "Unbekannter Subprozess";

            // Füge Subprozess-Informationen zur Ausgabe hinzu
            sbvrOutput.append("Subprozess erkannt: ").append(subProcessName).append("\n");

            // Verarbeitung der enthaltenen FlowNodes
            sbvrOutput.append("  Enthält folgende FlowNodes:\n");
            Collection<FlowNode> flowNodes = subProcess.getChildElementsByType(FlowNode.class);
            for (FlowNode flowNode : flowNodes) {
                String flowNodeName = flowNode.getName() != null ? flowNode.getName() : "Unbekannter FlowNode";
                sbvrOutput.append("    - ").append(flowNodeName)
                        .append(" (Typ: ").append(flowNode.getElementType().getTypeName()).append(")").append("\n");
            }

            // Verarbeitung der Nachrichtenflüsse, die den Subprozess betreffen
            sbvrOutput.append("  Zugehörige Nachrichtenflüsse:\n");
            for (MessageFlow messageFlow : messageFlows) {
                BaseElement source = (BaseElement) messageFlow.getSource();
                BaseElement target = (BaseElement) messageFlow.getTarget();

                // Prüfen, ob der Subprozess Quelle oder Ziel des Nachrichtenflusses ist
                if (subProcess.equals(source) || subProcess.equals(target)) {
                    String sourceName = getMessageFlowName(source, participants);
                    String targetName = getMessageFlowName(target, participants);

                    sbvrOutput.append("    - Nachrichtenfluss von ").append(sourceName)
                            .append(" nach ").append(targetName).append("\n");
                }
            }

            // Rekursive Verarbeitung verschachtelter Subprozesse
            processNestedSubProcesses(subProcess, sbvrOutput);
        }
    }

    public static void processSubProcessesForFlowNode(SubProcess subProcess, StringBuilder sbvrOutput, Collection<Lane> lanes) {
        sbvrOutput.append("Subprozess erkannt: ").append(subProcess.getName() != null ? subProcess.getName() : "Unbenannter Subprozess");
//                .append(" (ID: ").append(subProcess.getId()).append(")").append("\n");

        // Hole die enthaltenen FlowNodes des Subprozesses
//        Collection<FlowNode> flowNodes = subProcess.getChildElementsByType(FlowNode.class);
//        sbvrOutput.append("  Enthält folgende FlowNodes:\n");
    }

    private static void processNestedSubProcesses(SubProcess parentSubProcess, StringBuilder sbvrOutput) {
        Collection<SubProcess> nestedSubProcesses = parentSubProcess.getChildElementsByType(SubProcess.class);
        for (SubProcess nestedSubProcess : nestedSubProcesses) {
            String nestedName = nestedSubProcess.getName() != null ? nestedSubProcess.getName() : "Unbekannter Subprozess";
            sbvrOutput.append("  Verschachtelter Subprozess: ").append(nestedName).append("\n");
            processNestedSubProcesses(nestedSubProcess, sbvrOutput); // Rekursion für weitere verschachtelte Subprozesse
        }
    }

    private static String getMessageFlowName(BaseElement element, Collection<Participant> participants) {
        if (element instanceof FlowNode flowNode) {
            return flowNode.getName() != null ? flowNode.getName() : "Unbekannter FlowNode";
        } else if (element instanceof Participant participant) {
            return participant.getName() != null ? participant.getName() : "Unbekannter Teilnehmer";
        } else {
            return "Unbekannte Quelle/Ziel";
        }
    }

    private static String getMessageFlowParticipantName(BaseElement element, Collection<Participant> participants) {
        // Prüfe, ob das Element ein Participant ist
        if (element instanceof Participant participant) {
            return participant.getName() != null ? participant.getName() : "Unbenannter Teilnehmer";
        }

        // Falls nicht, überprüfe die zugehörigen Teilnehmer anhand der Prozesse
        for (Participant participant : participants) {
            if (participant.getProcess() != null && participant.getProcess().equals(element)) {
                return participant.getName() != null ? participant.getName() : "Unbenannter Teilnehmer";
            }
        }

        return "Unbekannte Quelle/Ziel";
    }
}
