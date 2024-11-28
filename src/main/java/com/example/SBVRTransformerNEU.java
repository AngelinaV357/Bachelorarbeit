package com.example;

import org.camunda.bpm.model.bpmn.instance.*;

import java.util.*;

import static com.example.Hilfsmethoden.getName;
import static com.example.Hilfsmethoden.getRoleForNode;

public class SBVRTransformerNEU {

    public static String createStartEventStatement(String sourceRole, String targetRole, String targetName, String startEventName) {
        return "Es ist notwendig, dass " + targetName + " " + targetRole + " ausführt, wenn " + sourceRole + " " + startEventName + " ausführt.";
    }

    public static String createFlowStatement(String sourceRole, String sourceName, String targetRole, String targetName, String condition) {
        return "Es ist notwendig, dass " + targetRole + " " + targetName + " ausführt, wenn " + sourceRole + " " + sourceName + " ausführt und " + condition + " gilt.\n";
    }

    public static String createExclusionStatement(String sourceName, String targetRole1, String targetName1, String targetRole2, String targetName2) {
        return "Es ist notwendig, dass " + targetRole1 + " " + targetName1 + " ausführt oder " + targetRole2 + " " + targetName2 + " ausführt, aber nicht beides gleichzeitig, wenn " + sourceName + " ausführt";
    }

    public static String createInclusiveStatement(String sourceRole, String sourceName, String targetRole, String targetName, String condition) {
        return "Es ist notwendig, dass " + targetRole + " " + targetName + " ausführt, wenn " + sourceRole + " " + sourceName + " ausführt und " + condition + " gilt.\n";
    }

    public static String createIntermediateCatchEventStatement(String senderRole, String senderName, String receiverRole, String receiverName) {
        return "Es ist notwendig, dass " + senderRole + " " + senderName + " an " + receiverRole + " " + receiverName + " sendet.\n";
    }

    public static String createIntermediateThrowEventStatement(String senderRole, String senderName, String receiverRole, String receiverName, String messageName) {
        // Diese Methode erstellt eine SBVR-Regel für das IntermediateThrowEvent
        return "Es ist notwendig, dass " + senderRole + " " + senderName + " die Nachricht " + messageName + " an " + receiverRole + " " + receiverName + " sendet.";
    }

    public static String createParallelStatement(String sourceName, String targetRole1, String targetName1, String targetRole2, String targetName2, String gatewayName) {
        // Gateway-Name wird nun korrekt in der SBVR-Aussage verwendet
        return "Es ist notwendig, dass " + targetRole1 + " " + targetName1 + " ausführt und " + targetRole2 + " " + targetName2 + " ausführt, wenn " + gatewayName + " ausführt.";
    }

    public static String createEndEventStatement(EndEvent endEvent, String sourceRole, String targetRole) {
        // Den Namen des EndEvents extrahieren
        String endEventName = Hilfsmethoden.getName(endEvent);  // Verwende Hilfsmethode getName, um den Namen des EndEvents zu extrahieren
        return "Es ist notwendig, dass " + targetRole + " Ende ausführt, wenn " + sourceRole + " " + endEventName + " ausführt.";
    }

    public static String createEventGatewayStatement(String activityName, String eventType) {
        // Formuliert die SBVR-Aussage basierend auf der Aktivität und dem Ereignis
        return "Es ist notwendig, dass die Aufgabe " + activityName + " startet, wenn das Datenereignis " + eventType + " eintritt.";
    }
}