package com.example.Interfaces;

import org.camunda.bpm.model.bpmn.instance.*;

import java.util.Collection;

public interface FlowNodeTransformer {
    String  transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes);
}
