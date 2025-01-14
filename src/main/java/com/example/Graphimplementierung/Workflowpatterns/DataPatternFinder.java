package com.example.Graphimplementierung.Workflowpatterns;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Nodes.DataEdge;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Edge;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Node;

import static com.example.Graphimplementierung.Grundstruktur.Parser.Main.cleanText;

public class DataPatternFinder {

    public static void generateDataEdgeRules(BPMNGraph graph, StringBuilder sbvrData) {
        for (Edge edge : graph.getEdges()) {
            // Prüfe, ob die Kante eine DataEdge ist
            if (edge instanceof DataEdge) {
                Node sourceNode = edge.getSource();
                Node targetNode = edge.getTarget();

                // Extrahiere relevante Informationen
                String sourceName = cleanText(sourceNode.getName());
                String targetName = cleanText(targetNode.getName());

                String sourceLane = sourceNode.getLane() != null ? sourceNode.getLane().getName() : "Unknown Lane";
                String targetLane = targetNode.getLane() != null ? targetNode.getLane().getName() : "Unknown Lane";

                // Regel formulieren
                String rule = "It is obligatory that \"" + sourceName +
                        "\" is associated with \"" + targetName +
                        "\", and provides the activity with additional information.";
                System.out.println(rule);
                sbvrData.append(rule).append("\n");
            }
        }
    }

}
