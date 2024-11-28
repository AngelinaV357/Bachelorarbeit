package com.example.Interfaces;

import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.Lane;
import com.example.SBVRTransformerNEU;
import org.camunda.bpm.model.bpmn.instance.UserTask;

import java.util.Collection;

import static com.example.SBVRTransformerNEU.createUserTaskStatement;

public class UserTaskTransformer implements FlowNodeTransformer{

    /**
     * Wandelt einen UserTask in eine SBVR-Regel um.
     * @param node Der UserTask, der transformiert werden soll
     * @param sourceRole Die Quelle der Rolle
     * @param targetRole Die Zielrolle, die die Aufgabe ausführt
     * @param lanes Die Lanes, die für das Mapping benötigt werden
     * @return Die SBVR-Regel für den UserTask
     */
    @Override
    public String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes) {
        if (node instanceof UserTask) {
            String taskName = node.getName();  // Hier nehmen wir an, dass der UserTask ein Name-Feld hat
            return createUserTaskStatement(taskName, targetRole);
        }
        return "";
    }
}
