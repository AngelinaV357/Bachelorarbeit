package com.example.Task;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.Collection;

public class SubProcessTransformer {

    public static void processSubProcessesAndMessageFlows(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Hole alle Subprozesse aus dem BPMN-Modell
        Collection<SubProcess> subProcesses = modelInstance.getModelElementsByType(SubProcess.class);

        // Iteriere durch alle gefundenen Subprozesse
        for (SubProcess subProcess : subProcesses) {
            String subProcessName = subProcess.getName() != null ? subProcess.getName() : "Unbekannter Subprozess";

            // Füge den Subprozess zur Ausgabe hinzu
            sbvrOutput.append("Subprozess erkannt: ").append(subProcessName).append("\n");

            // Ausgabe der enthaltenen FlowNodes
            Collection<FlowNode> flowNodes = subProcess.getChildElementsByType(FlowNode.class);
            sbvrOutput.append("  Enthält folgende FlowNodes:\n");
            for (FlowNode flowNode : flowNodes) {
                String flowNodeName = flowNode.getName() != null ? flowNode.getName() : "Unbekannter FlowNode";
                sbvrOutput.append("    - ").append(flowNodeName)
                        .append(" (Typ: ").append(flowNode.getElementType().getTypeName()).append(")").append("\n");
            }

            // Verarbeite Nachrichtenflüsse, die den Subprozess betreffen
            Collection<MessageFlow> messageFlows = modelInstance.getModelElementsByType(MessageFlow.class);
            sbvrOutput.append("  Zugehörige Nachrichtenflüsse:\n");
            for (MessageFlow messageFlow : messageFlows) {
                BaseElement source = (BaseElement) messageFlow.getSource();
                BaseElement target = (BaseElement) messageFlow.getTarget();

                // Prüfen, ob der Subprozess Quelle oder Ziel ist
                if (source.equals(subProcess) || target.equals(subProcess)) {
                    String sourceName = source instanceof FlowNode && ((FlowNode) source).getName() != null
                            ? ((FlowNode) source).getName()
                            : "Unbekannte Quelle";
                    String targetName = target instanceof FlowNode && ((FlowNode) target).getName() != null
                            ? ((FlowNode) target).getName()
                            : "Unbekanntes Ziel";

                    sbvrOutput.append("    - Nachrichtenfluss von ").append(sourceName)
                            .append(" nach ").append(targetName).append("\n");
                }
            }

            // Rekursives Verarbeiten von verschachtelten Subprozessen
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
