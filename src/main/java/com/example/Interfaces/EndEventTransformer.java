package com.example.Interfaces;

import com.example.Hilfsmethoden;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.SBVRTransformerNEU.createEndEventStatement;

public class EndEventTransformer implements FlowNodeTransformer {

    @Override
    public String transformFlowNode(FlowNode endEvent, String sourceRole, String targetRole, Collection<Lane> lanes) {
        // Den Namen des EndEvents extrahieren
        String endEventName = Hilfsmethoden.getName(endEvent);  // Aufruf der Hilfsmethode getName

        // Konvertiere die Collection der Ausgangsflüsse in eine Liste, um auf die Elemente per Index zuzugreifen
        List<SequenceFlow> outgoingFlows = new ArrayList<>(endEvent.getOutgoing());

        // Überprüfe, ob es mindestens einen Ausgangsfluss gibt
        if (!outgoingFlows.isEmpty()) {
            // Bestimmt den Namen des Zielknotens (z.B. eine Aktivität oder ein anderes Event)
            String targetName = Hilfsmethoden.getName(outgoingFlows.get(0).getTarget());

            // Jetzt das SBVR-Statement mit der Methode createEndEventStatement generieren
            return createEndEventStatement((EndEvent) endEvent, sourceRole, targetRole);
        } else {
            // Falls keine Ausgangsflüsse vorhanden sind, eine entsprechende Fehlermeldung oder Standardantwort zurückgeben
            return "Fehler: EndEvent hat keine Ausgangsflüsse.";
        }
    }
}
