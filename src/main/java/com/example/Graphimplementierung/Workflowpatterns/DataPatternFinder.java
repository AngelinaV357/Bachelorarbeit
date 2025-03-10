package com.example.Graphimplementierung.Workflowpatterns;

import com.example.Graphimplementierung.Grundstruktur.Nodes.*;

import static com.example.Graphimplementierung.Grundstruktur.Parser.Main.cleanText;

public class DataPatternFinder {

    /**
     * Data Transfer by Value - incoming/Outgoing Pattern
     * @param graph
     * @param sbvrData
     */
    public static void generateDataEdgeRules(BPMNGraph graph, StringBuilder sbvrData) {
        for (Edge edge : graph.getEdges()) {
            // Prüfe, ob die Kante eine DataEdge ist
            if (edge instanceof DataEdge) {
                Node sourceNode = edge.getSource();
                Node targetNode = edge.getTarget();

                // Extrahiere relevante Informationen
                String sourceName = cleanText(sourceNode.getName());
                String targetName = cleanText(targetNode.getName());

                // Überprüfen, ob der Pfeil auf eine Aktivität zeigt (Incoming)
                if (targetNode instanceof TaskNode) { // Wenn es sich um eine Aktivität handelt
                    // Ausgabe für das erkannte "Incoming"-Pattern
                    System.out.println("Pattern erkannt: Data Transfer by Value - Incoming");

                    // Regel formulieren für "Incoming"
                    String rule = "It is obligatory that \"" + targetName +
                            "\" receives Data  \"" + sourceName +
                            "\" and uses it to perform its activity. ";
                    System.out.println(rule);
                    sbvrData.append(rule).append("\n");
                }
                // Überprüfen, ob es ein "Outgoing" ist (Daten wird von der Aktivität aus weitergegeben)
                if (sourceNode instanceof TaskNode) { // Wenn es sich um eine Aktivität handelt
                    // Ausgabe für das erkannte "Outgoing"-Pattern
                    System.out.println("Pattern erkannt: Data Transfer by Value - Outgoing");

                    // Regel formulieren für "Outgoing"
                    String rule = "It is obligatory that \"" + sourceName + "\" sends its output to \"" + targetName + "\".";
                    System.out.println(rule);
                    sbvrData.append(rule).append("\n");
                }
            }
        }
    }
}
