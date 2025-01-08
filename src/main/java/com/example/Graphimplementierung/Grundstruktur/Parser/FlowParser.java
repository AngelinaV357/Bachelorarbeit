package com.example.Graphimplementierung.Grundstruktur.Parser;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Edge;
import com.example.Graphimplementierung.Grundstruktur.Nodes.GatewayEdge;
import com.example.Graphimplementierung.Grundstruktur.Nodes.GatewayNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FlowParser {

    // Methode zur Verarbeitung von normalen Edges ohne Gateways
    public static void processSequenceFlows(Document doc, BPMNGraph graph) {
        NodeList sequenceFlowNodes = doc.getElementsByTagName("ns0:sequenceFlow");

        // Über jedes SequenceFlow iterieren
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

                // Wenn keine Gateways beteiligt sind, erstelle eine normale Edge
                if (!(sourceNode instanceof GatewayNode || targetNode instanceof GatewayNode)) {
                    Edge edge = new Edge(id, sourceNode, targetNode, condition);
                    graph.addEdge(edge);
                }
            }
        }
    }

    // Methode zur Verarbeitung von GatewayEdges, die nur Kanten mit Gateways betrifft
    public static void processGatewayEdges(Document doc, BPMNGraph graph) {
        NodeList sequenceFlowNodes = doc.getElementsByTagName("ns0:sequenceFlow");

        for (int i = 0; i < sequenceFlowNodes.getLength(); i++) {
            Node node = sequenceFlowNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
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

                // Wenn einer der Knoten ein Gateway ist, erstelle eine GatewayEdge
                if (sourceNode instanceof GatewayNode || targetNode instanceof GatewayNode) {
                    GatewayEdge gatewayEdge = new GatewayEdge(id, sourceNode, targetNode, condition);
                    graph.addEdge(gatewayEdge);
                }
            }
        }
    }


    public static void processDataAssociations(Document doc, BPMNGraph graph) {
        // Verarbeitung von DataInputAssociations
        NodeList dataInputAssociations = doc.getElementsByTagName("ns0:dataInputAssociation");
        processAssociations(dataInputAssociations, graph, true);

        // Verarbeitung von DataOutputAssociations
        NodeList dataOutputAssociations = doc.getElementsByTagName("ns0:dataOutputAssociation");
        processAssociations(dataOutputAssociations, graph, false);
    }

    private static void processAssociations(NodeList associations, BPMNGraph graph, boolean isInput) {
        for (int i = 0; i < associations.getLength(); i++) {
            org.w3c.dom.Node associationNode = associations.item(i);
            if (associationNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element element = (Element) associationNode;

                String id = element.getAttribute("id");

                // Quelle (sourceRef)
                String sourceRef = null;
                NodeList sourceRefs = element.getElementsByTagName("ns0:sourceRef");
                if (sourceRefs.getLength() > 0) {
                    sourceRef = sourceRefs.item(0).getTextContent();
                }

                // Ziel (targetRef)
                String targetRef = null;
                NodeList targetRefs = element.getElementsByTagName("ns0:targetRef");
                if (targetRefs.getLength() > 0) {
                    targetRef = targetRefs.item(0).getTextContent();
                }

                // Validierung der Referenzen
                if (sourceRef == null || targetRef == null) {
                    continue;
                }

                // Holen der Knoten aus dem Graphen
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);

                // Überprüfen, ob die Knoten existieren
                if (sourceNode == null || targetNode == null) {
                    continue;
                }

                // Kante erstellen
                Edge edge = new Edge(id, sourceNode, targetNode, isInput ? "Data Input" : "Data Output");
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
