package com.example;

import org.camunda.bpm.model.bpmn.instance.*;
import java.util.List;

public class SBVRTransformer {

    public static String transformRoleToSBVR(String roleName) {
        // SBVR-Ausdruck für eine Rolle im Format "Role X is a role"
        String sbvrRole = roleName + " is a role.";
        return sbvrRole;
    }
    /**
     * Transforms an Activity node into an SBVR-compliant statement.
     * @param activity The BPMN activity node.
     * @return SBVR-compliant statement describing the activity.
     */
    public static String transformActivityToSBVR(Activity activity) {
        String action = activity.getName() != null ? activity.getName() : "führt eine Aktivität aus";
        String role = "Rolle"; // You can extract the role if needed from the activity or context.

        // Example SBVR-compliant sentence for activities
        return "Es ist notwendig, dass " + role + " " + action + " ausführt.";
    }

    /**
     * Transforms a Start Event into an SBVR-compliant statement.
     * @param startEvent The BPMN start event node.
     * @return SBVR-compliant statement describing the start event.
     */
    public static String transformStartEventToSBVR(StartEvent startEvent) {
        return "Der Prozess startet.";
    }

    /**
     * Transforms an End Event into an SBVR-compliant statement.
     * @param endEvent The BPMN end event node.
     * @return SBVR-compliant statement describing the end event.
     */
    public static String transformEndEventToSBVR(EndEvent endEvent) {
        return "Der Prozess endet.";
    }

    /**
     * Transforms an Exclusive Gateway (XOR) node and a sequence flow into an SBVR-compliant statement.
     * @param gateway The XOR Gateway node.
     * @param outgoingFlows The sequence flow connected to the gateway.
     * @return SBVR-compliant conditional statement based on the gateway.
     */
    public static String transformXORGatewayToSBVR(ExclusiveGateway gateway, List<SequenceFlow> outgoingFlows) {
        String sbvrStatements = "";

        // Hier nehmen wir die Quelle des ersten ausgehenden Flows als die Aktivität vor dem XOR-Gateway
        String sourceName = getName(outgoingFlows.get(0).getSource()); // Name der Aktivität vor dem XOR-Gateway
        String role = "Rolle"; // Placeholder für die Rolle (kann angepasst werden, wenn mehr Informationen vorhanden sind)

        for (SequenceFlow flow : outgoingFlows) {
            String condition = flow.getName() != null ? flow.getName() : "unbekannte Bedingung";
            String targetName = getName(flow.getTarget());

            // Formatierte Ausgabe für das XOR-Gateway
            sbvrStatements += "Es ist obligatorisch, dass " + targetName + " oder " + targetName +
                    ", aber nicht beides, wenn " + role + " " + sourceName + " ausführt. ";
        }

        return sbvrStatements;
    }

    /**
     * Transforms a Parallel Gateway (AND) node into multiple necessity statements.
     * @param gateway The AND Gateway node.
     * @param outgoingFlows List of outgoing sequence flows.
     * @return Multiple necessity statements concatenated for the AND condition.
     */
    public static String transformANDGatewayToSBVR(ParallelGateway gateway, List<SequenceFlow> outgoingFlows) {
        String sbvrStatements = "";

        // Wir extrahieren den Namen der Quelle der ausgehenden Flows
        String sourceName = getName(outgoingFlows.get(0).getSource()); // Quelle des AND-Gateways (Activity davor)
        String role = "Rolle"; // Platzhalter für die Rolle (kann angepasst werden, wenn mehr Informationen vorhanden sind)

        for (SequenceFlow flow : outgoingFlows) {
            String targetName = getName(flow.getTarget());

            // Für jedes ausgehende Flow erstellen wir eine Regel
            sbvrStatements += "Es ist notwendig, dass " + role + " " + targetName + " ausführt, wenn " +
                    role + " " + sourceName + " ausführt. ";
        }

        return sbvrStatements;
    }

    /**
     * Transforms a generic flow node connection into an SBVR-compliant necessity statement.
     * @param source The source node of the sequence flow.
     * @param target The target node of the sequence flow.
     * @return SBVR-compliant statement describing the flow between source and target.
     */
    public static String transformSequenceFlowToSBVR(FlowNode source, FlowNode target) {
        String sourceName = getName(source);
        String targetName = getName(target);
        String role = "Rolle"; // You can extract the role if needed from the context or source node.

        // Showing the sequence flow more explicitly
        return "Es ist notwendig, dass " + role + " " + targetName + " ausführt, wenn " +
                role + " " + sourceName + " ausführt.";
    }

    /**
     * Retrieves the name of the node, with default labels for unnamed nodes.
     * @param node The BPMN node.
     * @return The name of the node or a default label.
     */
    private static String getName(FlowNode node) {
        if (node instanceof Activity activity) {
            return activity.getName() != null ? activity.getName() : "Unbekannte Aktivität";
        } else if (node instanceof StartEvent) {
            return "Start";
        } else if (node instanceof EndEvent) {
            return "Ende";
        } else if (node instanceof ExclusiveGateway) {
            return "XOR-Gateway";
        } else if (node instanceof ParallelGateway) {
            return "UND-Gateway";
        }
        return "Unbekannter Knoten";
    }

}
