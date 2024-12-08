package com.example.Task;

import com.example.Hilfsmethoden;
import com.example.Interfaces.FlowNodeTransformer;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.SBVRTransformerNEU.createStartEventStatement;

public class StartEventTransformer implements FlowNodeTransformer {

    @Override
    public String transformFlowNode(FlowNode startEvent, String sourceRole, String targetRole, Collection<Lane> lanes) {
        // Den Namen des StartEvents direkt aus dem BPMN-Modell extrahieren
        String startEventName = startEvent.getAttributeValue("name"); // Holt den Attributwert "name" des Start-Events

        // Überprüfen, ob der StartEventName existiert und nicht leer ist
        if (startEventName == null || startEventName.trim().isEmpty()) {
            return "Fehler: StartEvent hat keinen Namen.";
        }

        // Konvertiere die Collection der Ausgangsflüsse in eine Liste, um auf die Elemente per Index zuzugreifen
        List<SequenceFlow> outgoingFlows = new ArrayList<>(startEvent.getOutgoing());

        // Überprüfe, ob es mindestens einen Ausgangsfluss gibt
        if (!outgoingFlows.isEmpty()) {
            // Jetzt das SBVR-Statement mit der Methode createStartEventStatement generieren
            return createStartEventStatement(startEventName);
        } else {
            // Falls keine Ausgangsflüsse vorhanden sind, eine entsprechende Fehlermeldung oder Standardantwort zurückgeben
            return "Es ist notwendig, dass der Prozess mit dem Startevent '" + startEventName + "' startet. ";
        }
    }
}
