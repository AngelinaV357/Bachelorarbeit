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

    static void addEdgesToDataInputAssociation(Document doc, BPMNGraph graph) {
        // Verarbeitung der Kanten (edges) für dataInputAssociations
        NodeList dataInputAssociations = doc.getElementsByTagName("ns0:dataInputAssociation");
        for (int i = 0; i < dataInputAssociations.getLength(); i++) {
            org.w3c.dom.Node node = dataInputAssociations.item(i);
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Extrahiere sourceRef und targetRef aus der XML
                String sourceRef = null;
                String targetRef = null;

                NodeList sourceRefs = element.getElementsByTagName("ns0:sourceRef");
                if (sourceRefs.getLength() > 0) {
                    sourceRef = sourceRefs.item(0).getTextContent();
                }

                NodeList targetRefs = element.getElementsByTagName("ns0:targetRef");
                if (targetRefs.getLength() > 0) {
                    targetRef = targetRefs.item(0).getTextContent();
                }

                // Sicherstellen, dass sourceRef und targetRef nicht leer sind
                if (sourceRef == null || sourceRef.isEmpty() || targetRef == null || targetRef.isEmpty()) {
                    System.out.println("Warnung: Ungültige sourceRef oder targetRef bei dataInputAssociation");
                    continue;
                }

                // Hole die Knoten vom Graph (Datenobjekte und Aktivitäten)
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);

                // Prüfe, ob die Knoten existieren
                if (sourceNode == null) {
                    System.out.println("Warnung: Quellknoten mit ID " + sourceRef + " nicht gefunden.");
                    continue;
                }
                if (targetNode == null) {
                    System.out.println("Warnung: Zielknoten mit ID " + targetRef + " nicht gefunden.");
                    continue;
                }

                // Wenn es sich um eine Verbindung zwischen Datenobjekten handelt
                if (sourceNode instanceof DataNode && targetNode instanceof DataNode) {
                    DataEdge dataObjectEdge = new DataEdge(sourceRef, sourceNode, targetNode);
                    graph.addEdge(dataObjectEdge);
                    System.out.println("DataObjectEdge{source='" + sourceNode.getName() + "', target='" + targetNode.getName() + "'}");
                } else {
                    // Normaler Edge Fall (Dateninput zu Aktivität)
                    Edge dataEdge = new Edge(sourceRef, sourceNode, targetNode, null);
                    graph.addEdge(dataEdge);
                    System.out.println("Edge{source='" + sourceNode.getName() + "', target='" + targetNode.getName() + "'}");
                }
            }
        }

        // Verarbeitung der Kanten (edges) für dataOutputAssociations
        NodeList dataOutputAssociations = doc.getElementsByTagName("ns0:dataOutputAssociation");
        for (int i = 0; i < dataOutputAssociations.getLength(); i++) {
            org.w3c.dom.Node node = dataOutputAssociations.item(i);
            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Extrahiere sourceRef und targetRef aus der XML
                String sourceRef = null;
                String targetRef = null;

                NodeList sourceRefs = element.getElementsByTagName("ns0:sourceRef");
                if (sourceRefs.getLength() > 0) {
                    sourceRef = sourceRefs.item(0).getTextContent();
                }

                NodeList targetRefs = element.getElementsByTagName("ns0:targetRef");
                if (targetRefs.getLength() > 0) {
                    targetRef = targetRefs.item(0).getTextContent();
                }

                // Sicherstellen, dass sourceRef und targetRef nicht leer sind
                if (sourceRef == null || sourceRef.isEmpty() || targetRef == null || targetRef.isEmpty()) {
                    System.out.println("Warnung: Ungültige sourceRef oder targetRef bei dataOutputAssociation");
                    continue;
                }

                // Hole die Knoten vom Graph (Aktivitäten und Datenobjekte)
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);

                // Prüfe, ob die Knoten existieren
                if (sourceNode == null) {
                    System.out.println("Warnung: Quellknoten mit ID " + sourceRef + " nicht gefunden.");
                    continue;
                }
                if (targetNode == null) {
                    System.out.println("Warnung: Zielknoten mit ID " + targetRef + " nicht gefunden.");
                    continue;
                }

                // Wenn es sich um eine Verbindung zwischen Datenobjekten handelt
                if (sourceNode instanceof DataNode && targetNode instanceof DataNode) {
                    DataEdge dataObjectEdge = new DataEdge(sourceRef, sourceNode, targetNode);
                    graph.addEdge(dataObjectEdge);
                    System.out.println("DataObjectEdge{source='" + sourceNode.getName() + "', target='" + targetNode.getName() + "'}");
                } else {
                    // Normaler Edge Fall (Aktivität zu Datenobjekt)
                    Edge dataEdge = new Edge(sourceRef, sourceNode, targetNode, null);
                    graph.addEdge(dataEdge);
                    System.out.println("Edge{source='" + sourceNode.getName() + "', target='" + targetNode.getName() + "'}");
                }
            }
        }
    }
}
