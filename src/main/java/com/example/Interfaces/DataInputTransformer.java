package com.example.Interfaces;

import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.DataInput;
import com.example.SBVRTransformerNEU;

import java.util.Collection;

public class DataInputTransformer implements FlowNodeTransformer {

    /**
     * Wandelt einen Data Input in eine SBVR-Regel um.
     * @param node Der FlowNode (Data Input), der transformiert werden soll
     * @param sourceRole Die Quelle der Rolle
     * @param targetRole Die Zielrolle
     * @param lanes Die Lanes, die für das Mapping benötigt werden
     * @return Die SBVR-Regel für den Data Input
     */
    @Override
    public String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes) {
        if (node instanceof DataInput) {
            // DataInput als DataInput casten und sicherstellen, dass es den richtigen Namen hat
            DataInput dataInput = (DataInput) node;

            // Name des DataInput extrahieren
            String dataName = dataInput.getName(); // Dies könnte eine andere Methode oder Eigenschaft sein, je nach Camunda API

            // Aufruf der Methode, um die SBVR-Regel zu erstellen
            return SBVRTransformerNEU.createDataInputStatement(dataName);
        }
        return "";  // Rückgabe eines leeren Strings, wenn das Node kein DataInput ist
    }
}