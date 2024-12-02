package com.example.Task;

import com.example.Hilfsmethoden;
import com.example.Interfaces.FlowNodeTransformer;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.SBVRTransformerNEU.createEndEventStatement;

public class EndEventTransformer implements FlowNodeTransformer {

    @Override
    public String transformFlowNode(FlowNode endEvent, String sourceRole, String targetRole, Collection<Lane> lanes) {
        String endEventName = Hilfsmethoden.getName(endEvent);
        List<SequenceFlow> outgoingFlows = new ArrayList<>(endEvent.getOutgoing());
        if (!outgoingFlows.isEmpty()) {
            String targetName = Hilfsmethoden.getName(outgoingFlows.get(0).getTarget());
            return createEndEventStatement(sourceRole, targetRole, targetName, endEventName);
        } else {
            return "Es ist notwendig, dass der Prozess mit dem Endevent " + endEventName + " endet.";
        }
    }
}