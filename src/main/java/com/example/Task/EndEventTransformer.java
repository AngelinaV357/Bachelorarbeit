package com.example.Task;

import com.example.Interfaces.FlowNodeTransformer;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.Lane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EndEventTransformer implements FlowNodeTransformer {

    @Override
    public String transformFlowNode(FlowNode endEvent, String sourceRole, String targetRole, Collection<Lane> lanes) {
        // Den Namen des EndEvents aus dem BPMN-Modell extrahieren
        String endEventName = endEvent.getAttributeValue("name");

        // Wenn der EndEventName leer ist, einen Standardnamen setzen
        if (endEventName == null || endEventName.trim().isEmpty()) {
            endEventName = "unbenanntes Endevent";
        }

        // Konvertiere die Collection der eingehenden Flüsse in eine Liste
        List<SequenceFlow> incomingFlows = new ArrayList<>(endEvent.getIncoming());

        // Standardwerte für Ausgabe setzen
        String precedingNodeName = "unbekannter Vorgängerknoten";
        String precedingCondition = "keine spezifische Bedingung";
        String result;

        // Überprüfen, ob es eingehende Flüsse gibt
        if (!incomingFlows.isEmpty()) {
            // Hole den Quellknoten des ersten eingehenden Flusses
            FlowNode sourceNode = incomingFlows.get(0).getSource();

            // Fall 1: Vorgänger ist ein Gateway (z. B. ExclusiveGateway)
            if (sourceNode instanceof ExclusiveGateway gateway) {
                precedingNodeName = gateway.getAttributeValue("name") != null ? gateway.getAttributeValue("name") : "unbenanntes Gateway";

                // Gehe durch die ausgehenden Flüsse des Gateways
                for (SequenceFlow flow : gateway.getOutgoing()) {
                    // Prüfen, ob der aktuelle Flow das EndEvent erreicht
                    if (flow.getTarget() == endEvent) {
                        // Extrahiere die Bedingung aus dem Namen des Flows
                        precedingCondition = flow.getName() != null ? flow.getName() : "unbekannte Bedingung";
                        break;
                    }
                }
                result = "Es ist notwendig, dass der Prozess mit dem EndEvent '" + endEventName
                        + "' endet, wenn das Gateway '" + precedingNodeName
                        + "' mit der Bedingung '" + precedingCondition + "' ausgewählt wird.";
            }
            // Fall 2: Vorgänger ist eine Aktivität
            else {
                // Extrahiere den Namen der Aktivität
                precedingNodeName = sourceNode.getAttributeValue("name") != null ? sourceNode.getAttributeValue("name") : "unbenannte Aktivität";
                result = "Es ist notwendig, dass der Prozess mit dem EndEvent '" + endEventName + "' endet, nachdem die Aktivität '" + precedingNodeName + "' abgeschlossen wurde.\n";
            }
        } else {
            // Kein Vorgängerknoten vorhanden
            result = "Der Prozess endet mit dem Endevent '" + endEventName + "'\n.";
        }

        return result.replaceAll("[\\n\\r]+", " ").trim();
    }
}
