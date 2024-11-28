package com.example.Interfaces;

import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import com.example.SBVRTransformerNEU;

import java.util.*;

import static com.example.Hilfsmethoden.getName;
import static com.example.Hilfsmethoden.getRoleForNode;

public class XORGatewayTransformer implements FlowNodeTransformer {

    @Override
    public String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes) {
        StringBuilder sbvrOutput = new StringBuilder();
        Set<String> processedFlows = new HashSet<>(); // Set zum Speichern bereits verarbeiteter Flüsse

        if (node instanceof ExclusiveGateway gateway) {
            List<SequenceFlow> outgoingFlows = new ArrayList<>(gateway.getOutgoing());

            // Verarbeite jeden ausgehenden Fluss des XOR-Gateways
            for (SequenceFlow flow : outgoingFlows) {
                String targetRoleCurrent = getRoleForNode(flow.getTarget(), lanes);
                String targetNameCurrent = getName(flow.getTarget());
                String condition = flow.getName() != null ? flow.getName() : "unbekannte Bedingung";

                // Generiere eine eindeutige ID für diesen Fluss (Source, Target, und Condition)
                String flowIdentifier = String.format("%s-%s-%s-%s", sourceRole, getName(flow.getSource()), targetRoleCurrent, condition);

                // Wenn dieser Fluss noch nicht verarbeitet wurde
                if (!processedFlows.contains(flowIdentifier)) {
                    // Füge diesen Fluss zur Liste der verarbeiteten Flüsse hinzu
                    processedFlows.add(flowIdentifier);

                    // Erstelle die SBVR-Aussage für diesen Fluss
                    String flowStatement = SBVRTransformerNEU.createFlowStatement(sourceRole, getName(flow.getSource()), targetRoleCurrent, targetNameCurrent, condition);
                    sbvrOutput.append(flowStatement).append("\n");

                    // Sonderfall für Exklusionsbedingungen
                    if (outgoingFlows.size() > 1) {
                        for (int i = 0; i < outgoingFlows.size(); i++) {
                            // Überprüfe, ob der Fluss miteinander ausgeschlossen werden sollte
                            SequenceFlow otherFlow = outgoingFlows.get(i);
                            if (!otherFlow.equals(flow)) {
                                String exclusionStatement = SBVRTransformerNEU.createExclusionStatement(getName(flow.getSource()), targetRoleCurrent, targetNameCurrent, targetRole, getName(otherFlow.getTarget()));
                                if (!processedFlows.contains(exclusionStatement)) {
                                    processedFlows.add(exclusionStatement);
                                    sbvrOutput.append(exclusionStatement).append("\n");
                                }
                            }
                        }
                    }
                }
            }
        }

        return sbvrOutput.toString();
    }
}