package com.example.Task;

import com.example.Interfaces.FlowNodeTransformer;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.Task;

import java.util.*;

import static com.example.Hilfsmethoden.getName;

public class ActivityTransformer implements FlowNodeTransformer {

    /**
     * Dieser Code transformiert einen Aktivitätsknoten aus einem BPMN-Diagramm in SBVR-konforme Aussagen.
     * Der `transformFlowNode`-Methoden nimmt einen FlowNode (Knoten) als Eingabe, prüft, ob es sich um eine Aktivität handelt,
     * und erzeugt daraufhin eine SBVR-Aussage, die die Bedingungen für die Aktivität beschreibt.
     */
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

            // Hole den Namen der Zielaktivität (Aktivität 1)
            String targetActivity = activityName;

            // Erstelle eine SBVR-Aussage für die Aktivität
            if (sourceActivity != null) {
                String activityStatement = "Es ist notwendig, dass " + targetRole + " " + targetActivity + " ausführt, wenn " + sourceRole + " " + sourceActivity + " ausführt.";
                sbvrOutput.append(activityStatement);
            }
        }

        return sbvrOutput.toString();
    }
}
