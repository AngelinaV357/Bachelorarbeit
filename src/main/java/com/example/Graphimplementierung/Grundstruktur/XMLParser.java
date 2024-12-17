package com.example.Graphimplementierung.Grundstruktur;

import com.example.Graphimplementierung.Grundstruktur.Nodes.*;
import org.w3c.dom.*;
import org.w3c.dom.Node;

import javax.xml.parsers.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class XMLParser {
    public static void main(String[] args) {
        try {
            // 1. XML-Dokument parsen
            File xmlFile = new File("src/main/resources/Employee Onboarding.bpmn"); // Pfad zur XML-Datei
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // 2. BPMN-Graph initialisieren
            BPMNGraph graph = new BPMNGraph();

            // 3. Lanes auslesen und eine Lane-Map erstellen
            Map<String, Lane> laneMap = createLaneMap(doc);

            // 4. "task"-Elemente auslesen und ActivityNodes erstellen
            NodeList taskNodes = doc.getElementsByTagName("ns0:task");
            for (int i = 0; i < taskNodes.getLength(); i++) {
                Node node = taskNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute("id");
                    String name = element.getAttribute("name");
                    String activityType = element.getTagName();

                    // Lane zuordnen
                    Lane lane = laneMap.getOrDefault(id, new Lane("defaultLane", "Default Lane"));

                    // ActivityNode erstellen
                    ActivityNode activityNode = new ActivityNode(id, name, lane, activityType);
                    graph.addNode(activityNode);
                }
            }

            // 5. Gateway-Elemente auslesen und GatewayNodes erstellen
            NodeList gatewayNodes = doc.getElementsByTagName("*");
            for (int i = 0; i < gatewayNodes.getLength(); i++) {
                Node node = gatewayNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String tagName = element.getTagName();

                    // Prüfen, ob das Element ein Gateway ist
                    if (tagName.equals("ns0:exclusiveGateway") || tagName.equals("ns0:parallelGateway") || tagName.equals("ns0:eventBasedGateway")) {
                        String id = element.getAttribute("id");
                        String name = element.getAttribute("name");
                        String gatewayType;

                        // Gateway-Typ anhand des Tags bestimmen
                        switch (tagName) {
                            case "ns0:parallelGateway":
                                gatewayType = "AND";
                                break;
                            case "ns0:eventBasedGateway":
                                gatewayType = "Event";
                                break;
                            case "ns0:exclusiveGateway":
                            default:
                                gatewayType = "XOR"; // Standard für XOR
                        }

                        // Standardname setzen, wenn kein Name vorhanden ist
                        if (name == null || name.isEmpty()) {
                            name = gatewayType + " Gateway";
                        }

                        // Lane zuordnen
                        Lane lane = laneMap.getOrDefault(id, new Lane("defaultLane", "Default Lane"));

                        // GatewayNode erstellen
                        GatewayNode gatewayNode = new GatewayNode(id, name, lane, gatewayType);
                        graph.addNode(gatewayNode);
                    }
                }
            }

            // 6. SubProcess-Elemente auslesen und SubProcessNodes erstellen
            NodeList subProcessNodes = doc.getElementsByTagName("ns0:subProcess");
            for (int i = 0; i < subProcessNodes.getLength(); i++) {
                Node node = subProcessNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute("id");
                    String name = element.getAttribute("name");

                    // Standardname setzen, falls Name leer ist
                    if (name == null || name.isEmpty()) {
                        name = "SubProcess";
                    }

                    // Lane zuordnen
                    Lane lane = laneMap.getOrDefault(id, new Lane("defaultLane", "Default Lane"));

                    // SubProcessNode erstellen und dem Graph hinzufügen
                    SubProcessNode subProcessNode = new SubProcessNode(id, name, lane);
                    graph.addNode(subProcessNode);
                }
            }

            // 7. StartEvent-Elemente auslesen und StartEventNodes erstellen
            NodeList startEventNodes = doc.getElementsByTagName("ns0:startEvent");
            for (int i = 0; i < startEventNodes.getLength(); i++) {
                Node node = startEventNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute("id");
                    String name = element.getAttribute("name");
                    if (name == null || name.isEmpty()) {
                        name = "Start";
                    }

                    // Lane zuordnen
                    Lane lane = laneMap.getOrDefault(id, new Lane("defaultLane", "Default Lane"));

                    StartEventNode startEventNode = new StartEventNode(id, name, lane);
                    graph.addNode(startEventNode);
                }
            }

            // 8. EndEvent-Elemente auslesen und EndEventNodes erstellen
            NodeList endEventNodes = doc.getElementsByTagName("ns0:endEvent");
            for (int i = 0; i < endEventNodes.getLength(); i++) {
                Node node = endEventNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute("id");
                    String name = element.getAttribute("name");
                    if (name == null || name.isEmpty()) {
                        name = "End";
                    }

                    // Lane zuordnen
                    Lane lane = laneMap.getOrDefault(id, new Lane("defaultLane", "Default Lane"));

                    EndEventNode endEventNode = new EndEventNode(id, name, lane);
                    graph.addNode(endEventNode);
                }
            }

            // 9. SequenceFlow-Elemente auslesen und Kanten erstellen
            NodeList sequenceFlowNodes = doc.getElementsByTagName("ns0:sequenceFlow");
            for (int i = 0; i < sequenceFlowNodes.getLength(); i++) {
                Node node = sequenceFlowNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // Attribute extrahieren
                    String id = element.getAttribute("id");
                    String sourceRef = element.getAttribute("sourceRef");
                    String targetRef = element.getAttribute("targetRef");
                    String condition = element.getAttribute("name");

                    // Knoten aus dem Graphen holen
                    com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
                    com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);


                    // Überprüfen, ob die Knoten existieren
                    if (sourceNode == null) {
                        System.err.println("Warnung: Quelle Knoten mit ID '" + sourceRef + "' nicht gefunden.");
                        continue; // Ignoriere diese Kante
                    }
                    if (targetNode == null) {
                        System.err.println("Warnung: Ziel Knoten mit ID '" + targetRef + "' nicht gefunden.");
                        continue; // Ignoriere diese Kante
                    }

                    // Kante erstellen
                    Edge edge = new Edge(id, sourceNode, targetNode, condition);
                    graph.addEdge(edge);
                }
            }

            // Optional: Ausgabe des gesamten Graphen
            System.out.println("\nAlle Knoten im Graph:");
            graph.getNodes().forEach(node -> System.out.println(node));

            System.out.println("\nAlle Kanten im Graph:");
            graph.getEdges().forEach(edge -> System.out.println(edge));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methode zum Erstellen der Lane-Map
    private static Map<String, Lane> createLaneMap(Document doc) {
        Map<String, Lane> laneMap = new HashMap<>();
        NodeList laneNodes = doc.getElementsByTagName("ns0:lane");
        for (int i = 0; i < laneNodes.getLength(); i++) {
            Node node = laneNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element laneElement = (Element) node;
                String laneId = laneElement.getAttribute("id");
                String laneName = laneElement.getAttribute("name");

                Lane lane = new Lane(laneId, laneName);
                NodeList flowNodeRefs = laneElement.getElementsByTagName("ns0:flowNodeRef");
                for (int j = 0; j < flowNodeRefs.getLength(); j++) {
                    String flowNodeId = flowNodeRefs.item(j).getTextContent();
                    laneMap.put(flowNodeId, lane);
                }
            }
        }
        return laneMap;
    }
}
