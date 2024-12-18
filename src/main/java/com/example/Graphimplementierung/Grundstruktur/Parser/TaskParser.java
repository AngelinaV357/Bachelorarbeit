package com.example.Graphimplementierung.Grundstruktur.Parser;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Lane;
import com.example.Graphimplementierung.Grundstruktur.Nodes.ActivityNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static com.example.Graphimplementierung.Grundstruktur.Parser.XMLParser.extractLane;

public class TaskParser {

    static void processActivityNodes(Document doc, String tagName, String activityType, BPMNGraph graph) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");

                // Lane extrahieren und zuweisen
                Lane lane = extractLane(element, graph);

                // ActivityNode erstellen
                ActivityNode activityNode = new ActivityNode(id, name, lane, activityType);
                graph.addNode(activityNode);
            }
        }
    }

    static void processIntermediateEvents(Document doc, BPMNGraph graph) {
        // Suche nach den IntermediateCatchEvent-Knoten im XML-Dokument
        NodeList intermediateEventNodes = doc.getElementsByTagName("ns0:intermediateCatchEvent");
        for (int i = 0; i < intermediateEventNodes.getLength(); i++) {
            Node node = intermediateEventNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Extrahiere die ID und den Namen des Intermediate Events
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");

                // Falls kein Name vorhanden ist, setzen wir einen Standardnamen
                if (name == null || name.isEmpty()) {
                    name = "Intermediate Catch Event";
                }

                // Überprüfen, ob es sich um ein IntermediateCatchEvent oder ein IntermediateThrowEvent handelt
                String eventType = "IntermediateCatchEvent";  // Standardmäßig als Catch Event

                // Falls der eventDefinitionRef auf ein "Throw" Event hinweist, setzen wir den Typ auf "IntermediateThrowEvent"
                String eventDefinitionRef = element.getAttribute("eventDefinitionRef");
                if (eventDefinitionRef != null && eventDefinitionRef.contains("Throw")) {
                    eventType = "IntermediateThrowEvent";  // Setzen auf Throw Event, wenn "Throw" erkannt wird
                }

                // Lane extrahieren und zuweisen (falls notwendig)
                Lane lane = extractLane(element, graph);

                // Erstelle den IntermediateEventNode und füge ihn dem Graphen hinzu
                ActivityNode intermediateEventNode = new ActivityNode(id, name, lane, eventType);
                graph.addNode(intermediateEventNode);

                // Für das Throw Event könnte es auch spezifische Logik für die Behandlung von ausgehenden Kanten geben
                // Hier könnte man zusätzliche Schritte hinzufügen, wenn nötig
            }
        }
    }

    static void processStartEndEvents(Document doc, String tagName, String eventType, BPMNGraph graph) {
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

                // Lane extrahieren und zuweisen
                Lane lane = extractLane(element, graph);

                // EventNode erstellen
                ActivityNode eventNode = new ActivityNode(id, name, lane, eventType);
                graph.addNode(eventNode);
            }
        }
    }

    static void processBusinessRuleTasks(Document doc, BPMNGraph graph) {
        // Suche nach den BusinessRuleTask-Knoten im XML-Dokument
        NodeList businessRuleTaskNodes = doc.getElementsByTagName("ns0:businessRuleTask");
        for (int i = 0; i < businessRuleTaskNodes.getLength(); i++) {
            Node node = businessRuleTaskNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Extrahiere die ID und den Namen der BusinessRuleTask
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");

                // Falls kein Name vorhanden ist, setzen wir einen Standardnamen
                if (name == null || name.isEmpty()) {
                    name = "Business Rule Task";
                }

                // Optional: Weitere Attribute wie 'completionQuantity', 'implementation' usw. extrahieren, wenn notwendig
                String completionQuantity = element.getAttribute("completionQuantity");
                String implementation = element.getAttribute("implementation");

                // Lane extrahieren und zuweisen
                Lane lane = extractLane(element, graph);

                // Erstelle den BusinessRuleTaskNode und füge ihn dem Graphen hinzu
                ActivityNode businessRuleTaskNode = new ActivityNode(id, name, lane, "BusinessRuleTask");
                graph.addNode(businessRuleTaskNode);
            }
        }
    }

    static void processUserTasks(Document doc, BPMNGraph graph) {
        NodeList userTaskNodes = doc.getElementsByTagName("ns0:userTask");
        for (int i = 0; i < userTaskNodes.getLength(); i++) {
            Node node = userTaskNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");
                String activityType = "UserTask";

                // Lane extrahieren und zuweisen
                Lane lane = extractLane(element, graph);

                // UserTaskNode erstellen
                ActivityNode userTaskNode = new ActivityNode(id, name, lane, activityType);
                graph.addNode(userTaskNode);
            }
        }
    }

    static void processServiceTasks(Document doc, BPMNGraph graph) {
        NodeList serviceTaskNodes = doc.getElementsByTagName("ns0:serviceTask");
        for (int i = 0; i < serviceTaskNodes.getLength(); i++) {
            Node node = serviceTaskNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");
                String activityType = "ServiceTask";

                // Lane extrahieren und zuweisen
                Lane lane = extractLane(element, graph);

                // ServiceTaskNode erstellen
                ActivityNode serviceTaskNode = new ActivityNode(id, name, lane, activityType);
                graph.addNode(serviceTaskNode);
            }
        }
    }

}
