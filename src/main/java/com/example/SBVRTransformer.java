package com.example;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.List;
import java.util.Collection;
import java.util.Set;

import static com.example.XMLReader.getRoleForNode;

public class SBVRTransformer {

    public static String transformRoleToSBVR(String roleName) {
        // SBVR-Ausdruck für eine Rolle im Format "Role X is a role"
        String sbvrRole = roleName + " is a role.";
        return sbvrRole;
    }
    public static String transformActivityToSBVR(Activity activity) {
        String action = activity.getName() != null ? activity.getName() : "führt eine Aktivität aus";

        // Überprüfen, ob es sich um eine Benutzeraufgabe oder eine Servicetask handelt
        if (activity instanceof UserTask) {
            return action + " ist eine User Story.";
        }
        {
            return action + " ist eine Service Story.";
        }
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

    public static String transformXORGatewayToSBVR(ExclusiveGateway gateway, List<SequenceFlow> outgoingFlows, String sourceRole, List<String> targetRoles, Collection<Lane> lanes, Set<String> processedGateways) {
        StringBuilder sbvrStatements = new StringBuilder();

        // Verhindere Dopplungen: Überprüfe, ob das Gateway bereits verarbeitet wurde
        String gatewayId = gateway.getId(); // ID des Gateways holen
        if (processedGateways.contains(gatewayId)) {
            // Gateway wurde bereits verarbeitet, keine Ausgabe mehr
            return sbvrStatements.toString();
        }

        // Markiere das Gateway als verarbeitet
        processedGateways.add(gatewayId);

        // Hole den Namen des Gateways
        String gatewayName = getName(gateway);

        // Hole den Namen der Quelle des Gateways
        String sourceName = getName(gateway.getIncoming().iterator().next().getSource());

        // Wenn die Anzahl der ausgehenden Flows genau 2 beträgt
        if (outgoingFlows.size() == 2) {
            SequenceFlow flow1 = outgoingFlows.get(0);
            SequenceFlow flow2 = outgoingFlows.get(1);

            String targetRole1 = getRoleForNode(flow1.getTarget(), lanes);
            String targetRole2 = getRoleForNode(flow2.getTarget(), lanes);

            String targetName1 = getName(flow1.getTarget());
            String targetName2 = getName(flow2.getTarget());

            String condition1 = flow1.getName() != null ? flow1.getName() : "unbekannte Bedingung";
            String condition2 = flow2.getName() != null ? flow2.getName() : "unbekannte Bedingung";

            sbvrStatements.append("Es ist notwendig, dass ")
                    .append(targetRole1).append(" ").append(targetName1).append(" ausführt oder ")
                    .append(targetRole2).append(" ").append(targetName2).append(" ausführt, ")
                    .append("aber nicht beides, wenn ").append(sourceRole).append(" ")
                    .append(sourceName).append(" ausführt.\n");

            sbvrStatements.append("Bedingungen: ")
                    .append(targetRole1).append(" Bedingung: ").append(condition1).append(", ")
                    .append(targetRole2).append(" Bedingung: ").append(condition2).append("\n");
        }
        return sbvrStatements.toString();
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

    /**
     * Transforms the task type into an SBVR-compliant statement specifying whether it is a UserTask or ServiceTask.
     * @param activity The BPMN activity node.
     * @return SBVR-compliant statement describing the type of task.
     */
    public static String transformTaskTypeToSBVR(Activity activity) {
        if (activity instanceof UserTask) {
            return "Es handelt sich um eine Benutzeraufgabe.";
        } else if (activity instanceof ServiceTask) {
            return "Es handelt sich um eine automatisierte Aufgabe.";
        } else {
            return "Es handelt sich um eine unbekannte Aufgabe.";
        }
    }

    public static String transformDataObjectToSBVR(BaseElement node) {
        if (node instanceof DataObject) {
            return transformDataObject((DataObject) node);
        } else if (node instanceof SendTask) {
            return transformSendTask((SendTask) node);
        }
        return "";
    }

    // Methode zur Verarbeitung von DataObject oder DataObjectReference
    static String transformDataObject(BaseElement element) {
        if (element instanceof DataObject dataObject) {
            String dataObjectName = dataObject.getName() != null ? dataObject.getName() : "Unbekanntes DataObject";
            return dataObjectName + " ist ein Datenobjekt.";
        } else if (element instanceof DataObjectReference dataObjectReference) {
            String dataObjectName = dataObjectReference.getAttributeValue("dataObjectRef");
            dataObjectName = (dataObjectName != null && !dataObjectName.isEmpty()) ? dataObjectName : "Unbekannte DataObjectReference";
            return dataObjectName + " ist eine Referenz auf ein Datenobjekt.";
        }
        return "Unbekanntes Element.";
    }

    // Methode zur Verarbeitung von SendTask
    private static String transformSendTask(SendTask sendTask) {
        String taskName = sendTask.getName() != null ? sendTask.getName() : "Unbekannte SendTask";
        // SBVR-Regel für SendTask
        String sbvrRule = taskName + " ist eine SendTask";
        return sbvrRule;
    }

    static String processAndTransformIntermediateCatchEvents(BpmnModelInstance modelInstance) {
        StringBuilder output = new StringBuilder();
        StringBuilder sbvrOutput = new StringBuilder();

        // Sammlung von IntermediateCatchEvent-Elementen
        Collection<IntermediateCatchEvent> intermediateCatchEvents = modelInstance.getModelElementsByType(IntermediateCatchEvent.class);

        for (IntermediateCatchEvent event : intermediateCatchEvents) {
            String eventName = event.getName() != null ? event.getName() : "Unbenanntes, Event";
            String eventId = event.getId();

            // Standardausgabe und Transformation
            String transformedEvent = "IntermediateCatchEvent: " + eventName;
            output.append(transformedEvent).append("\n");
            System.out.println(transformedEvent);

            // SBVR-Transformation und Ausgabe
            String sbvrTransformed = eventName + " ist ein IntermediateCatchEvent ";
            sbvrOutput.append(sbvrTransformed).append("\n");
            System.out.println(sbvrTransformed);
        }

        // Rückgabe der Ausgaben
        return "Standard Output:\n" + output.toString() + "\nSBVR Output:\n" + sbvrOutput.toString();
    }

    static void processAndTransformIntermediateThrowEvents(BpmnModelInstance modelInstance, StringBuilder output, StringBuilder sbvrOutput) {
        // Sammlung von IntermediateThrowEvent-Elementen
        Collection<IntermediateThrowEvent> intermediateThrowEvents = modelInstance.getModelElementsByType(IntermediateThrowEvent.class);

        for (IntermediateThrowEvent event : intermediateThrowEvents) {
            String eventName = event.getName() != null ? event.getName() : "Unbenanntes Event";
            String eventId = event.getId();

            // Standardausgabe und Transformation
            String transformedEvent = "IntermediateThrowEvent: " + eventName + " (ID: " + eventId + ")";
            output.append(transformedEvent).append("\n");
            System.out.println(transformedEvent);

            // SBVR-Transformation und Ausgabe
            String sbvrTransformed = eventName + " ist ein IntermediateThrowEvent ";
            sbvrOutput.append(sbvrTransformed).append("\n");
            System.out.println(sbvrTransformed);
        }
    }
}
