package com.example.Graphimplementierung.Grundstruktur.Parser;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Edge;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FlowParser {
    public static void processSequenceFlows(Document doc, BPMNGraph graph) {
        NodeList sequenceFlowNodes = doc.getElementsByTagName("ns0:sequenceFlow");
        for (int i = 0; i < sequenceFlowNodes.getLength(); i++) {
            org.w3c.dom.Node node = sequenceFlowNodes.item(i);
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element element = (Element) node;

                String id = element.getAttribute("id");
                String sourceRef = element.getAttribute("sourceRef");
                String targetRef = element.getAttribute("targetRef");
                String condition = element.getAttribute("name");

                // Knoten aus dem Graphen holen
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);

                // Überprüfen, ob die Knoten existieren
                if (sourceNode == null || targetNode == null) {
                    continue;
                }

                // Kante erstellen
                Edge edge = new Edge(id, sourceNode, targetNode, condition);
                graph.addEdge(edge);
            }
        }
    }

    public static void processMessageFlows(Document doc, BPMNGraph graph) {
        // Suche nach allen MessageFlow-Elementen unter Berücksichtigung des Namensraums
        NodeList messageFlowNodes = doc.getElementsByTagNameNS("*", "messageFlow");
        for (int i = 0; i < messageFlowNodes.getLength(); i++) {
            org.w3c.dom.Node node = messageFlowNodes.item(i);
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Extrahiere die Attribute id, sourceRef und targetRef
                String id = element.getAttribute("id");
                String sourceRef = element.getAttribute("sourceRef");
                String targetRef = element.getAttribute("targetRef");

                // Überprüfe, ob alle erforderlichen Attribute vorhanden sind
                if (id == null || id.isEmpty() || sourceRef == null || sourceRef.isEmpty() || targetRef == null || targetRef.isEmpty()) {
                    continue; // Überspringe diese Nachricht, falls eine der notwendigen Informationen fehlt
                }

                // Knoten aus dem Graphen holen
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);

                // Überprüfe, ob die Knoten existieren
                if (sourceNode == null || targetNode == null) {
                    continue; // Wenn einer der Knoten nicht existiert, überspringe diesen MessageFlow
                }

                // Bei MessageFlows gibt es normalerweise keine Bedingungen, daher wird hier ein leerer String verwendet
                String condition = "";

                // Kante für den MessageFlow erstellen
                Edge edge = new Edge(id, sourceNode, targetNode, condition);
                graph.addEdge(edge);
            }
        }
    }
}
