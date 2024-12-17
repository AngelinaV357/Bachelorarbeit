package com.example.Graphimplementierung.Workflowpatterns;

import com.example.Graphimplementierung.Grundstruktur.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Edge;
import com.example.Graphimplementierung.Grundstruktur.Nodes.ActivityNode;

public class WorkflowPatternAnalyzer {

    private BPMNGraph graph;

    public WorkflowPatternAnalyzer(BPMNGraph graph) {
        this.graph = graph;
    }

    // Methode zur Erkennung von XOR-Gateways und Generierung der entsprechenden SBVR-Regeln
    public String analyzeXORGateways() {
        StringBuilder sbvrRules = new StringBuilder();

        // Gehe alle Kanten im Graphen durch
        for (Edge edge : graph.getEdges()) {
            // Überprüfe, ob der Edge-Source und Target Node ein XOR-Gateway sind
            if (edge.getSource() instanceof ActivityNode && edge.getTarget() instanceof ActivityNode) {
                ActivityNode sourceNode = (ActivityNode) edge.getSource();
                ActivityNode targetNode = (ActivityNode) edge.getTarget();

                // Überprüfe, ob das Gateway ein exklusives Gateway (XOR) ist
                if (isXORGateway(sourceNode)) {
                    // Generiere SBVR-Regel für das XOR-Gateway
                    String outgoingName = targetNode.getName();
                    String incomingNode = sourceNode.getName();
                    String condition = edge.hasCondition() ? edge.getCondition() : "keine";

                    sbvrRules.append("Es ist obligatorisch, dass die Aktivität ")
                            .append(outgoingName)
                            .append(" nach der Aktivität ")
                            .append(incomingNode)
                            .append(" ausgeführt wird, wenn die Bedingung '")
                            .append(condition)
                            .append("' erfüllt ist.\n");

                    // Ausgabe der Kanten, die das XOR-Gateway betreffen
                    sbvrRules.append("Die Kante von ")
                            .append(sourceNode.getName())
                            .append(" zu ")
                            .append(targetNode.getName())
                            .append(" hat die Bedingung: ")
                            .append(condition)
                            .append("\n");
                }
            }
        }
        return sbvrRules.toString();
    }

    // Hilfsmethode zur Identifizierung eines XOR-Gateways anhand des activityType und des Namens
    private boolean isXORGateway(ActivityNode node) {
        // Ein XOR-Gateway ist entweder ein Gateway mit dem Typ "ExclusiveGateway"
        return node.getActivityType().equals("Gateway") && node.getName().contains("ExclusiveGateway");
    }

    // Methode zur Ausgabe aller SBVR-Regeln im Graph
    public void printSBVRRules() {
        String sbvrRules = analyzeXORGateways();
        System.out.println("SBVR-Regeln:\n" + sbvrRules);
    }

    // Methode zur Analyse von anderen Workflow-Patterns kann ebenfalls hinzugefügt werden
}
