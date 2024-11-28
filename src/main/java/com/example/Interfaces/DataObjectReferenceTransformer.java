package com.example.Interfaces;

import org.camunda.bpm.model.bpmn.instance.DataObjectReference;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import com.example.SBVRTransformerNEU;

import java.util.Collection;

public class DataObjectReferenceTransformer implements FlowNodeTransformer {

    /**
     * Wandelt eine Datenobjekt-Referenz in eine SBVR-Regel um.
     * @param node Der FlowNode (Datenobjekt-Referenz), der transformiert werden soll
     * @param sourceRole Die Quelle der Rolle
     * @param targetRole Die Zielrolle
     * @param lanes Die Lanes, die für das Mapping benötigt werden
     * @return Die SBVR-Regel für die Datenobjekt-Referenz
     */
    @Override
    public String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes) {
        if (node instanceof DataObjectReference dataObjectReference) {

            // Extrahiere den Namen des Datenobjekts
            String dataName = dataObjectReference.getName();

            // Übergebe den Namen an die SBVR-Regel-Erstellung
            return SBVRTransformerNEU.createDataObjectReferenceStatement(dataName);
        }
        return "";
    }
}