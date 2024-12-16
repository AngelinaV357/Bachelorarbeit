package com.example.Graphimplementierung;

public class Main {
    public static void main(String[] args) {
        // Erstellen eines neuen BPMN-Diagramms
        BPMNGraph graph = new BPMNGraph();

        // Erstellen von Knoten (Aktivitäten, Gateways, Ereignisse)
        Node startEvent = new EventNode("StartEvent1", null, "Start");
        Node task1 = new ActivityNode("Task1", "Lane1", "UserTask");
        Node gateway1 = new GatewayNode("Gateway1", "Lane1", "ExclusiveGateway");
        Node task2 = new ActivityNode("Task2", "Lane1", "ServiceTask");
        Node endEvent = new EventNode("EndEvent1", null, "End");

        // Hinzufügen der Knoten zum Graphen
        graph.addNode(startEvent);
        graph.addNode(task1);
        graph.addNode(gateway1);
        graph.addNode(task2);
        graph.addNode(endEvent);

        // Erstellen von Kanten (Verbindungen zwischen Knoten)
        Edge edge1 = new Edge("Edge1", startEvent, task1, null);
        Edge edge2 = new Edge("Edge2", task1, gateway1, null);
        Edge edge3 = new Edge("Edge3", gateway1, task2, "Condition1");
        Edge edge4 = new Edge("Edge4", task2, endEvent, null);

        // Hinzufügen der Kanten zum Graphen
        graph.addEdge(edge1);
        graph.addEdge(edge2);
        graph.addEdge(edge3);
        graph.addEdge(edge4);

        // Ausgabe des Graphen (Knoten und Kanten)
        System.out.println("Knoten im Graph:");
        for (Node node : graph.getNodes()) {
            System.out.println("ID: " + node.getId() + ", Typ: " + node.getType() + ", Lane: " + node.getLane());
        }

        System.out.println("\nKanten im Graph:");
        for (Edge edge : graph.getEdges()) {
            System.out.println("Edge ID: " + edge.getId() + ", Quelle: " + edge.getSource().getId() + ", Ziel: " + edge.getTarget().getId() + ", Bedingung: " + edge.getCondition());
        }
    }
}
