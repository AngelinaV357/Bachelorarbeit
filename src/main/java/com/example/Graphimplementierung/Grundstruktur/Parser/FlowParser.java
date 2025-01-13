package com.example.Graphimplementierung.Grundstruktur.Parser;

import com.example.Graphimplementierung.Grundstruktur.Nodes.*;
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


    // Methode zur Verarbeitung von Associations zwischen DataObjects
    public static void processDataAssociations(Document doc, BPMNGraph graph) {
        NodeList associationNodes = doc.getElementsByTagName("ns0:association");

        // Über jedes Association-Element iterieren
        for (int i = 0; i < associationNodes.getLength(); i++) {
                Node node = associationNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String id = element.getAttribute("id");
                    String sourceRef = element.getAttribute("sourceRef");
                    String targetRef = element.getAttribute("targetRef");

                    // Knoten aus dem Graphen holen
                    com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
                    com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);

                    // Überprüfen, ob die Knoten existieren
                    if (sourceNode == null || targetNode == null) {
                        continue;
                    }

                    // Wenn es sich um DataObjects handelt, erstelle eine Association-Edge
                    if (sourceNode instanceof DataNode && targetNode instanceof DataNode) {
                        DataEdge edge = new DataEdge(id, sourceNode, targetNode);
                        graph.addEdge(edge);
                    }
                }
            }
        }


    public static void processMessageFlows(Document doc, BPMNGraph graph) {
        // Hole alle MessageFlow Elemente aus der XML
        NodeList messageFlowNodes = doc.getElementsByTagName("ns0:messageFlow");

        // Iteriere durch alle MessageFlow Elemente
        for (int i = 0; i < messageFlowNodes.getLength(); i++) {
            Node node = messageFlowNodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Extrahiere die Attribute für das MessageFlow Element
                String id = element.getAttribute("id");
                String sourceRef = element.getAttribute("sourceRef").trim();  // Trimmen der ID
                String targetRef = element.getAttribute("targetRef").trim();  // Trimmen der ID

                // Debugging: Ausgabe der IDs und ihrer Werte
                //System.out.println("Processing MessageFlow with ID: " + id);
                //System.out.println("SourceRef: " + sourceRef);
                //System.out.println("TargetRef: " + targetRef);

                // Hole die entsprechenden Knoten aus dem Graphen anhand der IDs
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);

                // Prüfe, ob die Knoten existieren
                if (sourceNode == null) {
                    //System.out.println("Warnung: Quellknoten mit ID " + sourceRef + " nicht gefunden.");
                    continue;
                }
                if (targetNode == null) {
                    //System.out.println("Warnung: Zielknoten mit ID " + targetRef + " nicht gefunden.");
                    continue;
                }

                // Optional: Extrahiere die Bedingung
                String condition = element.getAttribute("name");

                // Erstelle eine normale Edge und füge sie dem Graphen hinzu
                MessageEdge edge = new MessageEdge(id, sourceNode, targetNode, condition);
                graph.addEdge(edge);

                // Optional: Ausgabe der Edge
                //System.out.println("Edge{id='" + id + "', source='" + sourceNode.getName() + "', target='" + targetNode.getName() + "'}");
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
}
