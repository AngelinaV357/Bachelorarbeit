package com.example.Event;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.Collection;

import static com.example.Hilfsmethoden.getEventType;
import static com.example.Hilfsmethoden.getMessageFlowParticipantName;

public class IntermediateThrowEventAnalyzer {

    public static void analyzeThrowEvents(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Alle IntermediateThrowEvents abrufen
        Collection<IntermediateThrowEvent> throwEvents = modelInstance.getModelElementsByType(IntermediateThrowEvent.class);

        // Alle MessageFlows und Teilnehmer abrufen
        Collection<MessageFlow> messageFlows = modelInstance.getModelElementsByType(MessageFlow.class);
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);

        // Durch alle IntermediateThrowEvents iterieren
        for (IntermediateThrowEvent throwEvent : throwEvents) {
            // Namen des IntermediateThrowEvents abrufen
            String throwEventName = sanitizeName(throwEvent.getName());

            // Ereignistyp ermitteln (z. B. Timer, Message, Signal etc.)
            String eventType = getEventType(throwEvent);

            // Verknüpfte MessageFlows analysieren
            for (MessageFlow messageFlow : messageFlows) {
                BaseElement source = (BaseElement) messageFlow.getSource();
                BaseElement target = (BaseElement) messageFlow.getTarget();

                // Wenn das Quell-Element des MessageFlows das aktuelle IntermediateThrowEvent ist
                if (source.equals(throwEvent)) {
                    String targetName = getMessageFlowParticipantName(target, participants);
                    sbvrOutput.append(" Es ist notwendig, dass das IntermediateThrowEvent ")
                            .append(sanitizeName(eventType))
                            .append(" ")
                            .append(throwEventName)
                            .append(" eine Nachricht an ")
                            .append(targetName)
                            .append(" sendet.\n");
                }
            }
        }
    }

    /**
     * Bereinigt den Namen, entfernt unerwünschte Zeilenumbrüche oder Sonderzeichen.
     */
    private static String sanitizeName(String name) {
        if (name == null) {
            return "Unbenannt";
        }
        // Entfernt Zeilenumbrüche und überflüssige Leerzeichen
        return name.replaceAll("[\\r\\n]+", " ").trim();
    }

    /**
     * Erstellt eine SBVR-Regel für das IntermediateThrowEvent.
     */
    public static String createIntermediateThrowEventStatement(String senderRole, String senderName, String receiverRole, String receiverName, String messageName) {
        return "Es ist notwendig, dass " + senderRole + " " + senderName + " die Nachricht " + messageName + " an " + receiverRole + " " + receiverName + " sendet.";
    }
}
