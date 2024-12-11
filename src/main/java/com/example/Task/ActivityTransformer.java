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

                // Überprüfen, ob das eingehende Element ein XOR-Gateway ist
                if (incomingNode instanceof ExclusiveGateway xorGateway) {
                    // XOR Gateway-Logik
                    String gatewayName = xorGateway.getAttributeValue("name");

                    // Fallback für den Fall, dass der Name des Gateways leer oder null ist
                    if (gatewayName == null || gatewayName.isEmpty()) {
                        gatewayName = "Unbenanntes XOR Gateway";  // Fallback-Wert setzen
                    }

                    // Rolle der eingehenden Aktivität ermitteln
                    String roleName = getRoleForNode((FlowNode) incomingNode, lanes);

                    // Regel für XOR Gateway
                    sbvrOutput.append("Es ist notwendig, dass '")
                            .append(roleName)
                            .append("' die Aktivität '")
                            .append(activityName)  // activityName sollte der Name der aktuellen Aktivität sein
                            .append("' ausführt, wenn das XOR Gateway '")
                            .append(gatewayName)
                            .append("' aktiv ist.\n");
                } else if (incomingNode instanceof ParallelGateway andGateway) {
                    // AND Gateway-Logik (Parallel-Gateway)
                    String gatewayName = andGateway.getAttributeValue("name");

                    if (gatewayName == null || gatewayName.isEmpty()) {
                        gatewayName = "Unbenanntes AND Gateway";  // Fallback-Wert setzen
                    }

                    sourceActivityRole = getRoleForNode(incomingNode, lanes);

                    // Regel für AND Gateway (Parallel Gateway)
                    sbvrOutput.append("Es ist notwendig, dass die Aktivitäten '" + activityName + "' und '" + getName(incomingNode) + "' ausgeführt werden, wenn das AND Gateway '" + gatewayName + "' aktiv ist und alle eingehenden Pfade abgeschlossen sind.").append("\n");
                } else {
                    // Wenn der Vorgänger kein XOR oder AND Gateway ist, überprüfen wir den normalen Fluss
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

                // Prüfen, ob der Vorgänger ein Gateway ist
                if (incomingNode instanceof ParallelGateway andGateway) {
                    // AND Gateway (Parallel Gateway) - Alle eingehenden Pfade müssen abgeschlossen sein
                    String gatewayName = andGateway.getAttributeValue("name");
                    if (gatewayName == null || gatewayName.isEmpty()) {
                        gatewayName = "Unbenanntes AND Gateway";  // Fallback-Wert setzen
                    }

                    // Regel für Subprozess und AND Gateway (Parallel Gateway)
                    sbvrOutput.append("Es ist notwendig, dass '" + targetActivityRole + "' den Subprozess '" + subProcessName + "' ausführt, wenn '" + sourceActivityRole + "' '" + sourceActivity + "' aktiv ist und alle eingehenden Pfade abgeschlossen sind.").append("\n");
                } else if (incomingNode instanceof InclusiveGateway inclusiveGateway) {
                    // Inclusive Gateway - Mindestens ein eingehender Pfad muss abgeschlossen sein
                    String gatewayName = inclusiveGateway.getAttributeValue("name");
                    if (gatewayName == null || gatewayName.isEmpty()) {
                        gatewayName = "Unbenanntes Inklusives Gateway";  // Fallback-Wert setzen
                    }

                    // Regel für Subprozess und Inclusive Gateway
                    sbvrOutput.append("Es ist notwendig, dass '" + targetActivityRole + "' den Subprozess '" + subProcessName + "' ausführt, wenn '" + sourceActivityRole + "' '" + sourceActivity + "' aktiv ist und mindestens ein eingehender Pfad abgeschlossen ist.").append("\n");
                } else if (incomingNode instanceof ExclusiveGateway exclusiveGateway) {
                    // Exklusives Gateway (OR Gateway) - Nur einer der eingehenden Pfade wird aktiviert
                    String gatewayName = exclusiveGateway.getAttributeValue("name");
                    if (gatewayName == null || gatewayName.isEmpty()) {
                        gatewayName = "Unbenanntes OR Gateway";  // Fallback-Wert setzen
                    }

                    // Regel für Subprozess und Exklusives Gateway
                    sbvrOutput.append("Es ist notwendig, dass '" + targetActivityRole + "' den Subprozess '" + subProcessName + "' ausführt, wenn '" + sourceActivityRole + "' '" + sourceActivity + "' aktiv ist und der Pfad durch das OR Gateway '" + gatewayName + "' abgeschlossen ist.").append("\n");
                } else {
                    // Regel für Subprozess ohne Gateway oder andere Gateways
                    sbvrOutput.append("Es ist notwendig, dass '" + targetActivityRole + "' den Subprozess '" + subProcessName + "' ausführt, wenn '" + sourceActivityRole + "' '" + sourceActivity + "' ausführt.").append("\n");
                }
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
