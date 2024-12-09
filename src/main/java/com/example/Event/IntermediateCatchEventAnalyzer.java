package com.example.Event;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.Collection;
import java.util.Iterator;

import static com.example.Hilfsmethoden.getEventType;
import static com.example.Hilfsmethoden.getMessageFlowParticipantName;

public class IntermediateCatchEventAnalyzer {

    public static void analyzeCatchEvents(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Alle IntermediateCatchEvents abrufen
        Collection<IntermediateCatchEvent> catchEvents = modelInstance.getModelElementsByType(IntermediateCatchEvent.class);

        // Alle MessageFlows und Teilnehmer abrufen
        Collection<MessageFlow> messageFlows = modelInstance.getModelElementsByType(MessageFlow.class);
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);

        // Durch alle IntermediateCatchEvents iterieren
        for (IntermediateCatchEvent catchEvent : catchEvents) {
            // Namen des IntermediateCatchEvents abrufen
            String catchEventName = sanitizeName(catchEvent.getName());

            // Ereignistyp ermitteln (z. B. Timer, Message, Signal etc.)
            String eventType = getEventType(catchEvent);

            // Überprüfen, ob es sich um ein TimerEvent handelt
            boolean isTimerEvent = false;
            for (EventDefinition eventDefinition : catchEvent.getEventDefinitions()) {
                if (eventDefinition instanceof TimerEventDefinition) {
                    isTimerEvent = true; // Es handelt sich um ein Timer Event
                    eventType = "Timer"; // Ereignistyp auf "Timer" setzen

                    // SBVR-Regel für Timer Event hinzufügen
                    sbvrOutput.append("Es ist notwendig, dass das IntermediateCatchEvent ")
                            .append(sanitizeName(catchEventName))
                            .append(" ")
                            .append(" ausgeführt wird, nachdem die festgelegte Zeit abgelaufen ist.\n");

                    break;
                }
            }

            // Wenn es sich nicht um ein Timer Event handelt, prüfen wir auf Message Event
            if (!isTimerEvent) {
                for (EventDefinition eventDefinition : catchEvent.getEventDefinitions()) {
                    if (eventDefinition instanceof MessageEventDefinition) {
                        eventType = "Message"; // Ereignistyp auf "Message" setzen
                        break;
                    }
                }
            }

            // Verknüpfte MessageFlows analysieren
            for (MessageFlow messageFlow : messageFlows) {
                BaseElement source = (BaseElement) messageFlow.getSource();
                BaseElement target = (BaseElement) messageFlow.getTarget();

                // Wenn das Ziel des MessageFlows das aktuelle IntermediateCatchEvent ist
                if (target.equals(catchEvent)) {
                    String sourceName = getMessageFlowParticipantName(source, participants);
                    sbvrOutput.append("Es ist notwendig, dass das IntermediateCatchEvent ")
                            .append(sanitizeName(eventType))
                            .append(" '")
                            .append(catchEventName)
                            .append("' eine Nachricht von '")
                            .append(sourceName)
                            .append("' empfängt, bevor fortgeführt wird.\n");
                }
            }
        }
        sbvrOutput.append("\n");
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
}
