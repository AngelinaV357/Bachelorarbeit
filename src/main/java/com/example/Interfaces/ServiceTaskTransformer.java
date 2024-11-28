package com.example.Interfaces;

import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.Lane;
import com.example.SBVRTransformerNEU;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;

import java.util.Collection;

public class ServiceTaskTransformer implements FlowNodeTransformer {

    /**
     * Diese Methode transformiert einen ServiceTask in eine SBVR-Regel.
     * Sie verwendet die Methode createServiceTaskStatement, um die SBVR-Regel zu generieren.
     * @param node Der ServiceTask (FlowNode)
     * @param sourceRole Die Quelle der Rolle, die den ServiceTask ausführt
     * @param targetRole Die Zielrolle, die den ServiceTask erwartet
     * @param lanes Die Lanes, die zur Zuweisung der Rollen genutzt werden
     * @return Die SBVR-Regel für den ServiceTask
     */
    @Override
    public String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes) {
        // Sicherstellen, dass der FlowNode ein ServiceTask ist
        if (node instanceof ServiceTask serviceTask) {
            // Den Namen des ServiceTasks extrahieren
            String taskName = serviceTask.getName();  // Annahme: getName() gibt den Namen des ServiceTasks zurück

            // SBVR-Regel generieren
            return SBVRTransformerNEU.createServiceTaskStatement(taskName, sourceRole, targetRole);
        }
        return ""; // Leere Rückgabe, falls der Node kein ServiceTask ist
    }
}
