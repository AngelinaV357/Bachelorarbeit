package com.example.Gateways;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.Collection;

import static com.example.Hilfsmethoden.getEventType;
import static com.example.Hilfsmethoden.getMessageFlowParticipantName;

public class EventBasedGatewayTransformer {

    public static void EventGatewayTransformer(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Alle Event-Based Gateways abrufen
        Collection<EventBasedGateway> gateways = modelInstance.getModelElementsByType(EventBasedGateway.class);

        // Alle MessageFlows und Teilnehmer abrufen
        Collection<MessageFlow> messageFlows = modelInstance.getModelElementsByType(MessageFlow.class);
        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);

        // Durch die Gateways iterieren
        for (EventBasedGateway gateway : gateways) {
            String gatewayName = gateway.getName() != null ? gateway.getName() : "Unbekanntes Event-Based Gateway"; // Default Name setzen
            sbvrOutput.append("Event-Based Gateway: ").append(gatewayName).append("\n");

            // Ausgangssequenzflüsse des Gateways abrufen
            Collection<SequenceFlow> outgoingFlows = gateway.getOutgoing();

            for (SequenceFlow flow : outgoingFlows) {
                FlowNode targetNode = flow.getTarget();

                // Wenn das Ziel ein IntermediateCatchEvent ist
                if (targetNode instanceof IntermediateCatchEvent catchEvent) {
                    sbvrOutput.append("  Verknüpft mit IntermediateCatchEvent: ")
                            .append(sanitizeName(catchEvent.getName())).append("\n");

                    // Hier den Ereignistyp ermitteln
                    String eventType = getEventType(catchEvent);
                    sbvrOutput.append(" Ereignistyp: ").append(eventType).append("\n");

                    // Hier wird der Name des Partizipanten aus dem MessageFlow des CatchEvents abgerufen
                    for (MessageFlow messageFlow : messageFlows) {
                        BaseElement source = (BaseElement) messageFlow.getSource();
                        BaseElement target = (BaseElement) messageFlow.getTarget();

                        // Wenn das Ziel des MessageFlows das IntermediateCatchEvent ist
                        if (target.equals(catchEvent)) {
                            String sourceName = getMessageFlowParticipantName(source, participants);
                            sbvrOutput.append("  Es ist notwendig, dass das Intermediate Catch Event ")
                                    .append(sanitizeName(eventType))
                                    .append(" ")
                                    .append(sanitizeName(catchEvent.getName()))
                                    .append(" eine Nachricht von ")
                                    .append(sourceName)
                                    .append(" empfängt, bevor fortgeführt wird.");
                        }
                    }
                }
                // Wenn das Ziel ein IntermediateThrowEvent ist
                else if (targetNode instanceof IntermediateThrowEvent throwEvent) {
                    sbvrOutput.append("  Verknüpft mit IntermediateThrowEvent: ")
                            .append(sanitizeName(throwEvent.getName())).append("\n");

                    // Hier den Ereignistyp ermitteln
                    String eventType = getEventType(throwEvent);
                    sbvrOutput.append("    Ereignistyp: ").append(eventType).append("\n");

                    // Hier wird der Name des Partizipanten aus dem MessageFlow des ThrowEvents abgerufen
                    for (MessageFlow messageFlow : messageFlows) {
                        BaseElement source = (BaseElement) messageFlow.getSource();
                        BaseElement target = (BaseElement) messageFlow.getTarget();

                        // Wenn das Quell-Element des MessageFlows das IntermediateThrowEvent ist
                        if (source.equals(throwEvent)) {
                            String targetName = getMessageFlowParticipantName(target, participants);
                            sbvrOutput.append("  Es ist notwendig, dass das Intermediate Throw Event ")
                                    .append(sanitizeName(throwEvent.getName()))
                                    .append(" eine Nachricht an den Participant ")
                                    .append(targetName)
                                    .append(" sendet.\n");
                        }
                    }
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
}
