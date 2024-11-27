package com.example;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.io.*;
import java.util.*;

import static com.example.Hilfsmethoden.*;
import static com.example.SBVRTransformerNEU.*;

public class BPMNProcessor { //BPMN Modell verarbeiten
    // Liest die BPMN-Datei ein und gibt das BpmnModelInstance zurück
    static BpmnModelInstance readBpmnFile(File file) {
        System.out.println("BPMN-Datei wird eingelesen...");
        return Bpmn.readModelFromFile(file);
    }

    // Verarbeitet die Sequenzflüsse
    public static void processSequenceFlows(BpmnModelInstance modelInstance, StringBuilder standardOutput, StringBuilder sbvrOutput) {
        // Holt alle Sequenzflüsse (SequenceFlow) aus dem Modell
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
        // Holt alle Lane-Elemente, die für die Zuweisung von Rollen verwendet werden
        Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);
        Set<String> processedGateways = new HashSet<>();

        // Iteriert über alle Sequenzflüsse
        for (SequenceFlow flow : sequenceFlows) {
            // Bestimmt den Quell- und Zielknoten des Sequenzflusses
            FlowNode source = flow.getSource();
            FlowNode target = flow.getTarget();
            // Bestimmt die Rolle des Quell- und Zielknotens basierend auf den Lanes
            String sourceRole = Hilfsmethoden.getRoleForNode(source, lanes);
            String targetRole = Hilfsmethoden.getRoleForNode(target, lanes);

            // Überprüft den Typ des Quellknotens und ruft die passende Verarbeitungsmethode auf
            if (source instanceof StartEvent) {
                sbvrOutput.append(transformStartEventToSBVR((StartEvent) source, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof EndEvent) {
                sbvrOutput.append(transformEndEventToSBVR((EndEvent) source, sourceRole, targetRole, lanes)).append("\n");
            } else if (source instanceof ExclusiveGateway) {
                // Für XOR-Gateways wird eine spezielle Verarbeitung durchgeführt
                transformXORGatewayToSBVR((ExclusiveGateway) source, sbvrOutput, sourceRole, lanes, processedGateways);
            } else if (source instanceof ParallelGateway) {
                List<SequenceFlow> outgoingFlows = new ArrayList<>(((ParallelGateway) source).getOutgoing());
                sbvrOutput.append(transformANDGatewayToSBVR((ParallelGateway) source, outgoingFlows, (List<Lane>) lanes)).append("\n");
            } else if (source instanceof InclusiveGateway) {
                // Verarbeitet Inklusive Gateways
                List<SequenceFlow> outgoingFlows = new ArrayList<>(((InclusiveGateway) source).getOutgoing());
                sbvrOutput.append(transformInklusiveGatewayToSBVR((InclusiveGateway) source, outgoingFlows, (List<Lane>) lanes)).append("\n");
            } else if (source instanceof EventBasedGateway) {
                // Für Event-Gateways wird die neue Methode aufgerufen
                transformEventGatewayToSBVR((EventBasedGateway) source, sbvrOutput, lanes, processedGateways);
//            } else if (source instanceof SubProcess) {
//                processSubProcesses((SubProcess) source, standardOutput, sbvrOutput);
            } else {
                // Für alle anderen Knoten wird ein generisches Logging eingefügt
                standardOutput.append("Nicht unterstützter Knoten-Typ: ").append(source.getClass().getSimpleName()).append("\n");
            }
        }
    }

    public static String transformANDGatewayToSBVR(ParallelGateway gateway, List<SequenceFlow> outgoingFlows, List<Lane> lanes) {
        StringBuilder sbvrStatements = new StringBuilder();
        Set<String> uniqueStatements = new HashSet<>(); // Set für Duplikatprüfung

        // Bestimme den Namen des Quellknotens (Eingang des Gateways)
        String sourceName = getName(gateway.getIncoming().iterator().next().getSource());

        // Bestimme die Rolle des Gateways
        String role = getRoleForNode(gateway, lanes);

        // Überprüfe, ob es zwei Ausgangsflüsse gibt
        if (outgoingFlows.size() == 2) {
            // Hole die Zielnamen und Zielrollen für beide Ausgangsflüsse
            String targetName1 = getName(outgoingFlows.get(0).getTarget());
            String targetRole1 = getRoleForNode(outgoingFlows.get(0).getTarget(), lanes);

            String targetName2 = getName(outgoingFlows.get(1).getTarget());
            String targetRole2 = getRoleForNode(outgoingFlows.get(1).getTarget(), lanes);

            // Erstelle die SBVR-Aussage für das Parallel-Gateway
            String sbvrStatement = createParallelStatement(sourceName, targetRole1, targetName1, targetRole2, targetName2, getName(gateway));

            // Vermeide Duplikate
            uniqueStatements.add(sbvrStatement);
            sbvrStatements.append(sbvrStatement);
        } else {
            // Wenn es nicht genau zwei Ausgangsflüsse gibt, füge eine Standardnachricht hinzu oder behandle es anders
            sbvrStatements.append("Es gibt nicht genau zwei Ausgangsflüsse für das Parallel-Gateway.");
        }

        return sbvrStatements.toString();
    }

    public static String transformInklusiveGatewayToSBVR(InclusiveGateway gateway, List<SequenceFlow> outgoingFlows, List<Lane> lanes) {
        StringBuilder sbvrStatements = new StringBuilder();
        Set<String> uniqueStatements = new HashSet<>(); // Set für Duplikatprüfung

        // Bestimme den Namen des Quellknotens (Eingang des Gateways)
        String sourceName = getName(gateway.getIncoming().iterator().next().getSource());

        // Bestimme die Rolle des Quellknotens
        String sourceRole = getRoleForNode(gateway.getIncoming().iterator().next().getSource(), lanes);

        // Gehe alle Ausgangsflüsse durch
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            // Hole die Zielnamen und Zielrollen für den aktuellen Ausgangsfluss
            String targetName = getName(outgoingFlow.getTarget());
            String targetRole = getRoleForNode(outgoingFlow.getTarget(), lanes);

            // Hole die Bedingung des aktuellen Flusses (z.B. cond(1), cond(2), ...)
            String condition = getCondition(outgoingFlow); // Diese Methode musst du ggf. definieren, um die Bedingung zu holen

            // Erstelle die SBVR-Aussage für diesen Ausgangsfluss
            String sbvrStatement = createInclusiveStatement(sourceRole, sourceName, targetRole, targetName, condition);

            // Vermeide Duplikate
            if (uniqueStatements.add(sbvrStatement)) {
                sbvrStatements.append(sbvrStatement);
            }
        }

        // Wenn keine Ausgangsflüsse vorhanden sind, füge eine Standardnachricht hinzu
        if (outgoingFlows.isEmpty()) {
            sbvrStatements.append("Es gibt keine Ausgangsflüsse für das Inklusive Gateway.");
        }

        return sbvrStatements.toString();
    }

    public static String transformEndEventToSBVR(EndEvent endEvent, String sourceRole, String targetRole, Collection<Lane> lanes) {
        // Den Namen des EndEvents extrahieren
        String endEventName = Hilfsmethoden.getName(endEvent);  // Aufruf der Hilfsmethode getName

        // Konvertiere die Collection der Ausgangsflüsse in eine Liste, um auf die Elemente per Index zuzugreifen
        List<SequenceFlow> outgoingFlows = new ArrayList<>(endEvent.getOutgoing());

        // Überprüfe, ob es mindestens einen Ausgangsfluss gibt
        if (!outgoingFlows.isEmpty()) {
            // Bestimmt den Namen des Zielknotens (z.B. eine Aktivität oder ein anderes Event)
            String targetName = Hilfsmethoden.getName(outgoingFlows.get(0).getTarget());

            // Jetzt das SBVR-Statement mit der Methode createEndEventStatement generieren
            return createEndEventStatement(endEvent, sourceRole, targetRole);
        } else {
            // Falls keine Ausgangsflüsse vorhanden sind, eine entsprechende Fehlermeldung oder Standardantwort zurückgeben
            return "Fehler: EndEvent hat keine Ausgangsflüsse.";
        }
    }

    public static String transformStartEventToSBVR(StartEvent startEvent, String sourceRole, String targetRole, Collection<Lane> lanes) {
        // Den Namen des StartEvents extrahieren
        String startEventName = Hilfsmethoden.getName(startEvent);  // Aufruf der Hilfsmethode getName

        // Konvertiere die Collection der Ausgangsflüsse in eine Liste, um auf die Elemente per Index zuzugreifen
        List<SequenceFlow> outgoingFlows = new ArrayList<>(startEvent.getOutgoing());

        // Überprüfe, ob es mindestens einen Ausgangsfluss gibt
        if (!outgoingFlows.isEmpty()) {
            // Bestimmt den Namen des Zielknotens (z.B. eine Aktivität oder ein anderes Event)
            String targetName = Hilfsmethoden.getName(outgoingFlows.get(0).getTarget());

            // Jetzt das SBVR-Statement mit der Methode createStartEventStatement generieren
            return createStartEventStatement(sourceRole, startEventName, targetRole, targetName);
        } else {
            // Falls keine Ausgangsflüsse vorhanden sind, eine entsprechende Fehlermeldung oder Standardantwort zurückgeben
            return "Fehler: StartEvent hat keine Ausgangsflüsse.";
        }
    }
}