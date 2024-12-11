package com.example;

import org.camunda.bpm.model.bpmn.impl.instance.IntermediateCatchEventImpl;
import org.camunda.bpm.model.bpmn.impl.instance.TaskImpl;
import org.camunda.bpm.model.bpmn.instance.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
        } else if (node instanceof InclusiveGateway) {
            return "OR-Gateway";
        }else if (node instanceof EventBasedGateway) {
            return "Event";
        }else if (node instanceof IntermediateCatchEventImpl) {
            return "CatchEvent";
        }else if (node instanceof IntermediateCatchEvent) {
            return "CatchEvent";
        }else if (node instanceof IntermediateThrowEvent) {
            return "ThrowEvent";
        }else if (node instanceof TaskImpl) {
            return "Task";
        }else if (node instanceof BusinessRuleTask) {
            return "BusinessRuleTask";
        }else {
            return "Nicht unterstützter Knotentyp";
        }
    }


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
     * Hilfsmethode, um den Teilnehmernamen aus dem MessageFlow zu extrahieren
     */
    public static String getMessageFlowParticipantNameFromSendTask(SendTask sendTask, Collection<MessageFlow> messageFlows, Collection<Participant> participants) {
        for (MessageFlow messageFlow : messageFlows) {
            BaseElement source = (BaseElement) messageFlow.getSource();
            BaseElement target = (BaseElement) messageFlow.getTarget();
            if (source.equals(sendTask)) {
                return getMessageFlowParticipantName(target, participants);
            }
        }
        return "Unbekannter Teilnehmer";
    }

    public static String getRoleForNode(FlowNode node, Collection<Lane> lanes) {
        Set<String> roles = new HashSet<>();
        for (Lane lane : lanes) {
            if (lane.getFlowNodeRefs().contains(node)) {
                roles.add(lane.getName());
            }
        }

        // Wenn mehrere Rollen gefunden werden, gib eine spezifische Fehlermeldung zurück
        if (roles.isEmpty()) {
            return "Unbekannte Rolle";
        }
        // Wenn mehrere Rollen vorhanden sind, könnte man z.B. die erste nehmen
        if (roles.size() > 1) {
            return "Mehrere Rollen zugeordnet";
        }
        return roles.iterator().next();
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

    /**
     * Bereinigt den Namen, entfernt unerwünschte Zeilenumbrüche oder Sonderzeichen.
     */
    public static String sanitizeName(String name) {
        if (name == null) {
            return "Unbenannt";
        }
        return name.replaceAll("[\\r\\n]+", " ").trim();
    }

}