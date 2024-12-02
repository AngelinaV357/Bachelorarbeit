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
                String messageName = messageFlow.getName() != null ? messageFlow.getName() : "Unbekannte Nachricht";

                // Wenn der Subprozess die Quelle ist
                if (source.equals(subProcess)) {
                    String targetName = getMessageFlowParticipantName(target, participants);
                    sbvrOutput.append("„Es ist notwendig, dass der Subprozess ")
                            .append(subProcessName)
                            .append(" die Nachricht [").append(messageName).append("] an ")
                            .append(targetName)
                            .append(" sendet.“\n");
                }

                // Wenn der Subprozess das Ziel ist
                if (target.equals(subProcess)) {
                    String sourceName = getMessageFlowParticipantName(source, participants);
                    sbvrOutput.append("„Es ist notwendig, dass der Subprozess ")
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
    }
}
