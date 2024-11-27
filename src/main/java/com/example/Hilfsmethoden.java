package com.example;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Hilfsmethoden {
    // --- Helper Methods ---

    static String getEventType(FlowNode node) {
        if (node instanceof IntermediateCatchEvent) {
            EventDefinition eventDefinition = ((IntermediateCatchEvent) node).getEventDefinitions().iterator().next();
            if (eventDefinition instanceof MessageEventDefinition) {
                return "Nachricht";
            } else if (eventDefinition instanceof TimerEventDefinition) {
                return "Timer";
            } else if (eventDefinition instanceof ConditionalEventDefinition) {
                return "Bedingung";
            }
        }
        return "unbekannter Ereignistyp";
    }

    static String getName(FlowNode node) {
        if (node instanceof Activity activity) {
            String name = activity.getName();
            if (name == null || name.isEmpty()) {
                System.out.println("Warnung: Aktivität ohne Namen gefunden.");
                return "Unbekannte Aktivität";
            }
            return name;
        } else if (node instanceof StartEvent) {
            return "Start";
        } else if (node instanceof EndEvent) {
            return "Ende";
        } else if (node instanceof ExclusiveGateway) {
            return "XOR-Gateway";
        } else if (node instanceof ParallelGateway) {
            return "UND-Gateway";
        } else {
            System.out.println("Warnung: Unbekannter Knotentyp: " + node.getClass().getSimpleName());
        }
        return "Unbekannter Knoten";
    }

//    public static String transformRoleToSBVR(String roleName) {
//        return roleName + " is a role.";
//    }
    /**
     * Diese Methode extrahiert die Teilnehmerrollen (Participants) aus dem BPMN-Modell und erstellt eine Map,
     * die die IDs der Teilnehmer den entsprechenden Namen zuordnet.
     *
     * @param modelInstance Das BpmnModelInstance, das die BPMN-Daten enthält.
     * @return Eine Map, in der die Teilnehmer-ID (participantId) den Teilnehmernamen (participantName) zugeordnet ist.
     */
    public static Map<String, String> getParticipantName(BpmnModelInstance modelInstance) {
        Map<String, String> participants = new HashMap<>();

        // Extrahieren der Teilnehmerrollen aus dem Modell
        Collection<Participant> participantElements = modelInstance.getModelElementsByType(Participant.class);

        for (Participant participant : participantElements) {
            String participantId = participant.getId();
            String participantName = participant.getName() != null ? participant.getName() : "Unbekannter Teilnehmer";

            // Zuordnung von participantId zu participantName
            participants.put(participantId, participantName);
        }

        return participants;  // Gibt die Map zurück
    }

    /**
     * Diese Methode gibt den Namen des Teilnehmers basierend auf der Teilnehmer-ID zurück.
     * Sie verwendet das BPMN-Modell, um die Teilnehmerrolle anhand der FlowNodes (Sender und Empfänger)
     * zu ermitteln.
     *
     * @param nodeId Die ID des FlowNodes, der einem Teilnehmer zugeordnet ist.
     * @param modelInstance Das BPMN-Modell, das die Teilnehmerinformationen enthält.
     * @return Der Name des Teilnehmers oder "Unbekannter Teilnehmer", wenn keine Zuordnung gefunden wurde.
     */
    public static String getParticipantRole(String nodeId, BpmnModelInstance modelInstance) {
        // Extrahieren der Teilnehmerrollen aus dem Modell
        Map<String, String> participants = getParticipantName(modelInstance);

        // Rückgabe des Teilnehmernamens basierend auf der nodeId
        return participants.getOrDefault(nodeId, "Unbekannter Teilnehmer");
    }

    static String getRoleForNode(FlowNode node, Collection<Lane> lanes) {
        for (Lane lane : lanes) {
            if (lane.getFlowNodeRefs().contains(node)) {
                return lane.getName();
            }
        }
        return "Unbekannte Rolle";
    }

    // Hilfsmethode zum Abrufen der Bedingung für einen Sequenzfluss
    static String getCondition(SequenceFlow flow) {
        ConditionExpression condition = flow.getConditionExpression();
        if (condition != null) {
            return condition.getTextContent(); // Rückgabe des Textinhalts der Bedingung
        }
        return "keine Bedingung"; // Falls keine Bedingung vorhanden ist
    }
    // Schreibt Text in eine Datei
    static void writeToFile(String content, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            System.out.println("In die Datei geschrieben: " + file.getPath());
        } catch (IOException e) {
            System.out.println("Fehler beim Schreiben der Datei: " + e.getMessage());
        }
    }

}