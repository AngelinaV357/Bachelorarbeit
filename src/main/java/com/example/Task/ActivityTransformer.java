package com.example.Task;

import com.example.Interfaces.FlowNodeTransformer;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.*;

import static com.example.Hilfsmethoden.getName;
import static com.example.Hilfsmethoden.getRoleForNode;

public class ActivityTransformer implements FlowNodeTransformer {

    @Override
    public String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes) {
        StringBuilder sbvrOutput = new StringBuilder();

        // Stelle sicher, dass der Knoten eine Aufgabe (Aktivität) oder Subprozess ist
        if (node instanceof Task task) {
            String activityName = getName(task);
            String sourceActivity = null;
            String sourceActivityRole = null;
            String targetActivityRole = getRoleForNode(task, lanes);

            // Überprüfen, ob der Vorgänger ein XOR Gateway ist
            if (!task.getIncoming().isEmpty()) {
                FlowNode incomingNode = task.getIncoming().iterator().next().getSource();
                if (incomingNode instanceof ExclusiveGateway xorGateway) {
                    String gatewayName = getName(xorGateway);
                    sourceActivityRole = getRoleForNode(incomingNode, lanes);

                    // Regel für XOR Gateway
                    sbvrOutput.append("Es ist notwendig, dass die Aktivität '" + activityName + "' ausgeführt wird, wenn das XOR Gateway '" + gatewayName + "' ausgeführt wird.").append("\n");
                } else {
                    // Wenn der Vorgänger kein XOR Gateway ist, überprüfen wir den normalen Fluss
                    sourceActivity = getName(incomingNode);
                    sourceActivityRole = getRoleForNode(incomingNode, lanes);
                    sbvrOutput.append("Es ist notwendig, dass '" + targetActivityRole + "' '" + activityName + "' ausführt, wenn '" + sourceActivityRole + "' '" + sourceActivity + "' ausführt.").append("\n");
                }
            }
        }

        // Prüfen, ob es sich um einen Subprozess handelt
        if (node instanceof SubProcess subProcess) {
            String subProcessName = getName(subProcess);
            String sourceActivity = null;
            String sourceActivityRole = null;
            String targetActivityRole = getRoleForNode(subProcess, lanes);

            if (!subProcess.getIncoming().isEmpty()) {
                FlowNode incomingNode = subProcess.getIncoming().iterator().next().getSource();
                sourceActivity = getName(incomingNode);
                sourceActivityRole = getRoleForNode(incomingNode, lanes);
            }

            if (sourceActivity != null) {
                sbvrOutput.append("Es ist notwendig, dass '" + targetActivityRole + "' der Subprozess '" + subProcessName + "' ausführt, wenn '" + sourceActivityRole + "' '" + sourceActivity + "' ausführt.").append("\n");
            }
        }

        // Überprüfen auf IntermediateCatchEvent
        if (node instanceof IntermediateCatchEvent catchEvent) {
            String catchEventName = getName(catchEvent);
            String sourceActivity = null;
            if (!catchEvent.getIncoming().isEmpty()) {
                sourceActivity = getName(catchEvent.getIncoming().iterator().next().getSource());
            }
            String sourceActivityRole = getRoleForNode(catchEvent.getIncoming().iterator().next().getSource(), lanes);
            String targetActivityRole = getRoleForNode(catchEvent, lanes);

            if (sourceActivity != null) {
                sbvrOutput.append("Es ist notwendig, dass '" + targetActivityRole + "' das IntermediateCatchEvent '" + catchEventName + "' ausführt, wenn '" + sourceActivityRole + "' '" + sourceActivity + "' ausführt.").append("\n");
            }
        }

        // Handle IntermediateThrowEvent
        if (node instanceof IntermediateThrowEvent throwEvent) {
            String throwEventName = getName(throwEvent);
            String sourceActivity = null;
            if (!throwEvent.getIncoming().isEmpty()) {
                sourceActivity = getName(throwEvent.getIncoming().iterator().next().getSource());
            }
            String sourceActivityRole = getRoleForNode(throwEvent.getIncoming().iterator().next().getSource(), lanes);
            String targetActivityRole = getRoleForNode(throwEvent, lanes);

            if (sourceActivity != null) {
                sbvrOutput.append("Es ist notwendig, dass '" + targetActivityRole + "' das IntermediateThrowEvent '" + throwEventName + "' ausführt, wenn '" + sourceActivityRole + "' '" + sourceActivity + "' ausführt.").append("\n");
            }
        }

        // Prüfen auf Event-Based Gateway
        if (node instanceof EventBasedGateway eventBasedGateway) {
            String gatewayName = getName(eventBasedGateway);
            String sourceActivity = null;
            if (!eventBasedGateway.getIncoming().isEmpty()) {
                sourceActivity = getName(eventBasedGateway.getIncoming().iterator().next().getSource());
            }
            String sourceActivityRole = getRoleForNode(eventBasedGateway.getIncoming().iterator().next().getSource(), lanes);
            String targetActivityRole = getRoleForNode(eventBasedGateway, lanes);

            if (sourceActivity != null) {
                sbvrOutput.append("Es ist notwendig, dass '" + targetActivityRole + "' das Event-Based Gateway '" + gatewayName + "' ausführt, wenn '" + sourceActivityRole + "' '" + sourceActivity + "' ausführt.").append("\n");
            }
        }

        return sbvrOutput.toString();
    }

}
