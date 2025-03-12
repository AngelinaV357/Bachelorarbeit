package com.example.Graphimplementierung.Grundstruktur.Parser;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Workflowpatterns.DataPatternFinder;
import com.example.Graphimplementierung.Workflowpatterns.GatewayPatternFinder;
import com.example.Graphimplementierung.Workflowpatterns.TaskPatternFinder;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. XML-Dokument parsen
            File xmlFile = new File("src/main/resources/Procurement of Work Equipment.bpmn");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            StringBuilder sbvrDataBuilder = new StringBuilder();

            // 2. BPMN-ModelInstance laden
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(new FileInputStream(xmlFile));

            // 3. BPMN-Graph initialisieren
            BPMNGraph graph = new BPMNGraph();
            XMLParser parser = new XMLParser();

            // 4. XML-Daten mit ModelInstance verarbeiten
            parser.parseXML(doc, graph, modelInstance);

            // 3. Ausgabe aller Knoten im Graphen
            System.out.println("\nAlle Knoten im Graph:");
            graph.getNodes().forEach(node -> System.out.println(cleanText(node.toString())));

            // 4. Ausgabe aller Kanten im Graphen
            System.out.println("\nAlle Kanten im Graph:");
            graph.getEdges().forEach(edge -> System.out.println(cleanText(edge.toString())));

            System.out.println("\nStarte mit Commencement on Creation Pattern...");
            findCommencementonCreation(graph, sbvrDataBuilder);

            System.out.println("Starte mit Gateway-Pattern-Suche...");
            findGateways(graph, sbvrDataBuilder);

            System.out.println("\nStarte mit Data-Pattern-Suche...");
            findDataPatterns(graph, sbvrDataBuilder);

            System.out.println("\nStarte mit Task-Pattern-Suche...");
            findTasks(graph, sbvrDataBuilder);

            System.out.println("\nStarte mit Intermediate Event Pattern-Suche...");
            findIntermediateEvents(graph, sbvrDataBuilder);

            System.out.println("\nStarte mit Chained Execution Pattern...");
            findChainedExecution(graph, sbvrDataBuilder);

            System.out.println("\nStarte mit Boundary Event Pattern-Suche...");
            findBoundaryEvents(graph, sbvrDataBuilder);

            System.out.println("\nStarte mit Sequence Pattern-Suche...");
            findSequences(graph, sbvrDataBuilder);

            System.out.println("\nStarte mit Subprozess Pattern-Suche...");
            findSubProcesses(graph, sbvrDataBuilder);

            // Füge hier die Aufrufe für Explicit Termination und Commencement on Creation Pattern hinzu
            System.out.println("\nStarte mit Explicit Termination Pattern...");
            findExplicitTerminationPattern(graph, sbvrDataBuilder);

            // 10. Speichern der SBVR-Daten
            SBVRFileSaver.saveSBVRToFile(sbvrDataBuilder.toString(), "generated_rules.sbvr");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methode für die Gateway Pattern-Suche
    private static void findGateways(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        GatewayPatternFinder gatewayPatternFinder = new GatewayPatternFinder();
        gatewayPatternFinder.findParallelGatewayPatterns(graph, sbvrDataBuilder);
        gatewayPatternFinder.findExclusiveGatewayPatterns(graph, sbvrDataBuilder);
        gatewayPatternFinder.findEventBasedGatewayPatterns(graph, sbvrDataBuilder);
    }

    // Methode für die Data Pattern-Suche
    private static void findDataPatterns(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        DataPatternFinder.generateDataEdgeRules(graph, sbvrDataBuilder);
    }

    // Methode für die Task Pattern-Suche
    private static void findTasks(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        TaskPatternFinder taskPatternFinder = new TaskPatternFinder();
        taskPatternFinder.processTaskNode(graph, sbvrDataBuilder);
    }

    // Methode für die Boundary Event Pattern-Suche
    private static void findBoundaryEvents(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        TaskPatternFinder taskPatternFinder = new TaskPatternFinder();
        taskPatternFinder.processBoundaryEvent(graph, sbvrDataBuilder); // Boundary Event Pattern suchen
    }

    // Methode für die Sequence Pattern-Suche
    private static void findSequences(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        TaskPatternFinder taskPatternFinder = new TaskPatternFinder();
        taskPatternFinder.processSequenceTask(graph, sbvrDataBuilder);
    }

    // Methode für das Finden des Explicit Termination Patterns (EndEvent)
    private static void findExplicitTerminationPattern(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        TaskPatternFinder taskPatternFinder = new TaskPatternFinder();
        taskPatternFinder.processIncomingEdgesForEndEvent(graph, sbvrDataBuilder);

    }
    private static void findCommencementonCreation(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        TaskPatternFinder taskPatternFinder = new TaskPatternFinder();
        taskPatternFinder.processOutgoingEdgesForStartEvent(graph, sbvrDataBuilder);
    }

    // Methode für das Finden von Intermediate Event Patterns
    private static void findIntermediateEvents(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        TaskPatternFinder taskPatternFinder = new TaskPatternFinder();
        taskPatternFinder.processIntermediateEvents(graph, sbvrDataBuilder);
    }

    private static void findChainedExecution(BPMNGraph graph, StringBuilder sbvrDataBuilder){
        TaskPatternFinder taskPatternFinder = new TaskPatternFinder();
        taskPatternFinder.processChainedExecution(graph, sbvrDataBuilder);
    }

    // Methode für das Finden von Subprozessen (neue Methode für Subprozess Pattern)
    private static void findSubProcesses(BPMNGraph graph, StringBuilder sbvrDataBuilder) {
        TaskPatternFinder taskPatternFinder = new TaskPatternFinder();
        taskPatternFinder.processSubProcess(graph, sbvrDataBuilder);
    }

    // Methode zum Entfernen von Zeilenumbrüchen und Tabulatoren
    public static String cleanText(String text) {
        return text.replaceAll("[\\r\\n\\t]", " ").trim();
    }
}
