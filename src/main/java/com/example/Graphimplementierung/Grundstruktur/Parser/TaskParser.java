package com.example.Graphimplementierung.Grundstruktur.Parser;

import com.example.Graphimplementierung.Grundstruktur.Nodes.*;
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
                String eventType = "IntermediateCatchEvent"; // Standardmäßig als Catch Event
                String eventDefinitionRef = element.getAttribute("eventDefinitionRef");
                if (eventDefinitionRef != null && eventDefinitionRef.contains("Throw")) {
                    eventType = "IntermediateThrowEvent"; // Setzen auf Throw Event, wenn "Throw" erkannt wird
                }

                // Lane extrahieren und zuweisen (falls notwendig)
                Lane lane = extractLane(element, graph);

                // Erstelle den IntermediateEventNode und füge ihn dem Graphen hinzu
                ActivityNode intermediateEventNode = new ActivityNode(id, name, lane, eventType);
                graph.addNode(intermediateEventNode);
            }
        }

        // Zusätzliche Verarbeitung: Message Flows für Intermediate Events
        NodeList messageFlowNodes = doc.getElementsByTagName("ns0:messageFlow");
        for (int i = 0; i < messageFlowNodes.getLength(); i++) {
            Node node = messageFlowNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Extrahiere die ID, sourceRef und targetRef der Message Flow
                String id = element.getAttribute("id");
                String sourceRef = element.getAttribute("sourceRef");
                String targetRef = element.getAttribute("targetRef");

                // Hole die entsprechenden Knoten aus dem Graphen
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);

                if (sourceNode != null && targetNode != null) {
                    // Erstelle eine Kante, die den Message Flow darstellt
                    Edge messageFlowEdge = new Edge(id, sourceNode, targetNode, "MessageFlow");
                    graph.addEdge(messageFlowEdge);
                }
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

    static void processParticipants(Document doc, BPMNGraph graph) {
        NodeList participantNodes = doc.getElementsByTagName("ns0:participant");  // Tagname für Teilnehmer
        for (int i = 0; i < participantNodes.getLength(); i++) {
            Node node = participantNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");

                // Optional: Weitere Attribute wie "processRef" extrahieren, falls benötigt
                String processRef = element.getAttribute("processRef");

                // Lane extrahieren (wenn notwendig)
                Lane lane = extractLane(element, graph);

                // ParticipantNode erstellen
                ParticipantNode participantNode = new ParticipantNode(id, name, lane);
                graph.addNode(participantNode);  // Participant zum Graphen hinzufügen
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
