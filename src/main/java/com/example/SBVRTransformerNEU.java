package com.example;

import org.camunda.bpm.model.bpmn.instance.*;

import java.util.*;

import static com.example.Hilfsmethoden.getName;
import static com.example.Hilfsmethoden.getRoleForNode;

public class SBVRTransformerNEU {

    public static void transformEventGatewayToSBVR(EventBasedGateway gateway, StringBuilder sbvrOutput, Collection<Lane> lanes, Set<String> processedGateways) {
        List<SequenceFlow> outgoingFlows = new ArrayList<>(gateway.getOutgoing());
        Set<String> uniqueStatements = new HashSet<>(); // Set zur Duplikatprüfung
        StringBuilder sbvrStatements = new StringBuilder();

        // Verhindere Dopplungen: Überprüfe, ob das Gateway bereits verarbeitet wurde
        String gatewayId = gateway.getId();
        if (processedGateways.contains(gatewayId)) {
            return;
        }
        processedGateways.add(gatewayId);

        String gatewayName = getName(gateway);

        // Iteriere über alle ausgehenden Flows
        for (SequenceFlow flow : outgoingFlows) {
            String targetName = getName(flow.getTarget());
            String eventType = Hilfsmethoden.getEventType(flow.getTarget()); // Methode, um den Ereignistyp (Nachricht, Timer, Bedingung) zu bestimmen

            // Generiere die SBVR-Aussage für den aktuellen Flow
            String eventStatement = createEventGatewayStatement(targetName, eventType);

            // Füge die Aussage hinzu, wenn sie noch nicht existiert
            if (!uniqueStatements.contains(eventStatement)) {
                uniqueStatements.add(eventStatement);
                sbvrStatements.append(eventStatement).append("\n");
            }
        }
        // Füge alle generierten SBVR-Aussagen zum Hauptoutput hinzu
        sbvrOutput.append(sbvrStatements);
    }


    //Erstellen nur die SBVR Logik
    public static void transformXORGatewayToSBVR(
            ExclusiveGateway gateway,
            StringBuilder sbvrOutput,
            String sourceRole,
            Collection<Lane> lanes,
            Set<String> processedGateways
    ) {
        // Liste der ausgehenden Sequenzflüsse des Gateways
        List<SequenceFlow> outgoingFlows = new ArrayList<>(gateway.getOutgoing());
        // Set für Duplikatprüfung, um redundante Aussagen zu vermeiden
        Set<String> uniqueStatements = new HashSet<>();
        // StringBuilder für SBVR-Aussagen
        StringBuilder sbvrStatements = new StringBuilder();

        // Verhindere die doppelte Verarbeitung eines Gateways
        String gatewayId = gateway.getId();
        if (processedGateways.contains(gatewayId)) {
            return; // Abbrechen, wenn das Gateway bereits verarbeitet wurde
        }
        processedGateways.add(gatewayId); // Gateway als verarbeitet markieren

        // Extrahiere relevante Informationen des Gateways
        String gatewayName = getName(gateway);
        String sourceName = getName(gateway.getIncoming().iterator().next().getSource());

        // Bedingung: Ein XOR-Gateway muss genau zwei ausgehende Flows haben
        if (outgoingFlows.size() == 2) {
            // Extrahiere die Ziel-Rollen und Ziel-Namen der ausgehenden Flows
            String targetRole1 = getRoleForNode(outgoingFlows.get(0).getTarget(), lanes);
            String targetRole2 = getRoleForNode(outgoingFlows.get(1).getTarget(), lanes);

            String targetName1 = getName(outgoingFlows.get(0).getTarget());
            String targetName2 = getName(outgoingFlows.get(1).getTarget());

            // Extrahiere die Bedingungen der ausgehenden Flows (falls vorhanden)
            String condition1 = outgoingFlows.get(0).getName() != null ? outgoingFlows.get(0).getName() : "unbekannte Bedingung";
            String condition2 = outgoingFlows.get(1).getName() != null ? outgoingFlows.get(1).getName() : "unbekannte Bedingung";

            // SBVR-Aussage für den ersten Flow
            String flowStatement1 = createFlowStatement(sourceRole, sourceName, targetRole1, targetName1, condition1);
            uniqueStatements.add(flowStatement1);// Vermeide Duplikate
            sbvrStatements.append(flowStatement1);

            // SBVR-Aussage für den zweiten Flow
            String flowStatement2 = createFlowStatement(sourceRole, sourceName, targetRole2, targetName2, condition2);
            if (uniqueStatements.add(flowStatement2)) { // Vermeide Duplikate
                sbvrStatements.append(flowStatement2);
            }

            // SBVR-Aussage für die Exklusionsregel (XOR-Logik)
            String exclusionStatement = createExclusionStatement(sourceName, targetRole1, targetName1, targetRole2, targetName2);
            if (uniqueStatements.add(exclusionStatement)) { // Vermeide Duplikate
                sbvrStatements.append(exclusionStatement);
            }
        } else {
            // Fall, wenn das XOR-Gateway nicht die erwartete Anzahl an ausgehenden Flows hat
            sbvrStatements.append("Warnung: XOR-Gateway '").append(gatewayName)
                    .append("' hat nicht genau zwei ausgehende Flows.\n");
        }

        // Füge die generierten SBVR-Aussagen dem Gesamtoutput hinzu
        sbvrOutput.append(sbvrStatements);
    }

//        // Subprozess-Transformation
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


    public static String createStartEventStatement(String sourceRole, String targetRole, String targetName, String startEventName) {
        return "Es ist notwendig, dass " + targetRole + " " + targetName + " ausführt, wenn " + sourceRole + " " + startEventName + " ausführt.";
    }

    private static String createFlowStatement(String sourceRole, String sourceName, String targetRole, String targetName, String condition) {
        return "Es ist notwendig, dass " + targetRole + " " + targetName + " ausführt, wenn " + sourceRole + " " + sourceName + " ausführt und " + condition + " gilt.\n";
    }

    private static String createExclusionStatement(String sourceName, String targetRole1, String targetName1, String targetRole2, String targetName2) {
        return "Es ist notwendig, dass " + targetRole1 + " " + targetName1 + " ausführt oder " + targetRole2 + " " + targetName2 + " ausführt, aber nicht beides gleichzeitig, wenn " + sourceName + " ausführt";
    }

    public static String createInclusiveStatement(String sourceRole, String sourceName, String targetRole, String targetName, String condition) {
        return "Es ist notwendig, dass " + targetRole + " " + targetName + " ausführt, wenn " + sourceRole + " " + sourceName + " ausführt und " + condition + " gilt.\n";
    }

    static String createParallelStatement(String sourceName, String targetRole1, String targetName1, String targetRole2, String targetName2, String gatewayName) {
        // Gateway-Name wird nun korrekt in der SBVR-Aussage verwendet
        return "Es ist notwendig, dass " + targetRole1 + " " + targetName1 + " ausführt und " + targetRole2 + " " + targetName2 + " ausführt, wenn " + gatewayName + " ausführt.";
    }

    static String createEndEventStatement(EndEvent endEvent, String sourceRole, String targetRole) {
        // Den Namen des EndEvents extrahieren
        String endEventName = Hilfsmethoden.getName(endEvent);  // Verwende Hilfsmethode getName, um den Namen des EndEvents zu extrahieren
        return "Es ist notwendig, dass " + targetRole + " Ende ausführt, wenn " + sourceRole + " " + endEventName + " ausführt.";
    }

    private static String createEventGatewayStatement(String activityName, String eventType) {
        // Formuliert die SBVR-Aussage basierend auf der Aktivität und dem Ereignis
        return "Es ist notwendig, dass die Aufgabe " + activityName + " startet, wenn das Datenereignis " + eventType + " eintritt.";
    }
}