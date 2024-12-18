package com.example.XMLIteration.Task.Gateways;

import org.camunda.bpm.model.bpmn.instance.*;
import java.util.Collection;

import static com.example.XMLIteration.Task.Hilfsmethoden.getMessageFlowParticipantName;

public class EventBasedGatewayTransformer {

    public static void EventGatewayTransformer(EventBasedGateway eventBasedGateway, StringBuilder sbvrOutput) {
        // Alle MessageFlows und Teilnehmer abrufen
        Collection<MessageFlow> messageFlows = eventBasedGateway.getModelInstance().getModelElementsByType(MessageFlow.class);
        Collection<Participant> participants = eventBasedGateway.getModelInstance().getModelElementsByType(Participant.class);

        // Ausgangssequenzflüsse des Gateways abrufen
        Collection<SequenceFlow> outgoingFlows = eventBasedGateway.getOutgoing();

        for (SequenceFlow flow : outgoingFlows) {
            FlowNode targetNode = flow.getTarget();

            // Wenn das Ziel ein IntermediateCatchEvent ist
            if (targetNode instanceof IntermediateCatchEvent catchEvent) {
                boolean isTimerEvent = false;

                // Überprüfe, ob es sich um ein Timer Event handelt
                for (EventDefinition eventDefinition : catchEvent.getEventDefinitions()) {
                    if (eventDefinition instanceof TimerEventDefinition) {
                        isTimerEvent = true;

                        for (MessageFlow messageFlow : messageFlows) {
                            BaseElement source = (BaseElement) messageFlow.getSource();
                            BaseElement target = (BaseElement) messageFlow.getTarget();

                            if (target.equals(catchEvent)) {
                                String sourceName = getMessageFlowParticipantName(source, participants);
                                sbvrOutput.append("Es ist notwendig, dass das Timer Event ")
                                        .append(sanitizeName(catchEvent.getName()))
                                        .append(" eine Nachricht von ")
                                        .append(sourceName)
                                        .append(" empfängt, bevor fortgeführt wird.\n");
                            }
                        }
                    }
                }

                // Wenn es kein Timer Event ist, wird der normale Event-Typ behandelt
                if (!isTimerEvent) {
                    for (MessageFlow messageFlow : messageFlows) {
                        BaseElement source = (BaseElement) messageFlow.getSource();
                        BaseElement target = (BaseElement) messageFlow.getTarget();

                        if (target.equals(catchEvent)) {
                            String sourceName = getMessageFlowParticipantName(source, participants);
                            sbvrOutput.append("Es ist notwendig, dass das Intermediate Catch Event '")
                                    .append(sanitizeName(catchEvent.getName()))
                                    .append("' eine Nachricht von '")
                                    .append(sourceName)
                                    .append("' empfängt, bevor fortgeführt wird.\n");
                        }
                    }
                }
            }

            // Wenn das Ziel ein Event-Based Gateway und das nachfolgende Ziel ein Timer Event ist
            if (targetNode instanceof EventBasedGateway eventBasedGatewayTarget) {

                for (SequenceFlow innerFlow : eventBasedGatewayTarget.getOutgoing()) {
                    FlowNode innerTargetNode = innerFlow.getTarget();

                    // Überprüfe, ob das Ziel des EventBasedGateway ein TimerEvent ist
                    if (innerTargetNode instanceof IntermediateCatchEvent catchEvent) {
                        boolean isTimerEventInFlow = false;

                        for (EventDefinition eventDefinition : catchEvent.getEventDefinitions()) {
                            if (eventDefinition instanceof TimerEventDefinition) {
                                isTimerEventInFlow = true;

                                // Gibt SBVR-Regel für Timer Event nach EventBasedGateway aus
                                for (MessageFlow messageFlow : messageFlows) {
                                    BaseElement source = (BaseElement) messageFlow.getSource();
                                    BaseElement target = (BaseElement) messageFlow.getTarget();

                                    if (target.equals(catchEvent)) {
                                        String sourceName = getMessageFlowParticipantName(source, participants);
                                        sbvrOutput.append("Es ist notwendig, dass das Timer Event nach Event-Based Gateway '")
                                                .append(sanitizeName(catchEvent.getName()))
                                                .append("' eine Nachricht von '")
                                                .append(sourceName)
                                                .append("' empfängt, bevor fortgeführt wird.\n");
                                    }
                                }
                            }
                        }

                        // Falls es kein Timer Event ist, andere Events behandeln
                        if (!isTimerEventInFlow) {
                            for (MessageFlow messageFlow : messageFlows) {
                                BaseElement source = (BaseElement) messageFlow.getSource();
                                BaseElement target = (BaseElement) messageFlow.getTarget();

                                if (target.equals(catchEvent)) {
                                    String sourceName = getMessageFlowParticipantName(source, participants);
                                    sbvrOutput.append("Es ist notwendig, dass das Intermediate Catch Event '")
                                            .append(sanitizeName(catchEvent.getName()))
                                            .append("' eine Nachricht von '")
                                            .append(sourceName)
                                            .append("' empfängt, bevor fortgeführt wird.\n");
                                }
                            }
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
