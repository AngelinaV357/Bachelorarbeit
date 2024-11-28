package com.example.Interfaces;

import org.camunda.bpm.model.bpmn.instance.DataObject;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import com.example.SBVRTransformerNEU;

import java.util.Collection;

public class DataObjectTransformer implements FlowNodeTransformer {

    /**
     * Wandelt ein Data Object in eine SBVR-Regel um.
     * @param node Der FlowNode (Data Object), der transformiert werden soll
     * @param sourceRole Die Quelle der Rolle
     * @param targetRole Die Zielrolle
     * @param lanes Die Lanes, die für das Mapping benötigt werden
     * @return Die SBVR-Regel für das Data Object
     */
    @Override
    public String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes) {
        if (node instanceof DataObject) {
            String dataName = node.getName();  // Hier nehmen wir an, dass DataObject ein Name-Feld hat
            return SBVRTransformerNEU.createDataObjectStatement(dataName);
        }
        return "";
    }
}