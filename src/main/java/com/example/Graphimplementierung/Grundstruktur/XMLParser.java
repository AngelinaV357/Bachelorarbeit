package com.example.Graphimplementierung.Grundstruktur;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import org.w3c.dom.Node;

public class XMLParser {
    public static void main(String[] args) {
        try {
            // 1. XML-Dokument parsen
            File xmlFile = new File("src/main/resources/May_combine_ingredients.bpmn"); // Pfad zur XML-Datei
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // 2. BPMN-Graph initialisieren
            BPMNGraph graph = new BPMNGraph();

            // 3. "task"-Elemente auslesen und ActivityNodes erstellen
            NodeList taskNodes = doc.getElementsByTagName("ns0:task");
            for (int i = 0; i < taskNodes.getLength(); i++) {
                Node node = taskNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {  // Überprüfen, ob der Knoten ein Element ist
                    Element element = (Element) node;

                    // Attribute extrahieren
                    String id = element.getAttribute("id");
                    String name = element.getAttribute("name");
                    String activityType = element.getTagName();

                    // Lane extrahieren (falls vorhanden)
                    Lane lane = null;
                    NodeList laneNodes = element.getElementsByTagName("ns0:lane");
                    if (laneNodes.getLength() > 0) {
                        // Lane gefunden, extrahiere deren Informationen
                        Element laneElement = (Element) laneNodes.item(0);
                        String laneId = laneElement.getAttribute("id");
                        String laneName = laneElement.getAttribute("name");
                        lane = new Lane(laneId, laneName);
                    } else {
                        // Standard-Lane, falls keine Lane gefunden wird
                        lane = new Lane("defaultLane", "Default Lane");
                    }

                    // ActivityNode erstellen
                    ActivityNode activityNode = new ActivityNode(id, name, lane, activityType);

                    // Node zum Graph hinzufügen
                    graph.addNode(activityNode);

                    System.out.println("ActivityNode hinzugefügt: " + activityNode);
                }
            }

            // 4. "exclusiveGateway"-Elemente auslesen und GatewayNodes erstellen (XOR-Gateway)
            NodeList gatewayNodes = doc.getElementsByTagName("ns0:exclusiveGateway");
            for (int i = 0; i < gatewayNodes.getLength(); i++) {
                Node node = gatewayNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // Attribute extrahieren
                    String id = element.getAttribute("id");
                    String name = element.getAttribute("name");
                    String gatewayDirection = element.getAttribute("gatewayDirection");
                    String gatewayType = "XOR";  // XOR Gateway für exclusiveGateway

                    // Lane extrahieren (falls vorhanden)
                    Lane lane = null;
                    NodeList laneNodes = element.getElementsByTagName("ns0:lane");
                    if (laneNodes.getLength() > 0) {
                        // Lane gefunden, extrahiere deren Informationen
                        Element laneElement = (Element) laneNodes.item(0);
                        String laneId = laneElement.getAttribute("id");
                        String laneName = laneElement.getAttribute("name");
                        lane = new Lane(laneId, laneName);
                    } else {
                        // Standard-Lane, falls keine Lane gefunden wird
                        lane = new Lane("defaultLane", "Default Lane");
                    }

                    // Standardname setzen, wenn kein Name vorhanden ist
                    if (name == null || name.isEmpty()) {
                        name = "Merge Gateway";
                    }

                    // XOR Gateway erstellen
                    GatewayNode gatewayNode = new GatewayNode(id, name, lane, gatewayType);

                    // Node zum Graph hinzufügen
                    graph.addNode(gatewayNode);

                    System.out.println("XOR GatewayNode hinzugefügt: " + gatewayNode);
                }
            }


            // 5. "startEvent"-Elemente auslesen und StartEventNodes erstellen
            NodeList startEventNodes = doc.getElementsByTagName("ns0:startEvent");
            for (int i = 0; i < startEventNodes.getLength(); i++) {
                Node node = startEventNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // Attribute extrahieren
                    String id = element.getAttribute("id");
                    String name = element.getAttribute("name");

                    // Lane extrahieren (falls vorhanden)
                    Lane lane = null;
                    NodeList laneNodes = element.getElementsByTagName("ns0:lane");
                    if (laneNodes.getLength() > 0) {
                        // Lane gefunden, extrahiere deren Informationen
                        Element laneElement = (Element) laneNodes.item(0);
                        String laneId = laneElement.getAttribute("id");
                        String laneName = laneElement.getAttribute("name");
                        lane = new Lane(laneId, laneName);
                    } else {
                        // Standard-Lane, falls keine Lane gefunden wird
                        lane = new Lane("defaultLane", "Default Lane");
                    }

                    // StartEventNode erstellen
                    StartEventNode startEventNode = new StartEventNode(id, name, lane);

                    // Node zum Graph hinzufügen
                    graph.addNode(startEventNode);

                    System.out.println("StartEventNode hinzugefügt: " + startEventNode);
                }
            }

            // 6. "endEvent"-Elemente auslesen und EndEventNodes erstellen
            NodeList endEventNodes = doc.getElementsByTagName("ns0:endEvent");
            for (int i = 0; i < endEventNodes.getLength(); i++) {
                Node node = endEventNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // Attribute extrahieren
                    String id = element.getAttribute("id");
                    String name = element.getAttribute("name");

                    // Lane extrahieren (falls vorhanden)
                    Lane lane = null;
                    NodeList laneNodes = element.getElementsByTagName("ns0:lane");
                    if (laneNodes.getLength() > 0) {
                        // Lane gefunden, extrahiere deren Informationen
                        Element laneElement = (Element) laneNodes.item(0);
                        String laneId = laneElement.getAttribute("id");
                        String laneName = laneElement.getAttribute("name");
                        lane = new Lane(laneId, laneName);
                    } else {
                        // Standard-Lane, falls keine Lane gefunden wird
                        lane = new Lane("defaultLane", "Default Lane");
                    }

                    // EndEventNode erstellen
                    EndEventNode endEventNode = new EndEventNode(id, name, lane);

                    // Node zum Graph hinzufügen
                    graph.addNode(endEventNode);

                    System.out.println("EndEventNode hinzugefügt: " + endEventNode);
                }
            }

            NodeList sequenceFlowNodes = doc.getElementsByTagName("ns0:sequenceFlow");
            for (int i = 0; i < sequenceFlowNodes.getLength(); i++) {
                Node node = sequenceFlowNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // Attribute extrahieren
                    String id = element.getAttribute("id");
                    String sourceRef = element.getAttribute("sourceRef");
                    String targetRef = element.getAttribute("targetRef");
                    String condition = element.getAttribute("condition");

                    // Knoten aus dem Graphen holen
                    com.example.Graphimplementierung.Grundstruktur.Node sourceNode = graph.getNodeById(sourceRef);
                    com.example.Graphimplementierung.Grundstruktur.Node targetNode = graph.getNodeById(targetRef);

                    // Überprüfen, ob der sourceNode und targetNode gefunden wurden
                    if (sourceNode == null) {
                        System.out.println("WARNUNG: SourceNode mit ID " + sourceRef + " nicht gefunden.");
                        continue;  // Skippen, wenn der Knoten nicht existiert
                    }

                    if (targetNode == null) {
                        System.out.println("WARNUNG: TargetNode mit ID " + targetRef + " nicht gefunden.");
                        continue;  // Skippen, wenn der Knoten nicht existiert
                    }

                    // Edge erstellen
                    Edge edge = new Edge(id, sourceNode, targetNode, condition);

                    // Edge zum Graph hinzufügen
                    graph.addEdge(edge);

                    System.out.println("Edge hinzugefügt: " + edge);
                }
            }


            // Optional: Ausgabe des gesamten Graphen
            System.out.println("\nAlle Knoten im Graph:");
            graph.getNodes().forEach(System.out::println);

            System.out.println("\nAlle Kanten im Graph:");
            graph.getEdges().forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

