package com.example.Interfaces;

import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.Lane;
import com.example.SBVRTransformerNEU;

import java.util.Collection;

public class SendTaskTransformer implements FlowNodeTransformer {

    /**
     * Transformiert einen Send Task aus einem BPMN-Diagramm und erstellt die entsprechende SBVR-Regel.
     * @param node Der FlowNode, der der Send Task ist
     * @param sourceRole Die Rolle des Senders
     * @param targetRole Die Rolle des Empfängers
     * @param lanes Eine Sammlung von Lanes, die für die Zuweisung von Rollen relevant sein können
     * @return Die generierte SBVR-Regel
     */
    @Override
    public String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes) {
        StringBuilder sbvrOutput = new StringBuilder();

        // Überprüfen, ob der FlowNode ein SendTask ist
        if (node instanceof SendTask sendTask) {
            // Den Namen des SendTasks extrahieren
            String taskName = sendTask.getName() != null ? sendTask.getName() : "unbekannte Aufgabe";

            // Da wir die Rollen des Senders und des Empfängers als Parameter übergeben bekommen,
            // können wir diese direkt nutzen und die SBVR-Regel generieren.
            String sendTaskStatement = SBVRTransformerNEU.createSendTaskStatement(taskName, sourceRole, targetRole);

            // Die SBVR-Regel zum Ausgabe-String hinzufügen
            sbvrOutput.append(sendTaskStatement);
        }

        return sbvrOutput.toString();
    }
}