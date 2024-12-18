package com.example.XMLIteration.Task;

import org.camunda.bpm.model.bpmn.instance.*;

import java.util.Collection;

import static com.example.XMLIteration.Task.Hilfsmethoden.sanitizeName;

public class StartEventTransformer {

    public String processStartEvent(FlowNode startEvent, String sourceRole, String targetRole, Collection<Participant> participants) {
        // Den Namen des StartEvents direkt aus dem BPMN-Modell extrahieren
        String startEventName = startEvent.getAttributeValue("name");

        // Überprüfen, ob der StartEventName existiert und nicht leer ist
        if (startEventName == null || startEventName.trim().isEmpty()) {
            return "Fehler: StartEvent hat keinen Namen.";
        }

        // Prüfen, ob das StartEvent eine MessageEventDefinition hat
        if (startEvent instanceof StartEvent) {
            StartEvent start = (StartEvent) startEvent;

            // Schleife durch Event-Definitionen
            for (EventDefinition eventDefinition : start.getEventDefinitions()) {
                if (eventDefinition instanceof MessageEventDefinition) {
                    // Verknüpfte MessageFlows analysieren
                    Collection<MessageFlow> messageFlows = start.getModelInstance().getModelElementsByType(MessageFlow.class);

                    for (MessageFlow messageFlow : messageFlows) {
                        BaseElement source = (BaseElement) messageFlow.getSource();
                        BaseElement target = (BaseElement) messageFlow.getTarget();

                        // Wenn das Ziel des MessageFlows das aktuelle StartEvent ist
                        if (target.equals(start)) {
                            // Extrahiere die Nachricht, die mit dem MessageFlow verbunden ist
                            Message message = messageFlow.getMessage();
                            String messageName = (message != null && message.getName() != null) ? message.getName() : "Unbekannte Nachricht";

                            // Hier verwenden wir 'participants' anstelle von 'lanes'
                            String sourceName = getMessageFlowParticipantName(source, participants);

                            // Generiere die SBVR-Ausgabe
                            return "Es ist erforderlich, dass der Prozess mit '"
                                    + sanitizeName(start.getName()) + "' startet, "
                                    + "wenn die Nachricht '"
                                    + messageName
                                    + "' von '"
                                    + sourceName
                                    + "' empfangen wird.";
                        }
                    }
                }
            }
        }



        // Standardausgabe, wenn keine MessageEventDefinition gefunden wird
        return "Es ist notwendig, dass der Prozess mit dem StartEvent '" + startEventName + "' startet.";
    }

    private String getMessageFlowParticipantName(BaseElement element, Collection<Participant> participants) {
        // Prüfe, ob das Element ein Participant ist
        if (element instanceof Participant) {
            Participant participant = (Participant) element;
            return participant.getName() != null ? participant.getName() : "Unbenannter Teilnehmer";
        }

        // Falls das Element kein Participant ist, durchsuche die Teilnehmer nach einem passenden Element
        for (Participant participant : participants) {
            if (participant.getProcess() != null && participant.getProcess().equals(element)) {
                return participant.getName() != null ? participant.getName() : "Unbenannter Teilnehmer";
            }
        }

        return "Unbekannte Quelle/Ziel";
    }
}
