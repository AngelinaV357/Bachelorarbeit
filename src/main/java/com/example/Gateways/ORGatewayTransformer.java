package com.example.Gateways;

import com.example.Interfaces.FlowNodeTransformer;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import com.example.main.SBVRTransformerNEU;

import java.util.*;

import static com.example.Hilfsmethoden.getName;
import static com.example.Hilfsmethoden.getRoleForNode;

public class ORGatewayTransformer implements FlowNodeTransformer {
    /**
     * Dieser Code transformiert einen Inklusiv-Gateway (OR-Gateway) Knoten aus einem BPMN-Diagramm in SBVR-konforme Aussagen.
     * Der `transformFlowNode`-Methoden nimmt einen FlowNode (Knoten) als Eingabe, prüft, ob es sich um einen Inklusiv-Gateway handelt,
     * und erzeugt daraufhin eine Sammlung von SBVR-Statements, die die verschiedenen Flüsse und ihre Bedingungen darstellen.
     * Es werden für alle ausgehenden Flüsse des Gateways Paare von Flüssen verglichen. Falls es mehrere ausgehende Flüsse gibt,
     * wird für jedes Paar von Flüssen ein SBVR-Statement zur Beschreibung des Flusses erstellt.
     * Zusätzlich wird für jedes Paar von Flüssen ein Inklusiv-Statement generiert, um die Bedingungen für die Ausführung der Flüsse zu beschreiben.
     */
    @Override
    public String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes) {
        // Stelle sicher, dass der Knoten ein Inklusiv-Gateway ist
        StringBuilder sbvrOutput = new StringBuilder();
        if (node instanceof InclusiveGateway gateway) {
            Set<String> uniqueStatements = new HashSet<>();
            StringBuilder sbvrStatements = new StringBuilder();

            List<SequenceFlow> outgoingFlows = new ArrayList<>(gateway.getOutgoing());
            String sourceName = getName(gateway.getIncoming().iterator().next().getSource());

            // Generiere Statements für alle ausgehenden Flows
            for (int i = 0; i < outgoingFlows.size(); i++) {
                for (int j = i + 1; j < outgoingFlows.size(); j++) {
                    SequenceFlow flow1 = outgoingFlows.get(i);
                    SequenceFlow flow2 = outgoingFlows.get(j);

                    String targetName1 = getName(flow1.getTarget());
                    String targetRole1 = getRoleForNode(flow1.getTarget(), lanes);
                    String condition1 = flow1.getName() != null ? flow1.getName() : "unbekannte Bedingung";

                    String targetName2 = getName(flow2.getTarget());
                    String targetRole2 = getRoleForNode(flow2.getTarget(), lanes);
                    String condition2 = flow2.getName() != null ? flow2.getName() : "unbekannte Bedingung";

                    // Erstelle SBVR-Statements für jedes Paar von Flüssen
                    String flowStatement1 = SBVRTransformerNEU.createInclusiveStatement(sourceRole, sourceName, targetRole1, targetName1, condition1);
                    if (uniqueStatements.add(flowStatement1)) {
                        sbvrStatements.append(flowStatement1);
                    }

                    String flowStatement2 = SBVRTransformerNEU.createInclusiveStatement(sourceRole, sourceName, targetRole2, targetName2, condition2);
                    if (uniqueStatements.add(flowStatement2)) {
                        sbvrStatements.append(flowStatement2);
                    }

                    // Regel: Mindestens eine der folgenden Aktivitäten muss wahr sein
                    String atLeastOneRule = "Es ist notwendig, dass mindestens '" + targetName1 + "' oder '" + targetName2 + "' wahr sind, wenn '" + condition1 + "' oder '" + condition2 + "' gilt.\n";
                    sbvrStatements.append(atLeastOneRule);
                }
            }

            sbvrOutput.append(sbvrStatements);
        }
        return sbvrOutput.toString();
    }
}
