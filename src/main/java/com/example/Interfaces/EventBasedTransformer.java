package com.example.Interfaces;

import com.example.Hilfsmethoden;
import com.example.SBVRTransformerNEU;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.util.*;

import static com.example.Hilfsmethoden.getName;

public class EventBasedTransformer implements FlowNodeTransformer {

    /**
     * Transformiert ein Event-Based Gateway in SBVR-Aussagen basierend auf den ausgehenden Flüssen.
     * @param gateway   Der FlowNode, der das Event-Based Gateway repräsentiert
     * @param sourceRole   Die Rolle des Quellknotens.
     * @param targetRole   Die Rolle des Zielknotens.
     * @param lanes     Eine Collection von Lanes, die für die Bestimmung von Rollen verwendet werden können.
     * @return          Eine String-Darstellung der SBVR-Aussagen, die das Event-Based Gateway repräsentieren.
     */
    @Override
    public String transformFlowNode(FlowNode gateway, String sourceRole, String targetRole, Collection<Lane> lanes) {
        // Konvertiere die Collection der Ausgangsflüsse in eine Liste, um auf die Elemente per Index zuzugreifen
        List<SequenceFlow> outgoingFlows = new ArrayList<>(gateway.getOutgoing());
        Set<String> uniqueStatements = new HashSet<>(); // Set zur Duplikatprüfung
        StringBuilder sbvrStatements = new StringBuilder();

        // Iteriere über alle ausgehenden Flows
        for (SequenceFlow flow : outgoingFlows) {
            String targetName = getName(flow.getTarget());
            String eventType = Hilfsmethoden.getEventType(flow.getTarget()); // Methode, um den Ereignistyp zu bestimmen (Nachricht, Timer, Bedingung)

            // Generiere die SBVR-Aussage für den aktuellen Flow
            String eventStatement = SBVRTransformerNEU.createEventGatewayStatement(targetName, eventType);

            // Füge die Aussage hinzu, wenn sie noch nicht existiert
            if (!uniqueStatements.contains(eventStatement)) {
                uniqueStatements.add(eventStatement);
                sbvrStatements.append(eventStatement).append("\n");
            }
        }
        return sbvrStatements.toString();
    }
}

