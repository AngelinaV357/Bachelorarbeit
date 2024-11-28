package com.example.Interfaces;

import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import com.example.SBVRTransformerNEU;

import java.util.Collection;

public class ServiceTaskTransformer implements FlowNodeTransformer {

    /**
     * Transformiert einen ServiceTask in eine SBVR-Regel.
     * Überprüft, ob der übergebene FlowNode ein ServiceTask ist, und ruft die Methode
     * createServiceTaskStatement auf, um die entsprechende SBVR-Regel zu erstellen.
     *
     * @param node Der FlowNode, der möglicherweise ein ServiceTask ist.
     * @param sourceRole Die Rolle, die den ServiceTask ausführt.
     * @param targetRole Die Rolle, die das Ergebnis des ServiceTasks erwartet.
     * @param lanes Die Lanes zur Zuweisung der Rollen.
     * @return Die generierte SBVR-Regel oder eine leere Zeichenkette, falls der FlowNode kein ServiceTask ist.
     */
    @Override
    public String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes) {
        // Prüfen, ob der FlowNode ein ServiceTask ist
        if (node instanceof ServiceTask) {
            ServiceTask serviceTask = (ServiceTask) node;

            // Den Namen des ServiceTasks extrahieren
            String taskName = serviceTask.getName();
            if (taskName == null || taskName.isEmpty()) {
                return "Der ServiceTask hat keinen Namen. Transformation nicht möglich.";
            }

            // SBVR-Regel generieren
            return SBVRTransformerNEU.createServiceTaskStatement(taskName, sourceRole, targetRole);
        }

        // Rückgabe, falls der FlowNode kein ServiceTask ist
        return "Der übergebene FlowNode ist kein ServiceTask.";
    }
}