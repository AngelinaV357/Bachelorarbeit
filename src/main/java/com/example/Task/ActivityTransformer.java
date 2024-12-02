package com.example.Task;

import com.example.Interfaces.FlowNodeTransformer;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;

import java.util.*;

import static com.example.Hilfsmethoden.getName;
import static com.example.Hilfsmethoden.getRoleForNode;

public class ActivityTransformer implements FlowNodeTransformer {

    @Override
    public String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes) {
        StringBuilder sbvrOutput = new StringBuilder();

        // Stelle sicher, dass der Knoten eine Aufgabe (Aktivität) ist
        if (node instanceof Task task) {
            // Hole den Namen der Aktivität (Task)
            String activityName = getName(task);

            // Hole den ersten eingehenden Flow und bestimme die Quell-Aktivität
            String sourceActivity = null;
            if (!task.getIncoming().isEmpty()) {
                // Der erste eingehende Flow zur Aktivität (Aktivität 2)
                sourceActivity = getName(task.getIncoming().iterator().next().getSource());
            }

            // Hole die Rollen für die Quell- und Ziel-Aktivitäten
            String sourceActivityRole = getRoleForNode(task.getIncoming().iterator().next().getSource(), lanes);
            String targetActivityRole = getRoleForNode(task, lanes);

            // Überprüfen, ob das XOR-Gateway in der Quellaktivität vorhanden ist
            String gatewayName = null;
            if (task.getIncoming().iterator().next().getSource() instanceof ExclusiveGateway gateway) {
                gatewayName = gateway.getName();  // Holen des Namens des XOR-Gateways
            }

            // Erstelle eine SBVR-Aussage für die Aktivität
            if (sourceActivity != null) {
                String activityStatement;
                if (gatewayName != null) {
                    // Wenn ein XOR-Gateway vorhanden ist, füge den Namen des Gateways in die Aussage ein
                    activityStatement = "Es ist notwendig, dass " + targetActivityRole + " " + activityName + " ausführt, wenn " + sourceActivityRole + " " + sourceActivity + " ausführt und das XOR-Gateway '" + gatewayName + "' aktiv ist.";
                } else {
                    activityStatement = "Es ist notwendig, dass " + targetActivityRole + " " + activityName + " ausführt, wenn " + sourceActivityRole + " " + sourceActivity + " ausführt.";
                }
                sbvrOutput.append(activityStatement).append("\n");  // Absatz nach jedem Satz
            }
        }

        return sbvrOutput.toString();
    }
}
