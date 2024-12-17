package com.example.Graphimplementierung.Grundstruktur;

import com.example.Graphimplementierung.Grundstruktur.Nodes.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import org.w3c.dom.Node;

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

            // 3. "task"-Elemente auslesen und ActivityNodes erstellen
            NodeList taskNodes = doc.getElementsByTagName("ns0:task");
            for (int i = 0; i < taskNodes.getLength(); i++) {
                Node node = taskNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute("id");
                    String name = element.getAttribute("name");
                    String activityType = "Task";  // Zum Beispiel für Task-Elemente

                    // Lane extrahieren
                    Lane lane = extractLane(element);

                    // ActivityNode erstellen
                    ActivityNode activityNode = new ActivityNode(id, name, lane, activityType);
                    graph.addNode(activityNode);
                }
            }

            // 4. "subProcess"-Elemente auslesen und ActivityNodes erstellen
            NodeList subProcessNodes = doc.getElementsByTagName("ns0:subProcess");
            for (int i = 0; i < subProcessNodes.getLength(); i++) {
                Node node = subProcessNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getAttribute("id");
                    String name = element.getAttribute("name");
                    String activityType = "SubProcess";  // Subprozess-Typ

                    // Lane extrahieren
                    Lane lane = extractLane(element);

                    // ActivityNode erstellen (mit SubProcess-Typ)
                    ActivityNode subProcessNode = new ActivityNode(id, name, lane, activityType);
                    graph.addNode(subProcessNode);
                }
            }

            // 5. "exclusiveGateway" und andere Gateway-Elemente auslesen und ActivityNodes erstellen
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
                        String gatewayType = "Gateway";  // Standard für Gateway

                        // Gateway-Typ anhand des Tags bestimmen
                        if (tagName.equals("ns0:parallelGateway")) {
                            gatewayType = "AND";
                        } else if (tagName.equals("ns0:eventBasedGateway")) {
                            gatewayType = "Event";
                        }

                        // Standardname setzen, wenn kein Name vorhanden ist
                        if (name == null || name.isEmpty()) {
                            name = gatewayType + " Gateway";
                        }

                        // Lane extrahieren
                        Lane lane = extractLane(element);

                        // ActivityNode erstellen (mit Gateway-Typ)
                        ActivityNode gatewayNode = new ActivityNode(id, name, lane, gatewayType);
                        graph.addNode(gatewayNode);
                    }
                }
            }

            // 6. "startEvent"-Elemente auslesen und ActivityNodes erstellen
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

                    // Lane extrahieren
                    Lane lane = extractLane(element);

                    // ActivityNode erstellen (mit StartEvent-Typ)
                    ActivityNode startEventNode = new ActivityNode(id, name, lane, "StartEvent");
                    graph.addNode(startEventNode);
                }
            }

            // 7. "endEvent"-Elemente auslesen und ActivityNodes erstellen
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

                    // Lane extrahieren
                    Lane lane = extractLane(element);

                    // ActivityNode erstellen (mit EndEvent-Typ)
                    ActivityNode endEventNode = new ActivityNode(id, name, lane, "EndEvent");
                    graph.addNode(endEventNode);
                }
            }

            // 8. "sequenceFlow"-Elemente auslesen und Kanten erstellen
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

            // Optional: Ausgabe des gesamten Graphen, fokussiert auf Knoten und Kanten
            System.out.println("\nAlle Knoten im Graph:");
            graph.getNodes().forEach(node -> System.out.println(node));

            System.out.println("\nAlle Kanten im Graph:");
            graph.getEdges().forEach(edge -> System.out.println(edge));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methode zum Extrahieren der Lane aus einem Element (angepasst für LaneSet)
    // Methode zum Extrahieren der Lane aus einem Element
    private static Lane extractLane(Element element) {
        Lane lane = null;

        // Durchsuche alle LaneSet-Elemente im gesamten Dokument
        NodeList laneSetNodes = element.getOwnerDocument().getElementsByTagName("ns0:laneSet");

        // Überprüfen, ob es LaneSets gibt
        for (int i = 0; i < laneSetNodes.getLength(); i++) {
            Element laneSetElement = (Element) laneSetNodes.item(i);

            // Durchsuche alle Lanes innerhalb des LaneSets
            NodeList laneNodes = laneSetElement.getElementsByTagName("ns0:lane");
            if (laneNodes.getLength() > 0) {
                // Wähle die erste Lane (oder je nach Bedarf eine andere)
                Element laneElement = (Element) laneNodes.item(0);
                String laneId = laneElement.getAttribute("id");
                String laneName = laneElement.getAttribute("name");

                // Setze die Lane
                lane = new Lane(laneId, laneName);
                break;  // Wir nehmen nur die erste Lane aus dem ersten LaneSet, brechen die Schleife ab
            }
        }

        // Falls keine Lane gefunden wurde, setze eine Standard-Lane
        if (lane == null) {
            lane = new Lane("defaultLane", "Default Lane");
        }

        return lane;
    }


}
