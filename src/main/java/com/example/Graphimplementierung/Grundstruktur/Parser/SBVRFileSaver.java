package com.example.Graphimplementierung.Grundstruktur.Parser;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Workflowpatterns.TaskPatternFinder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SBVRFileSaver {

    // Methode zum Speichern der generierten SBVR-Daten in eine Datei
    public static void saveSBVRToFile(String sbvrData, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(sbvrData);
            System.out.println("SBVR-Daten wurden erfolgreich in die Datei gespeichert: " + filePath);
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der SBVR-Daten in die Datei: " + e.getMessage());
        }
    }

    // Beispielaufruf innerhalb der Main-Methode
    public static void main(String[] args) {
        // Erstelle eine Instanz von TaskPatternFinder und BPMNGraph
        TaskPatternFinder taskPatternFinder = new TaskPatternFinder();
        BPMNGraph graph = new BPMNGraph(); // Initialisiere deine BPMNGraph-Instanz korrekt

        // StringBuilder zum Sammeln der SBVR-Daten
        StringBuilder sbvrDataBuilder = new StringBuilder();

        // SBVR-Daten durch die Verarbeitung von Tasks im Graph generieren
        //taskPatternFinder.findAllTaskPatterns(graph, sbvrDataBuilder);

        // Speichern der generierten SBVR-Daten in eine Datei
        String outputFilePath = "generated_rules.sbvr";
        saveSBVRToFile(sbvrDataBuilder.toString(), outputFilePath);
    }
}
