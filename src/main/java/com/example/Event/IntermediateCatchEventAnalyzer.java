package com.example.Event;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.Collection;

import static com.example.Hilfsmethoden.getEventType;
import static com.example.Hilfsmethoden.getMessageFlowParticipantName;
import static com.example.Hilfsmethoden.getRoleForNode;

public class IntermediateCatchEventAnalyzer {

    public static void analyzeCatchEvents(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Alle IntermediateCatchEvents abrufen
        Collection<IntermediateCatchEvent> catchEvents = modelInstance.getModelElementsByType(IntermediateCatchEvent.class);

        // Alle MessageFlows und Teilnehmer abrufen
        Collection<MessageFlow> messageFlows = modelInstance.getModelElementsByType(MessageFlow.class);
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);
        Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);

        // Durch alle IntermediateCatchEvents iterieren
        for (IntermediateCatchEvent catchEvent : catchEvents) {
            // Namen des IntermediateCatchEvents abrufen
            String catchEventName = sanitizeName(catchEvent.getName());

            // Ereignistyp ermitteln (z. B. Timer, Message, Signal etc.)
            String eventType = getEventType(catchEvent);

            // Überprüfen, ob es sich um ein TimerEvent handelt
            boolean isTimerEvent = false;
            Activity previousActivity = null; // Die vorherige Aktivität
            FlowElement previousFlowElement = null; // Das vorherige Element (Aktivität oder Gateway)
            Activity nextActivity = null; // Die nachfolgende Aktivität
            for (EventDefinition eventDefinition : catchEvent.getEventDefinitions()) {
                if (eventDefinition instanceof TimerEventDefinition) {
                    isTimerEvent = true; // Es handelt sich um ein Timer Event
                    eventType = "Timer"; // Ereignistyp auf "Timer" setzen

                    // Vorherige Aktivität oder Gateway ermitteln
                    previousFlowElement = getPreviousFlowElement(catchEvent, modelInstance);

                    // Nachfolgende Aktivität ermitteln (die Aktivität, die vom CatchEvent folgt)
                    nextActivity = getNextActivity(catchEvent, modelInstance);

                    // Wenn das vorherige Element ein Gateway ist
                    if (previousFlowElement instanceof Gateway) {
                        String roleName = getRoleForNode((FlowNode) previousFlowElement, lanes);
                        sbvrOutput.append("Es ist notwendig, dass das Event-Based Gateway '")
                                .append(sanitizeName(previousFlowElement.getName()))
                                .append("' abgeschlossen wird, bevor das Timer Event '")
                                .append(sanitizeName(catchEventName))
                                .append("' eintritt.")
                                .append(".\n");
                    } else if (previousFlowElement instanceof Activity) {
                        String roleName = getRoleForNode((FlowNode) previousFlowElement, lanes);
                        sbvrOutput.append("Es ist notwendig, dass '")
                                .append(roleName)
                                .append("' die Aktivität '")
                                .append(previousFlowElement != null ? previousFlowElement.getName() : "unbekannte Aktivität")
                                .append("' ausführt, bevor das Timer Event '")
                                .append(sanitizeName(catchEventName))
                                .append("' eintritt.")
                                .append("'.\n");
                    }
                    String roleName = getRoleForNode((FlowNode) previousFlowElement, lanes);
                    // SBVR-Regel für die nachfolgende Aktivität (nach dem Timer Event)
                    sbvrOutput.append("Es ist notwendig, dass '")
                            .append(roleName)
                            .append("' die Aktivität '")
                            .append(nextActivity != null ? nextActivity.getName() : "unbekannte Aktivität")
                            .append("' ausgeführt, nachdem das Timer Event '")
                            .append(sanitizeName(catchEventName))
                            .append("' eingetreten ist.\n");

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

    /**
     * Findet das vorherige Element (Aktivität oder Gateway), das zum IntermediateCatchEvent führt.
     */
    private static FlowElement getPreviousFlowElement(IntermediateCatchEvent catchEvent, BpmnModelInstance modelInstance) {
        // Alle Sequenzflüsse durchsuchen, um das vorherige Element zu finden
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);

        for (SequenceFlow sequenceFlow : sequenceFlows) {
            if (sequenceFlow.getTarget().equals(catchEvent)) {
                BaseElement sourceElement = sequenceFlow.getSource();
                if (sourceElement instanceof Activity || sourceElement instanceof Gateway) {
                    return (FlowElement) sourceElement;
                }
            }
        }
        return null; // Kein vorheriges Element gefunden
    }

    /**
     * Findet die nachfolgende Aktivität, die nach dem IntermediateCatchEvent ausgeführt wird.
     */
    private static Activity getNextActivity(IntermediateCatchEvent catchEvent, BpmnModelInstance modelInstance) {
        // Alle Sequenzflüsse durchsuchen, um die nachfolgende Aktivität zu finden
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);

        for (SequenceFlow sequenceFlow : sequenceFlows) {
            if (sequenceFlow.getSource().equals(catchEvent)) {
                BaseElement targetElement = sequenceFlow.getTarget();
                if (targetElement instanceof Activity) {
                    return (Activity) targetElement;
                }
            }
        }
        return null; // Keine nachfolgende Aktivität gefunden
    }
}
