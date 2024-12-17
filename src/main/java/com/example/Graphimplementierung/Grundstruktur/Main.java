package com.example.Graphimplementierung.Grundstruktur;

public class Main {

    public static void main(String[] args) {
        // Pfad zur XML-Datei
        String filePath = "src/main/resources/Car Wash Process.bpmn";

        // XMLParser erstellen und XML parsen
        XMLParser parser = new XMLParser();
        BPMNGraph graph = parser.parseXML(filePath);

        // Ausgabe der Knoten und Kanten des Graphen
        System.out.println("\nAlle Knoten im Graph:");
        graph.getNodes().forEach(node -> System.out.println(node));

        System.out.println("\nAlle Kanten im Graph:");
        graph.getEdges().forEach(edge -> System.out.println(edge));
    }
}
