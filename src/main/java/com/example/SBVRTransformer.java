package com.example;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.*;

import static com.example.Hilfsmethoden.getName;
import static com.example.Hilfsmethoden.getParticipantName;
import static com.example.Hilfsmethoden.getRoleForNode;

public class SBVRTransformer {

    // --- Start/Ende Events ---
    public static String transformStartEventToSBVR(StartEvent startEvent) {
        return "Der Prozess startet.";
    }

    public static String transformEndEventToSBVR(EndEvent endEvent) {
        return "Der Prozess endet.";
    }

    //!ÜBERARBEITEN!
    public static String transformActivityToSBVR(Activity activity, Set<String> processedActivities) {
        String action = activity.getName() != null ? activity.getName() : " aus";

        String activityStatement = action + " ist eine ";
        if (activity instanceof UserTask) {
            activityStatement += "User Story.";
        } else if (activity instanceof SendTask) {
            activityStatement += "SendTask.";
        } else {
            activityStatement += "normale Aktivität";
        }

        // Duplikatprüfung für Aktivitäten
        if (!processedActivities.contains(activityStatement)) {
            processedActivities.add(activityStatement);
            return activityStatement;
        }

        return ""; // Leeres Statement, wenn bereits verarbeitet
    }

    public static String transformTaskTypeToSBVR(Activity activity) {
        if (activity instanceof UserTask) {
            return "Es handelt sich um eine Benutzeraufgabe.";
        } else if (activity instanceof ServiceTask) {
            return "Es handelt sich um eine automatisierte Aufgabe.";
        } else {
            return "Es handelt sich um eine unbekannte Aufgabe.";
        }
    }

    //XOR Gateway ausgabe
    public static String transformXORGatewayToSBVR(ExclusiveGateway gateway, List<SequenceFlow> outgoingFlows, String sourceRole, List<String> targetRoles, Collection<Lane> lanes, Set<String> processedGateways) {
        StringBuilder sbvrStatements = new StringBuilder();
        Set<String> uniqueStatements = new HashSet<>(); // Set für Duplikatprüfung

        // Verhindere Dopplungen: Überprüfe, ob das Gateway bereits verarbeitet wurde
        String gatewayId = gateway.getId();
        if (processedGateways.contains(gatewayId)) {
            return sbvrStatements.toString();
        }
        processedGateways.add(gatewayId);

        String gatewayName = getName(gateway);
        String sourceName = getName(gateway.getIncoming().iterator().next().getSource());

        // Wenn die Anzahl der ausgehenden Flows genau 2 beträgt
        if (outgoingFlows.size() == 2) {
            // Variablen für die Flows und Bedingungen
            String targetRole1 = getRoleForNode(outgoingFlows.get(0).getTarget(), lanes);
            String targetRole2 = getRoleForNode(outgoingFlows.get(1).getTarget(), lanes);

            String targetName1 = getName(outgoingFlows.get(0).getTarget());
            String targetName2 = getName(outgoingFlows.get(1).getTarget());

            String condition1 = outgoingFlows.get(0).getName() != null ? outgoingFlows.get(0).getName() : "unbekannte Bedingung";
            String condition2 = outgoingFlows.get(1).getName() != null ? outgoingFlows.get(1).getName() : "unbekannte Bedingung";

            // Vermeidung von Duplikaten durch Set
            String flowStatement1 = "Es ist notwendig, dass " + targetRole1 + " " + targetName1 + " ausführt, wenn " +
                    sourceRole + " " + sourceName + " ausführt und " + condition1 + " gilt.\n";
            String flowStatement2 = "Es ist notwendig, dass " + targetRole2 + " " + targetName2 + " ausführt, wenn " +
                    sourceRole + " " + sourceName + " ausführt und " + condition2 + " gilt.\n";

            uniqueStatements.add(flowStatement1);
            sbvrStatements.append(flowStatement1);
            if (!uniqueStatements.contains(flowStatement2)) {
                uniqueStatements.add(flowStatement2);
                sbvrStatements.append(flowStatement2);
            }

            // Regel für das XOR-Gateways
            String exclusionStatement = "Es ist notwendig, dass " + targetRole1 + " " + targetName1 + " ausführt oder " +
                    targetRole2 + " " + targetName2 + " ausführt, aber nicht beides gleichzeitig, wenn " + sourceName + "ausführt";

            if (!uniqueStatements.contains(exclusionStatement)) {
                uniqueStatements.add(exclusionStatement);
                sbvrStatements.append(exclusionStatement);
            }
        }

        return sbvrStatements.toString();
    }

    public static String transformANDGatewayToSBVR(ParallelGateway gateway, List<SequenceFlow> outgoingFlows, List<Lane> lanes) {
        StringBuilder sbvrStatements = new StringBuilder();
        Set<String> uniqueStatements = new HashSet<>(); // Set für Duplikatprüfung

        String sourceName = getName(gateway.getIncoming().iterator().next().getSource());

        // Bestimmen der Rolle automatisch, basierend auf der Lane des Gateways
        String role = getRoleForNode(gateway, lanes);

        // Iteriere über alle ausgehenden Flows
        for (SequenceFlow flow : outgoingFlows) {
            String targetName = getName(flow.getTarget());

            // Erstelle das SBVR-Statement
            String sbvrStatement = "Es ist notwendig, dass " + role + " " + targetName + " ausführt, wenn " +
                    role + " " + sourceName + " ausführt.";

            // Vermeide Duplikate
            if (!uniqueStatements.contains(sbvrStatement)) {
                uniqueStatements.add(sbvrStatement);
                sbvrStatements.append(sbvrStatement).append(" ");
            }
        }

        return sbvrStatements.toString();
    }

    // --- Sequence Flow Transformation ---
    public static String transformSequenceFlowToSBVR(FlowNode source, FlowNode target, String sourceRole, String targetRole, Set<String> processedFlows) {
        String sourceName = getName(source);
        String targetName = getName(target);

        String flowStatement = "Es ist notwendig, dass " + targetRole + " " + targetName + " ausführt, wenn " +
                sourceRole + " " + sourceName + " ausführt.";

        // Verhindere Duplikate durch Set
        if (!processedFlows.contains(flowStatement)) {
            processedFlows.add(flowStatement);
            return flowStatement;
        }
        return ""; // Leeres Statement, wenn bereits verarbeitet
    }

    // --- Data Objects & SendTask Transformation ---
    public static String transformDataObjectToSBVR(BaseElement node) {
        if (node instanceof DataObject) {
            return transformDataObject((DataObject) node);
        }
        // Entferne die Behandlung von SendTask hier
        return "";
    }

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

    private static String transformSendTask(SendTask sendTask) {
        String taskName = sendTask.getName() != null ? sendTask.getName() : "Unbekannte SendTask";
        return taskName + " ist eine SendTask";
    }

    //Catch event sendet etwas an die Betroffene Nachricht; Nachricht nicht ausgefüllt
    public static String processAndTransformIntermediateCatchEvents(MessageFlow messageFlow, Collection<Lane> lanes, Map<String, String> participants) {
        StringBuilder sbvrStatement = new StringBuilder();

        // Name des Senders und Empfängers
        String senderName = getName((FlowNode) messageFlow.getSource());
        String receiverName = getName((FlowNode) messageFlow.getTarget());

        // Hole die Teilnehmerrollen basierend auf der XML-Datenstruktur
        String senderRole = getParticipantName(senderName, participants);
        String receiverRole = getParticipantName(receiverName, participants);

        // Nachrichtenname
        String messageName = messageFlow.getName() != null ? messageFlow.getName() : "unbekannte Nachricht";

        // Erstellen der SBVR-Regel mit den erkannten Teilnehmernamen
        sbvrStatement.append("Es ist notwendig, dass ")
                .append(senderRole).append(" ")
                .append(senderName)
                .append(" die Nachricht ")
                .append(messageName)
                .append(" an ")
                .append(receiverRole).append(" ")
                .append(receiverName)
                .append(" sendet.\n");

        // Ausgabe zur Konsole
        System.out.println(sbvrStatement);

        return sbvrStatement.toString();  // Gibt die vollständige Ausgabe zurück, wenn benötigt
    }


    //Throw event empfängt etwas von dem Participant; ausgemaltes Nachrichtenzeichen
    static void processAndTransformIntermediateThrowEvents(BpmnModelInstance modelInstance, StringBuilder output, StringBuilder sbvrOutput) {
        // Sammlung aller IntermediateThrowEvents im Modell
        Collection<IntermediateThrowEvent> intermediateThrowEvents = modelInstance.getModelElementsByType(IntermediateThrowEvent.class);

        // Durchlaufe alle IntermediateThrowEvents
        for (IntermediateThrowEvent event : intermediateThrowEvents) {
            String eventName = event.getName() != null ? event.getName() : "Unbenanntes Event";
            String eventId = event.getId();

            // Event in Standardausgabe hinzufügen
            String transformedEvent = "IntermediateThrowEvent: " + eventName + " (ID: " + eventId + ")";
            output.append(transformedEvent).append("\n");

            // Ausgabe in der Konsole
            System.out.println(transformedEvent);

            // SBVR Transformation hinzufügen
            String sbvrTransformed = eventName + " ist ein IntermediateThrowEvent ";
            sbvrOutput.append(sbvrTransformed).append("\n");

            // Ausgabe in der Konsole
            System.out.println(sbvrTransformed);
        }
    }

    public static String processAndTransformBusinessRuleTask(BusinessRuleTask businessRuleTask) {
        String taskName = businessRuleTask.getName();
        String implementation = businessRuleTask.getImplementation();
        return "\"" + taskName + "\" ist ein BusinessRuleTask\"";
    }

    public static String transformRoleToSBVR(String roleName) {
        // SBVR-Ausdruck für eine Rolle im Format "Role X is a role"
        return roleName + " is a role.";
    }
}