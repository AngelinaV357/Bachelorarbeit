package com.example;

import org.camunda.bpm.model.bpmn.instance.*;
import java.util.List;
import java.util.Collection;

import static com.example.XMLReader.getRoleForNode;

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

    public static String transformXORGatewayToSBVR(ExclusiveGateway gateway, List<SequenceFlow> outgoingFlows, String sourceRole, List<String> targetRoles, Collection<Lane>lanes
    ) {
        StringBuilder sbvrStatements = new StringBuilder();

        // Hole den Namen des Gateways
        String gatewayName = getName(gateway);

        // Hole den Namen der Quelle des Gateways
        String sourceName = getName(gateway.getIncoming().iterator().next().getSource());

        // Wenn die Anzahl der ausgehenden Flows genau 2 beträgt
        if (outgoingFlows.size() == 2) {
            // Hole die beiden ausgehenden Flows
            SequenceFlow flow1 = outgoingFlows.get(0);
            SequenceFlow flow2 = outgoingFlows.get(1);

            // Hole die Zielknoten und deren Rollen
            String targetRole1 = getRoleForNode(flow1.getTarget(), lanes);
            String targetRole2 = getRoleForNode(flow2.getTarget(), lanes);

            String targetName1 = getName(flow1.getTarget());
            String targetName2 = getName(flow2.getTarget());

            String condition1 = flow1.getName() != null ? flow1.getName() : "unbekannte Bedingung";
            String condition2 = flow2.getName() != null ? flow2.getName() : "unbekannte Bedingung";

            // SBVR für das XOR-Gateway formulieren
            sbvrStatements.append("Es ist notwendig, dass ")
                    .append(targetRole1).append(" ").append(targetName1).append(" ausführt oder ")
                    .append(targetRole2).append(" ").append(targetName2).append(" ausführt, ")
                    .append("aber nicht beides, wenn ").append(sourceRole).append(" ")
                    .append(sourceName).append(" ausführt.\n");

            // Optional: Falls du die Bedingungen der Flows in die SBVR-Aussage einfließen lassen möchtest
            sbvrStatements.append("Bedingungen: ")
                    .append(targetRole1).append(" Bedingung: ").append(condition1).append(", ")
                    .append(targetRole2).append(" Bedingung: ").append(condition2).append("\n");

        } else {
            sbvrStatements.append("Fehler: XOR-Gateway sollte genau zwei ausgehende Flows haben.\n");
        }

        return sbvrStatements.toString();
    }
//    public static String transformXORGatewayToSBVR(ExclusiveGateway gateway, List<SequenceFlow> outgoingFlows, String sourceRole, List<String> targetRoles) {
//        StringBuilder sbvrStatements = new StringBuilder();
//
//        String gatewayName = getName(gateway); // Name des Gateways
//        String sourceName = getName(gateway.getIncoming().iterator().next().getSource()); // Quelle des Gateways
//
//        // Iteration über alle ausgehenden Flows
//        for (int i = 0; i < outgoingFlows.size(); i++) {
//            SequenceFlow flow = outgoingFlows.get(i); // Aktueller Flow
//            String condition = flow.getName() != null ? flow.getName() : "unbekannte Bedingung"; // Bedingung des Flows
//            String targetRole = targetRoles.get(i); // Zielrolle
//            String targetName = getName(flow.getTarget()); // Zielname
//
//            // SBVR-Statement hinzufügen
//            sbvrStatements.append("Es ist obligatorisch, dass ")
//                    .append(targetRole).append(" ")
//                    .append(targetName).append(" oder ")
//                    .append(targetName).append(" ausführt, aber nicht beides, wenn ")
//                    .append(sourceRole).append(" ")
//                    .append(sourceName).append(" ausführt und Bedingung: ")
//                    .append(condition).append(".\n");
//        }
//
//        return sbvrStatements.toString();
//    }

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

            // Für jeden ausgehenden Flow erstellen wir eine Regel
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
    public static String transformSequenceFlowToSBVR(FlowNode source, FlowNode target, String sourceRole, String targetRole) {
        String sourceName = getName(source);
        String targetName = getName(target);

        // Showing the sequence flow more explicitly
        return "Es ist notwendig, dass " + targetRole + " " + targetName + " ausführt, wenn " +
                sourceRole + " " + sourceName + " ausführt.";
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
