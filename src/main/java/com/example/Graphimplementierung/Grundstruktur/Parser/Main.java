package com.example.Graphimplementierung.Grundstruktur.Parser;
//https://github.com/stackmystack/SBVR-Parsing-Engine?search=1
//https://github.com/paudan/bpmn-sbvr-extraction/tree/master/src
import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Workflowpatterns.DataPatternFinder;
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
            File xmlFile = new File("src/main/resources/.bpmn");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            StringBuilder sbvrDataBuilder = new StringBuilder();

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
            gatewayPatternFinder.findParallelGatewayPatterns(graph, sbvrDataBuilder);
            gatewayPatternFinder.findExclusiveGatewayPatterns(graph, sbvrDataBuilder);
            gatewayPatternFinder.findEventBasedGatewayPatterns(graph, sbvrDataBuilder);

//             4. Task Pattern Finder initialisieren (Für alle Tasks)
            TaskPatternFinder taskPatternFinder = new TaskPatternFinder();
            taskPatternFinder.findAllTaskPatterns(graph, sbvrDataBuilder);
//            DataPatternFinder.generateDataEdgeRules(graph, sbvrDataBuilder);
//            taskPatternFinder.processMessageEdges(graph, sbvrDataBuilder);

//            // 5. SubProcess Pattern Finder initialisieren
//            SubProcessPatternFinder subProcessPatternFinder = new SubProcessPatternFinder();
//            subProcessPatternFinder.findSubProcessPatterns(graph, sbvrDataBuilder);

            SBVRFileSaver.saveSBVRToFile(sbvrDataBuilder.toString(), "generated_rules.sbvr");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methode zum Entfernen von Zeilenumbrüchen und Tabulatoren
    public static String cleanText(String text) {
        // Entfernen von Zeilenumbrüchen, Carriage-Returns und Tabulatoren
        return text.replaceAll("[\\r\\n\\t]", " ").trim();
    }
}
