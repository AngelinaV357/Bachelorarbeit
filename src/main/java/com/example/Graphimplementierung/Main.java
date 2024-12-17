//package com.example.Graphimplementierung;
//
//import org.w3c.dom.*;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import java.io.File;
//
//public class Main {
//    public static void main(String[] args) {
//        try {
//            // XML-Datei einlesen (beispielsweise "bpmn_example.xml")
//            File xmlFile = new File("src/main/resources/May_combine_ingredients.bpmn");
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document doc = builder.parse(xmlFile);
//
//            // Debug-Ausgabe, um zu überprüfen, ob das XML-Dokument geladen wurde
//            System.out.println("XML-Dokument geladen: " + doc.getDocumentElement().getTagName());
//
//            // BPMN-Graph erstellen
//            BPMNGraph graph = new BPMNGraph();
//
//            // Der Namensraum (hier als Beispiel: ns0)
//            String namespaceURI = "http://www.omg.org/spec/BPMN/20100524/MODEL";  // Beispiel-URI, muss mit deinem Namensraum übereinstimmen
//
//            // Alle Knoten (Events, Tasks, Gateways) extrahieren und hinzufügen
//            NodeList nodeList = doc.getElementsByTagNameNS(namespaceURI, "*");  // Alle Elemente im Dokument mit Namensraum
//            for (int i = 0; i < nodeList.getLength(); i++) {
//                org.w3c.dom.Node node = nodeList.item(i);  // Verwende den richtigen Node-Typ
//
//                // Extrahieren von Knoteninformationen
//                NamedNodeMap attributes = node.getAttributes();
//                Attr idNode = (Attr) attributes.getNamedItem("id");  // Cast auf Attr, nicht Node
//                if (idNode == null) {
//                    continue;  // Überspringe den Knoten, wenn er kein "id"-Attribut hat
//                }
//                String id = idNode.getValue();  // Verwende getValue() für das Attribut
//                String type = node.getNodeName();  // Der richtige Aufruf
//
//                if (type.equals(namespaceURI + ":startEvent") || type.equals(namespaceURI + ":endEvent")) {
//                    // Ereignisse (Start und End) hinzufügen
//                    String eventType = type.equals(namespaceURI + ":startEvent") ? "Start" : "End";
//                    EventNode eventNode = new EventNode(id, null, eventType);
//                    graph.addNode(eventNode);
//                }
//                else if (type.equals(namespaceURI + ":task")) {
//                    // Aktivität hinzufügen
//                    Node nameNode = (Node) attributes.getNamedItem("name");
//                    String activityType = (nameNode != null) ? ((org.w3c.dom.Node) nameNode).getNodeValue() : "Unbenannte Aktivität";
//                    ActivityNode activityNode = new ActivityNode(id, null, activityType);
//                    graph.addNode(activityNode);
//                }
//                else if (type.equals(namespaceURI + ":exclusiveGateway")) {
//                    // Gateway hinzufügen
//                    GatewayNode gatewayNode = new GatewayNode(id, null, "ExclusiveGateway");
//                    graph.addNode(gatewayNode);
//                }
//            }
//
//            // Alle Kanten (Verbindungen) extrahieren und hinzufügen
//            NodeList flowList = doc.getElementsByTagNameNS(namespaceURI, "sequenceFlow");
//            for (int i = 0; i < flowList.getLength(); i++) {
//                Element flowElement = (Element) flowList.item(i);
//                String id = flowElement.getAttribute("id");
//                String sourceRef = flowElement.getAttribute("sourceRef");
//                String targetRef = flowElement.getAttribute("targetRef");
//
//                // Finden der Quell- und Zielknoten
//                org.w3c.dom.Node sourceNode = findNodeById(graph, sourceRef);
//                org.w3c.dom.Node targetNode = findNodeById(graph, targetRef);
//
//                // Kante hinzufügen
//                Edge edge = new Edge(id, (Node) sourceNode, (Node) targetNode, null);
//                graph.addEdge(edge);
//            }
//
//            // Ausgabe des Graphen (Knoten und Kanten)
//            System.out.println("Knoten im Graph:");
//            for (Node node : graph.getNodes()) {
//                System.out.println("ID: " + node.getId() + ", Typ: " + node.getType() + ", Lane: " + node.getLane());
//            }
//
//            System.out.println("\nKanten im Graph:");
//            for (Edge edge : graph.getEdges()) {
//                System.out.println("Edge ID: " + edge.getId() + ", Quelle: " + edge.getSource().getId() + ", Ziel: " + edge.getTarget().getId() + ", Bedingung: " + edge.getCondition());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Methode, um einen Knoten nach seiner ID zu finden
//    private static org.w3c.dom.Node findNodeById(BPMNGraph graph, String id) {
//        for (Node node : graph.getNodes()) {
//            if (node.getId().equals(id)) {
//                return (org.w3c.dom.Node) node;
//            }
//        }
//        return null;
//    }
//}
//
//
