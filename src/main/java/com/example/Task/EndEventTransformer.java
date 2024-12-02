package com.example.Task;

import com.example.Hilfsmethoden;
import com.example.Interfaces.FlowNodeTransformer;
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
            return createEndEventStatement(sourceRole, targetRole, targetName, endEventName);  // Übergibt die richtigen Parameter
        } else {
            // Falls keine Ausgangsflüsse vorhanden sind, prüfen wir, ob es sich um ein Endevent handelt
            // Bei einem EndEvent, das keine Ausgangsflüsse hat, könnte dies einfach das Ende des Prozesses sein
            return "Es ist notwendig, dass der Prozess mit dem Endevent " + endEventName + " endet.";
        }
    }
}