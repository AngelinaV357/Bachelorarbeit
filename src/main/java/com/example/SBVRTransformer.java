//package com.example;
//
//import org.camunda.bpm.model.bpmn.BpmnModelInstance;
//import org.camunda.bpm.model.bpmn.instance.*;
//
//import java.util.*;
//
//import static com.example.Hilfsmethoden.*;
//
//public class SBVRTransformer { //Transformation der BPMN Modelle in SBVR Syntax
//

//    public static String transformTaskTypeToSBVR(Activity activity) {
//        if (activity instanceof UserTask) {
//            return "Es handelt sich um eine Benutzeraufgabe.";
//        } else if (activity instanceof ServiceTask) {
//            return "Es handelt sich um eine automatisierte Aufgabe.";
//        } else {
//            return "Es handelt sich um eine unbekannte Aufgabe.";
//        }
//    }

    //Sequence Flow Transformation
//    public static String transformSequenceFlowToSBVR(FlowNode source, FlowNode target, String sourceRole, String targetRole, Set<String> processedFlows) {
//        String sourceName = getName(source);
//        String targetName = getName(target);
//
//        String flowStatement = "Es ist notwendig, dass " + targetRole + " " + targetName + " ausführt, wenn " +
//                sourceRole + " " + sourceName + " ausführt.";
//
//        // Verhindere Duplikate durch Set
//        if (!processedFlows.contains(flowStatement)) {
//            processedFlows.add(flowStatement);
//            return flowStatement;
//        }
//        return ""; // Leeres Statement, wenn bereits verarbeitet
//    }
//
//    //Data Objects & SendTask Transformation
//    public static String transformDataObjectToSBVR(BaseElement node) {
//        if (node instanceof DataObject) {
//            return transformDataObject((DataObject) node);
//        }
//        // Entferne die Behandlung von SendTask hier
//        return "";
//    }
//
//    /**
//     * Transformiert ein BPMN-Element (DataObject, DataObjectReference oder DataInput) in eine textuelle Beschreibung.
//     *
//     * @param element Ein BPMN-Element, das transformiert werden soll.
//     * @return Eine textuelle Beschreibung des Elements. Gibt "Unbekanntes Element" zurück, wenn der Typ nicht erkannt wird.
//     */
//    static String transformDataObject(BaseElement element) {
//        if (element instanceof DataObject dataObject) {
//            String dataObjectName = dataObject.getName() != null ? dataObject.getName() : "Unbekanntes DataObject";
//            return dataObjectName + " ist ein Datenobjekt.";
//        } else if (element instanceof DataObjectReference dataObjectReference) {
//            String dataObjectName = dataObjectReference.getAttributeValue("dataObjectRef");
//            dataObjectName = (dataObjectName != null && !dataObjectName.isEmpty()) ? dataObjectName : "Unbekannte DataObjectReference";
//            return dataObjectName + " ist eine Referenz auf ein Datenobjekt.";
//        } else if ("dataInput".equals(element.getElementType().getTypeName())) {
//            // Erkennung von dataInput
//            String inputDataName = element.getAttributeValue("name");
//            inputDataName = (inputDataName != null && !inputDataName.isEmpty()) ? inputDataName : "Unbekanntes InputData";
//            return inputDataName + " ist ein Input-Datenobjekt.";
//        }
//        return "Unbekanntes Element.";
//
//
//    }
//
//    private static String transformSendTask(SendTask sendTask) {
//        String taskName = sendTask.getName() != null ? sendTask.getName() : "Unbekannte SendTask";
//        return taskName + " ist eine SendTask";
//    }
//
//    //Catch event sendet etwas an die Betroffene Nachricht; Nachricht nicht ausgefüllt
//    public static String processAndTransformIntermediateCatchEvents(MessageFlow messageFlow, Collection<Lane> lanes, BpmnModelInstance modelInstance) {
//        StringBuilder sbvrStatement = new StringBuilder();
//
//        // Name des Senders und Empfängers
//        String senderName = getName((FlowNode) messageFlow.getSource());
//        String receiverName = getName((FlowNode) messageFlow.getTarget());
//
//        // Hole die Teilnehmerrollen basierend auf der XML-Datenstruktur und dem BpmnModelInstance
//        String senderRole = getParticipantRole(senderName, modelInstance);  // Verwendet getParticipantRole statt getParticipantName
//        String receiverRole = getParticipantRole(receiverName, modelInstance);  // Verwendet getParticipantRole statt getParticipantName
//
//        // Nachrichtenname
//        String messageName = messageFlow.getName() != null ? messageFlow.getName() : "unbekannte Nachricht";
//
//        // Erstellen der SBVR-Regel mit den erkannten Teilnehmernamen
//        sbvrStatement.append("Es ist notwendig, dass ")
//                .append(senderRole).append(" ")
//                .append(senderName)
//                .append(" die Nachricht ")
//                .append(messageName)
//                .append(" an ")
//                .append(receiverRole).append(" ")
//                .append(receiverName)
//                .append(" sendet.\n");
//
//        // Ausgabe zur Konsole
//        System.out.println(sbvrStatement);
//
//        return sbvrStatement.toString();  // Gibt die vollständige Ausgabe zurück, wenn benötigt
//    }
//
//    //    Throw event empfängt etwas von dem Participant; ausgemaltes Nachrichtenzeichen
//    static void processAndTransformIntermediateThrowEvents(BpmnModelInstance modelInstance, StringBuilder output, StringBuilder sbvrOutput) {
//        // Sammlung aller IntermediateThrowEvents im Modell
//        Collection<IntermediateThrowEvent> intermediateThrowEvents = modelInstance.getModelElementsByType(IntermediateThrowEvent.class);
//
//        // Durchlaufe alle IntermediateThrowEvents
//        for (IntermediateThrowEvent event : intermediateThrowEvents) {
//            String eventName = event.getName() != null ? event.getName() : "Unbenanntes Event";
//            String eventId = event.getId();
//
//            // Event in Standardausgabe hinzufügen
//            String transformedEvent = "IntermediateThrowEvent: " + eventName + " (ID: " + eventId + ")";
//            output.append(transformedEvent).append("\n");
//
//            // Ausgabe in der Konsole
//            System.out.println(transformedEvent);
//
//            // SBVR Transformation hinzufügen
//            String sbvrTransformed = eventName + " ist ein IntermediateThrowEvent ";
//            sbvrOutput.append(sbvrTransformed).append("\n");
//
//            // Ausgabe in der Konsole
//            System.out.println(sbvrTransformed);
//        }
//    }
//
//    public static String processAndTransformBusinessRuleTask(BusinessRuleTask businessRuleTask) {
//        String taskName = businessRuleTask.getName();
//        String implementation = businessRuleTask.getImplementation();
//        return "\"" + taskName + "\" ist ein BusinessRuleTask\"";
//    }
//
//    //
//    // Subprozess-Transformation
//    public static String transformSubProcessToSBVR(SubProcess subProcess, Set<String> processedSubProcesses) {
//        String subProcessName = subProcess.getName() != null ? subProcess.getName() : "unbenannter Subprozess";
//
//        // Verhindere doppelte Verarbeitung
//        if (processedSubProcesses.contains(subProcessName)) {
//            return ""; // Leeres Statement zurückgeben, falls bereits verarbeitet
//        }
//
//        processedSubProcesses.add(subProcessName);
//
//        // SBVR-Regel erstellen
//        String sbvrStatement = subProcessName + "\" ist ein Subprozess.";
//        return sbvrStatement;
//    }
//}
