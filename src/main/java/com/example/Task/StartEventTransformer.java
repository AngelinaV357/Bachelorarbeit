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
        // Den Namen des StartEvents extrahieren
        String startEventName = Hilfsmethoden.getName(startEvent);  // Aufruf der Hilfsmethode getName

        // Konvertiere die Collection der Ausgangsflüsse in eine Liste, um auf die Elemente per Index zuzugreifen
        List<SequenceFlow> outgoingFlows = new ArrayList<>(startEvent.getOutgoing());

        // Überprüfe, ob es mindestens einen Ausgangsfluss gibt
        if (!outgoingFlows.isEmpty()) {
            // Bestimmt den Namen des Zielknotens (z.B. eine Aktivität oder ein anderes Event)
            String targetName = Hilfsmethoden.getName(outgoingFlows.get(0).getTarget());

            // Jetzt das SBVR-Statement mit der Methode createStartEventStatement generieren
            return createStartEventStatement(sourceRole, startEventName, targetRole, targetName);
        } else {
            // Falls keine Ausgangsflüsse vorhanden sind, eine entsprechende Fehlermeldung oder Standardantwort zurückgeben
            return "Fehler: StartEvent hat keine Ausgangsflüsse.";
        }
    }
}
