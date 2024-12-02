package com.example.Task;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.Collection;

import static com.example.Hilfsmethoden.getMessageFlowParticipantName;

public class SubProcessTransformer {

    public static void processSubProcessesAndMessageFlows(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        Collection<SubProcess> subProcesses = modelInstance.getModelElementsByType(SubProcess.class);
        Collection<MessageFlow> messageFlows = modelInstance.getModelElementsByType(MessageFlow.class);
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);

        for (SubProcess subProcess : subProcesses) {
            String subProcessName = subProcess.getName() != null ? subProcess.getName() : "Unbekannter Subprozess";

            // Iteriere durch alle Nachrichtenflüsse
            for (MessageFlow messageFlow : messageFlows) {
                BaseElement source = (BaseElement) messageFlow.getSource();
                BaseElement target = (BaseElement) messageFlow.getTarget();

                // Name der Nachricht (falls vorhanden)
                String messageName = messageFlow.getName() != null ? messageFlow.getName() : "Nachricht";

                // Wenn der Subprozess die Quelle ist
                if (source.equals(subProcess)) {
                    String targetName = getMessageFlowParticipantName(target, participants);
                    sbvrOutput.append("„Es ist notwendig, dass ")
                            .append(subProcessName)
                            .append(" die Nachricht [").append(messageName).append("] an ")
                            .append(targetName)
                            .append(" sendet.“\n");
                }

                // Wenn der Subprozess das Ziel ist
                if (target.equals(subProcess)) {
                    String sourceName = getMessageFlowParticipantName(source, participants);
                    sbvrOutput.append("„Es ist notwendig, dass ")
                            .append(subProcessName)
                            .append(" die Nachricht [").append(messageName).append("] vom ")
                            .append(sourceName)
                            .append(" empfängt.“\n");
                }
            }
        }
    }
    public static void processSubProcessesForFlowNode(SubProcess subProcess, StringBuilder sbvrOutput, Collection<Lane> lanes) {
        sbvrOutput.append("Subprozess erkannt: ").append(subProcess.getName() != null ? subProcess.getName() : "Unbenannter Subprozess");
//                .append(" (ID: ").append(subProcess.getId()).append(")").append("\n");

        // Hole die enthaltenen FlowNodes des Subprozesses
//        Collection<FlowNode> flowNodes = subProcess.getChildElementsByType(FlowNode.class);
//        sbvrOutput.append("  Enthält folgende FlowNodes:\n");
    }
//
//    private static void processNestedSubProcesses(SubProcess parentSubProcess, StringBuilder sbvrOutput) {
//        Collection<SubProcess> nestedSubProcesses = parentSubProcess.getChildElementsByType(SubProcess.class);
//        for (SubProcess nestedSubProcess : nestedSubProcesses) {
//            String nestedName = nestedSubProcess.getName() != null ? nestedSubProcess.getName() : "Unbekannter Subprozess";
//            sbvrOutput.append("  Verschachtelter Subprozess: ").append(nestedName).append("\n");
//            processNestedSubProcesses(nestedSubProcess, sbvrOutput); // Rekursion für weitere verschachtelte Subprozesse
//        }
//    }
//
//    private static String getMessageFlowName(BaseElement element, Collection<Participant> participants) {
//        if (element instanceof FlowNode flowNode) {
//            return flowNode.getName() != null ? flowNode.getName() : "Unbekannter FlowNode";
//        } else if (element instanceof Participant participant) {
//            return participant.getName() != null ? participant.getName() : "Unbekannter Teilnehmer";
//        } else {
//            return "Unbekannte Quelle/Ziel";
//        }
//    }

}
