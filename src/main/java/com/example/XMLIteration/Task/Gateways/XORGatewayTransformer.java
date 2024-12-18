package com.example.XMLIteration.Task.Gateways;

import com.example.XMLIteration.Task.Interfaces.FlowNodeTransformer;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import com.example.XMLIteration.Task.main.SBVRTransformerNEU;

import java.util.*;

import static com.example.XMLIteration.Task.Hilfsmethoden.getName;
import static com.example.XMLIteration.Task.Hilfsmethoden.getRoleForNode;

public class XORGatewayTransformer implements FlowNodeTransformer {

    @Override
    public String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes) {
        StringBuilder sbvrOutput = new StringBuilder();
        StringBuilder sbvrStatements = new StringBuilder();

        // Nur wenn der Knoten ein ExclusiveGateway ist
        if (node instanceof ExclusiveGateway gateway) {
            List<SequenceFlow> outgoingFlows = new ArrayList<>(gateway.getOutgoing());
            Set<String> uniqueStatements = new HashSet<>();

            String sourceName = getName(gateway.getIncoming().iterator().next().getSource());

            // Überprüfen, ob es genau zwei ausgehende Flüsse gibt
            if (outgoingFlows.size() == 2) {
                String targetRole1 = getRoleForNode(outgoingFlows.get(0).getTarget(), lanes);
                String targetRole2 = getRoleForNode(outgoingFlows.get(1).getTarget(), lanes);

                String targetName1 = getName(outgoingFlows.get(0).getTarget());
                String targetName2 = getName(outgoingFlows.get(1).getTarget());

                String condition1 = outgoingFlows.get(0).getName() != null ? outgoingFlows.get(0).getName() : "unbekannte Bedingung";
                String condition2 = outgoingFlows.get(1).getName() != null ? outgoingFlows.get(1).getName() : "unbekannte Bedingung";

                // Erzeuge Flow-Statements
                String flowStatement1 = SBVRTransformerNEU.createFlowStatement(sourceRole, sourceName, targetRole1, targetName1, condition1);
                uniqueStatements.add(flowStatement1);
                sbvrStatements.append(flowStatement1);

                String flowStatement2 = SBVRTransformerNEU.createFlowStatement(sourceRole, sourceName, targetRole2, targetName2, condition2);
                if (uniqueStatements.add(flowStatement2)) {
                    sbvrStatements.append(flowStatement2);
                }
            }
        } else {
            sbvrStatements.append("Der Knoten ist kein ExclusiveGateway.\n");
        }

        sbvrOutput.append(sbvrStatements);
        return sbvrOutput.toString();
    }
}
