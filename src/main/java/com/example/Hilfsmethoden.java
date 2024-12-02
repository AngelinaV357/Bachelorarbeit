package com.example;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Hilfsmethoden {

    public static String getEventType(FlowNode node) {
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

    public static MessageFlow getMessageFlowForEvent(IntermediateCatchEvent event, BpmnModelInstance modelInstance) {
        // Durchlaufen aller MessageFlows und Überprüfen, ob das IntermediateCatchEvent als Quelle des MessageFlows festgelegt ist
        for (MessageFlow flow : modelInstance.getModelElementsByType(MessageFlow.class)) {
            if (flow.getSource().equals(event)) {
                return flow; // Der MessageFlow wurde gefunden
            }
        }
        return null; // Kein passender MessageFlow gefunden
    }

    public static String getName(FlowNode node) {
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
     * @return Eine Map, in der die Teilnehmer-ID (participantId) den Teilnehmernamen (participantName) zugeordnet ist.
     */
    public static String getMessageFlowParticipantName(BaseElement element, Collection<Participant> participants) {
        // Prüfe, ob das Element ein Participant ist
        if (element instanceof Participant participant) {
            return participant.getName() != null ? participant.getName() : "Unbenannter Teilnehmer";
        }

        // Falls nicht, überprüfe die zugehörigen Teilnehmer anhand der Prozesse
        for (Participant participant : participants) {
            if (participant.getProcess() != null && participant.getProcess().equals(element)) {
                return participant.getName() != null ? participant.getName() : "Unbenannter Teilnehmer";
            }
        }

        return "Unbekannte Quelle/Ziel";
    }

    /**
     * Diese Methode gibt den Namen des Teilnehmers basierend auf der Teilnehmer-ID zurück.
     * Sie verwendet das BPMN-Modell, um die Teilnehmerrolle anhand der FlowNodes (Sender und Empfänger)
     * zu ermitteln.
     *
     * @param name Die ID des FlowNodes, der einem Teilnehmer zugeordnet ist.
     * @param modelInstance Das BPMN-Modell, das die Teilnehmerinformationen enthält.
     * @return Der Name des Teilnehmers oder "Unbekannter Teilnehmer", wenn keine Zuordnung gefunden wurde.
     */
    public static String getParticipantRole(String name, BpmnModelInstance modelInstance) {
        // Suche nach dem Participant-Element, nicht nach FlowNode
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);
        for (Participant participant : participants) {
            if (participant.getName().equals(name)) {
                // Hier handelt es sich um einen Teilnehmer, nicht um einen FlowNode
                return participant.getName(); // Oder eine andere Rolle
            }
        }
        return "Unbekannte Rolle"; // Standardwert
    }

    public static String getRoleForNode(FlowNode node, Collection<Lane> lanes) {
        for (Lane lane : lanes) {
            if (lane.getFlowNodeRefs().contains(node)) {
                return lane.getName();
            }
        }
        return "Unbekannte Rolle";
    }


    /**
     * Hilfsmethode zum Abrufen der Bedingung für einen Sequenzfluss
     */
    static String getCondition(SequenceFlow flow) {
        ConditionExpression condition = flow.getConditionExpression();
        if (condition != null) {
            return condition.getTextContent(); // Rückgabe des Textinhalts der Bedingung
        }
        return "keine Bedingung"; // Falls keine Bedingung vorhanden ist
    }

    /**
     * Schreibt Text in die Datei
     */
    static void writeToFile(String content, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            System.out.println("In die Datei geschrieben: " + file.getPath());
        } catch (IOException e) {
            System.out.println("Fehler beim Schreiben der Datei: " + e.getMessage());
        }
    }
}