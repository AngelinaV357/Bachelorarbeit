package com.example.Event;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.Collection;

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

//            sbvrOutput.append("IntermediateCatchEvent: ").append(catchEventName).append("\n");

            // Ereignistyp ermitteln (z. B. Timer, Message, Signal etc.)
            String eventType = getEventType(catchEvent);
//            sbvrOutput.append("  Ereignistyp: ").append(eventType).append("\n");

            // Verknüpfte MessageFlows analysieren
            for (MessageFlow messageFlow : messageFlows) {
                BaseElement source = (BaseElement) messageFlow.getSource();
                BaseElement target = (BaseElement) messageFlow.getTarget();

                // Wenn das Ziel des MessageFlows das aktuelle IntermediateCatchEvent ist
                if (target.equals(catchEvent)) {
                    String sourceName = getMessageFlowParticipantName(source, participants);
                    sbvrOutput.append(" Es ist notwendig, dass das IntermediateCatchEvent ")
                            .append(sanitizeName(eventType))
                            .append(" ")
                            .append(catchEventName)
                            .append(" eine Nachricht von ")
                            .append(sourceName)
                            .append(" empfängt, bevor fortgeführt wird.\n");
                }
            }

//            // Ausgangssequenzflüsse analysieren
//            Collection<SequenceFlow> outgoingFlows = catchEvent.getOutgoing();
//            for (SequenceFlow flow : outgoingFlows) {
//                FlowNode targetNode = flow.getTarget();

//                // Ziel des Sequenzflusses hinzufügen
//                if (targetNode != null) {
//                    sbvrOutput.append("  Sequenzfluss führt zu: ").append(sanitizeName(targetNode.getName())).append("\n");
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
