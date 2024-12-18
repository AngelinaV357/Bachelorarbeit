package com.example.XMLIteration.Task.Interfaces;

import org.camunda.bpm.model.bpmn.instance.*;

import java.util.Collection;

/**
 * Interface für die Transformation eines FlowNodes in eine SBVR-Regel.
 * Wird von spezifischen Transformern für verschiedene FlowNode-Typen implementiert.
 */
public interface FlowNodeTransformer {
    /**
     * Wandelt einen FlowNode in eine SBVR-Regel um.
     * @param node Der FlowNode, der transformiert werden soll
     * @param sourceRole Die Quelle der Rolle
     * @param targetRole Die Zielrolle
     * @param lanes Die Lanes, die für das Mapping benötigt werden
     * @return Eine SBVR-Regel als String
     */
    String transformFlowNode(FlowNode node, String sourceRole, String targetRole, Collection<Lane> lanes);
}