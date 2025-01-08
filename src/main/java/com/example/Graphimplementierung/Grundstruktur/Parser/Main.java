package com.example.Graphimplementierung.Grundstruktur.Parser;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Workflowpatterns.GatewayPatternFinder;
import com.example.Graphimplementierung.Workflowpatterns.SubProcessPatternFinder;
import com.example.Graphimplementierung.Workflowpatterns.TaskPatternFinder;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. XML-Dokument parsen
            File xmlFile = new File("src/main/resources/Receipt of Good.bpmn");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // 2. BPMN-Graph initialisieren
            BPMNGraph graph = new BPMNGraph();
            XMLParser parser = new XMLParser();
            parser.parseXML(doc, graph); // Alle Knoten und Kanten hinzufügen

            // 3. Ausgabe aller Knoten im Graphen
            System.out.println("\nAlle Knoten im Graph:");
            graph.getNodes().forEach(node -> System.out.println(cleanText(node.toString())));

            // 4. Ausgabe aller Kanten im Graphen
            System.out.println("\nAlle Kanten im Graph:");
            graph.getEdges().forEach(edge -> System.out.println(cleanText(edge.toString())));

            // 3. Gateway Pattern Finder initialisieren
            GatewayPatternFinder gatewayPatternFinder = new GatewayPatternFinder();
            gatewayPatternFinder.findExclusiveGatewayPatterns(graph);  // Nur einmal aufrufen
            gatewayPatternFinder.findParallelGatewayPatterns(graph);   // Nur einmal aufrufen
            gatewayPatternFinder.findEventBasedGatewayPatterns(graph);  // EventBased Gateway

            // 4. Task Pattern Finder initialisieren (Für alle Tasks)
            TaskPatternFinder taskPatternFinder = new TaskPatternFinder();
            taskPatternFinder.findAllTaskPatterns(graph);  // Alle Task-Typen auf einmal finden

            // 5. SubProcess Pattern Finder initialisieren
            SubProcessPatternFinder subProcessPatternFinder = new SubProcessPatternFinder();
            subProcessPatternFinder.findSubProcessPatterns(graph);  // SubProzess-Muster finden

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methode zum Entfernen von Zeilenumbrüchen und Tabulatoren
    private static String cleanText(String text) {
        // Entfernen von Zeilenumbrüchen, Carriage-Returns und Tabulatoren
        return text.replaceAll("[\\r\\n\\t]", " ").trim();
    }
}
