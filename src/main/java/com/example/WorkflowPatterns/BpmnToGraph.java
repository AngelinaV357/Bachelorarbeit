package com.example.WorkflowPatterns;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.nio.graphml.GraphMLImporter;

import java.io.FileReader;
import java.io.IOException;

public class BpmnToGraph {

    public static Graph<String, DefaultEdge> createGraphFromXML(String filePath) {
        // Erstelle den Graphen
        Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        // Erstelle den GraphML-Importer
        GraphMLImporter<String, DefaultEdge> importer = new GraphMLImporter<>();

        // Konfiguration des Importers (optional: bei Bedarf Schlüssel oder Attribute hinzufügen)
        importer.setVertexFactory(id -> id); // Verwende die IDs aus der XML-Datei als Knoten-IDs

        // Importiere die XML-Datei in den Graphen
        try (FileReader fileReader = new FileReader(filePath)) {
            importer.importGraph(graph, fileReader);
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Datei: " + e.getMessage());
        }

        return graph;
    }

    public static void main(String[] args) {
        String filePath = "\"src/main/resources/May_combine_ingredients.bpmn\""; // Pfad zur XML-Datei
        Graph<String, DefaultEdge> graph = createGraphFromXML(filePath);

        // Teste den importierten Graphen
        System.out.println("Knoten: " + graph.vertexSet());
        System.out.println("Kanten: " + graph.edgeSet());
    }
}
