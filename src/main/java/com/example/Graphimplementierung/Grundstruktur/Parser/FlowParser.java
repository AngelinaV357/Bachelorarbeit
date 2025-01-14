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


    static void addEdgesToDataInputAssociation(Document doc, BPMNGraph graph) {
        // Verarbeitung für dataInputAssociation und dataOutputAssociation
        processAssociations(doc, graph, "dataInputAssociation");
        processAssociations(doc, graph, "dataOutputAssociation");
    }

    private static void processAssociations(Document doc, BPMNGraph graph, String tagName) {
        // Holen der Assoziationen für den gegebenen tagName (dataInputAssociation oder dataOutputAssociation)
        NodeList associations = doc.getElementsByTagNameNS("*", tagName);
        System.out.println("Verarbeite Assoziationen des Typs: " + tagName);

        for (int i = 0; i < associations.getLength(); i++) {
            Element element = (Element) associations.item(i);

            // Debugging: Ausgabe der Assoziation
            System.out.println("Verarbeite Assoziation " + (i + 1) + ": " + element);

            // Holen der sourceRef und targetRef aus der Assoziation
            NodeList sourceRefs = element.getElementsByTagName("ns0:sourceRef");
            NodeList targetRefs = element.getElementsByTagName("ns0:targetRef");

            // Debugging: Anzahl der sourceRefs und targetRefs
            System.out.println("Anzahl der sourceRefs: " + sourceRefs.getLength());
            System.out.println("Anzahl der targetRefs: " + targetRefs.getLength());

            // Verarbeite alle sourceRefs
            for (int j = 0; j < sourceRefs.getLength(); j++) {
                String sourceRef = sourceRefs.item(j).getTextContent();

                // Debugging: Quelle für diese Assoziation
                System.out.println("sourceRef: " + sourceRef);

                // Verarbeite alle targetRefs
                for (int k = 0; k < targetRefs.getLength(); k++) {
                    String targetRef = targetRefs.item(k).getTextContent();

                    // Debugging: Ziel für diese Assoziation
                    System.out.println("targetRef: " + targetRef);

                    // Sicherstellen, dass sourceRef und targetRef nicht leer sind
                    if (sourceRef == null || sourceRef.isEmpty() || targetRef == null || targetRef.isEmpty()) {
                        System.out.println("Warnung: Ungültige sourceRef oder targetRef bei " + tagName);
                        continue;
                    }

                    // Hole die Knoten vom Graphen anhand der sourceRef und targetRef
                    com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
                    com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);

                    // Debugging: Knoten-IDs
                    System.out.println("Überprüfe Knoten mit ID: " + sourceRef + " und " + targetRef);
                    System.out.println("Gefundener sourceNode: " + sourceNode);
                    System.out.println("Gefundener targetNode: " + targetNode);

                    // Prüfe, ob die Knoten existieren, wenn nicht, überspringe diese Assoziation
                    if (sourceNode == null) {
                        System.out.println("Warnung: Quellknoten mit ID " + sourceRef + " nicht gefunden.");
                        continue;
                    }
                    if (targetNode == null) {
                        System.out.println("Warnung: Zielknoten mit ID " + targetRef + " nicht gefunden.");
                        continue;
                    }

                    // ** Neue Extraktion der Elementtypen:**
                    // Prüfe, ob der sourceNode und targetNode das richtige Element sind
                    if (sourceNode instanceof DataNode && targetNode instanceof DataNode) {
                        // Erstelle eine DataEdge
                        DataEdge dataObjectEdge = new DataEdge(sourceRef, sourceNode, targetNode);
                        graph.addEdge(dataObjectEdge);
                        // Debugging: Ausgabe der DataEdge
                        System.out.println("DataEdge hinzugefügt: " + dataObjectEdge);
                    } else {
                        // Andernfalls normale Edge erstellen
                        Edge edge = new Edge(sourceRef, sourceNode, targetNode, null);
                        graph.addEdge(edge);
                        // Debugging: Ausgabe der normalen Edge
                        System.out.println("Normale Edge hinzugefügt: " + edge);
                    }
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
