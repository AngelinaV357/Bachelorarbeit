package com.example.Graphimplementierung.Grundstruktur;

import com.example.Graphimplementierung.Grundstruktur.Nodes.*;
import org.w3c.dom.*;
import org.w3c.dom.Node;

import javax.xml.parsers.*;
import java.io.File;

public class XMLParser {

    public BPMNGraph parseXML(String xmlFilePath) {
        BPMNGraph graph = new BPMNGraph();

        try {
            // 1. XML-Dokument parsen
            File xmlFile = new File(xmlFilePath); // Pfad zur XML-Datei
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // 2. "task"-Elemente auslesen und ActivityNodes erstellen
            processActivityNodes(doc, "ns0:task", "Task", graph);
            processActivityNodes(doc, "ns0:subProcess", "SubProcess", graph);
            processGateways(doc, graph);
            processStartEndEvents(doc, "ns0:startEvent", "StartEvent", graph);
            processStartEndEvents(doc, "ns0:endEvent", "EndEvent", graph);
            processDataObjects(doc, graph);
            processDataInputs(doc, graph);
            processUserTasks(doc, graph);
            processServiceTasks(doc, graph);
            processSequenceFlows(doc, graph);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return graph;
    }

    private void processActivityNodes(Document doc, String tagName, String activityType, BPMNGraph graph) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");

                // Lane extrahieren
                Lane lane = extractLane(element);

                // ActivityNode erstellen
                ActivityNode activityNode = new ActivityNode(id, name, lane, activityType);
                graph.addNode(activityNode);
            }
        }
    }

    private void processGateways(Document doc, BPMNGraph graph) {
        NodeList gatewayNodes = doc.getElementsByTagName("*");
        for (int i = 0; i < gatewayNodes.getLength(); i++) {
            Node node = gatewayNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String tagName = element.getTagName();

                if (tagName.equals("ns0:exclusiveGateway") || tagName.equals("ns0:parallelGateway") || tagName.equals("ns0:eventBasedGateway")) {
                    String id = element.getAttribute("id");
                    String name = element.getAttribute("name");
                    String gatewayType = "Gateway";  // Standard für Gateway

                    // Gateway-Typ bestimmen
                    if (tagName.equals("ns0:parallelGateway")) {
                        gatewayType = "AND";
                    } else if (tagName.equals("ns0:eventBasedGateway")) {
                        gatewayType = "Event";
                    }

                    // Name setzen, wenn nicht vorhanden
                    if (name == null || name.isEmpty()) {
                        name = gatewayType + " Gateway";
                    }

                    // Lane extrahieren
                    Lane lane = extractLane(element);

                    // GatewayNode erstellen
                    ActivityNode gatewayNode = new ActivityNode(id, name, lane, gatewayType);
                    graph.addNode(gatewayNode);
                }
            }
        }
    }

    private void processStartEndEvents(Document doc, String tagName, String eventType, BPMNGraph graph) {
        NodeList eventNodes = doc.getElementsByTagName(tagName);
        for (int i = 0; i < eventNodes.getLength(); i++) {
            Node node = eventNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");

                if (name == null || name.isEmpty()) {
                    name = eventType.equals("StartEvent") ? "Start" : "End";
                }

                // Lane extrahieren
                Lane lane = extractLane(element);

                // EventNode erstellen
                ActivityNode eventNode = new ActivityNode(id, name, lane, eventType);
                graph.addNode(eventNode);
            }
        }
    }

    private void processDataObjects(Document doc, BPMNGraph graph) {
        NodeList dataObjectNodes = doc.getElementsByTagName("ns0:dataObject");
        for (int i = 0; i < dataObjectNodes.getLength(); i++) {
            Node node = dataObjectNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");
                String activityType = "DataObject";

                // Lane extrahieren
                Lane lane = extractLane(element);

                // DataObjectNode erstellen
                ActivityNode dataObjectNode = new ActivityNode(id, name, lane, activityType);
                graph.addNode(dataObjectNode);
            }
        }
    }

    private void processDataInputs(Document doc, BPMNGraph graph) {
        NodeList dataInputNodes = doc.getElementsByTagName("ns0:dataInput");
        for (int i = 0; i < dataInputNodes.getLength(); i++) {
            Node node = dataInputNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");
                String activityType = "DataInput";

                // Lane extrahieren
                Lane lane = extractLane(element);

                // DataInputNode erstellen
                ActivityNode dataInputNode = new ActivityNode(id, name, lane, activityType);
                graph.addNode(dataInputNode);
            }
        }
    }

    private void processUserTasks(Document doc, BPMNGraph graph) {
        NodeList userTaskNodes = doc.getElementsByTagName("ns0:userTask");
        for (int i = 0; i < userTaskNodes.getLength(); i++) {
            Node node = userTaskNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");
                String activityType = "UserTask";

                // Lane extrahieren
                Lane lane = extractLane(element);

                // UserTaskNode erstellen
                ActivityNode userTaskNode = new ActivityNode(id, name, lane, activityType);
                graph.addNode(userTaskNode);
            }
        }
    }

    private void processServiceTasks(Document doc, BPMNGraph graph) {
        NodeList serviceTaskNodes = doc.getElementsByTagName("ns0:serviceTask");
        for (int i = 0; i < serviceTaskNodes.getLength(); i++) {
            Node node = serviceTaskNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");
                String activityType = "ServiceTask";

                // Lane extrahieren
                Lane lane = extractLane(element);

                // ServiceTaskNode erstellen
                ActivityNode serviceTaskNode = new ActivityNode(id, name, lane, activityType);
                graph.addNode(serviceTaskNode);
            }
        }
    }

    private void processSequenceFlows(Document doc, BPMNGraph graph) {
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

                // Kante erstellen
                Edge edge = new Edge(id, sourceNode, targetNode, condition);
                graph.addEdge(edge);
            }
        }
    }

    // Methode zum Extrahieren der Lane aus einem Element
    private static Lane extractLane(Element element) {
        Lane lane = null;
        NodeList laneSetNodes = element.getOwnerDocument().getElementsByTagName("ns0:laneSet");

        for (int i = 0; i < laneSetNodes.getLength(); i++) {
            Element laneSetElement = (Element) laneSetNodes.item(i);
            NodeList laneNodes = laneSetElement.getElementsByTagName("ns0:lane");
            if (laneNodes.getLength() > 0) {
                Element laneElement = (Element) laneNodes.item(0);
                String laneId = laneElement.getAttribute("id");
                String laneName = laneElement.getAttribute("name");

                lane = new Lane(laneId, laneName);
                break;
            }
        }

        if (lane == null) {
            lane = new Lane("defaultLane", "Default Lane");
        }

        return lane;
    }
}