package com.example.Interfaces;

import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import com.example.SBVRTransformerNEU;

import java.util.*;

import static com.example.Hilfsmethoden.getName;
import static com.example.Hilfsmethoden.getRoleForNode;

public class ANDGatewayTransformer implements FlowNodeTransformer {
    /**
     * Dieser Code transformiert einen Parallel-Gateway (AND-Gateway) Knoten aus einem BPMN-Diagramm in SBVR-konforme Aussagen.
     * Der `transformFlowNode`-Methoden nimmt einen FlowNode (Knoten) als Eingabe, prüft, ob es sich um einen Parallel-Gateway handelt,
     * und erzeugt daraufhin eine Sammlung von SBVR-Statements, die die verschiedenen Flüsse und ihre Bedingungen darstellen.
     * Es werden für alle ausgehenden Flüsse des Gateways Paare von Flüssen verglichen. Falls es mehrere ausgehende Flüsse gibt,
     * wird für jedes Paar von Flüssen ein SBVR-Statement zur Beschreibung des Flusses erstellt.
     * Zusätzlich wird für jedes Paar von Flüssen ein Ausschluss-Statement generiert, um zu verdeutlichen, dass diese Flüsse nicht gleichzeitig
     * durchlaufen werden können (logische "UND"-Bedingung).
     */
    @Override
    public String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes) {
        // Stelle sicher, dass der Knoten ein Parallel-Gateway ist
        StringBuilder sbvrOutput = null;
        StringBuilder sbvrStatements = null;
        if (node instanceof ParallelGateway gateway) {
            sbvrOutput = new StringBuilder();
            Set<String> processedGateways = new HashSet<>();

            List<SequenceFlow> outgoingFlows = new ArrayList<>(gateway.getOutgoing());
            Set<String> uniqueStatements = new HashSet<>();
            sbvrStatements = new StringBuilder();

            String gatewayId = gateway.getId();
            processedGateways.add(gatewayId);

            String gatewayName = getName(gateway);
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

                    String flowStatement1 = SBVRTransformerNEU.createFlowStatement(sourceRole, sourceName, targetRole1, targetName1, condition1);
                    if (uniqueStatements.add(flowStatement1)) {
                        sbvrStatements.append(flowStatement1);
                    }

                    String flowStatement2 = SBVRTransformerNEU.createFlowStatement(sourceRole, sourceName, targetRole2, targetName2, condition2);
                    if (uniqueStatements.add(flowStatement2)) {
                        sbvrStatements.append(flowStatement2);
                    }

                    String exclusionStatement = SBVRTransformerNEU.createParallelStatement(sourceName, targetRole1, targetName1, targetRole2, targetName2, gatewayName);
                    if (uniqueStatements.add(exclusionStatement)) {
                        sbvrStatements.append(exclusionStatement);
                    }
                }
            }
        }
        sbvrOutput.append(sbvrStatements);
        return sbvrOutput.toString();
    }
}
