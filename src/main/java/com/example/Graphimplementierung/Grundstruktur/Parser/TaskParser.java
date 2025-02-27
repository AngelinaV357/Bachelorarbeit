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
                TaskNode taskNode = new TaskNode(id, name, lane, activityType);
                graph.addNode(taskNode);
            }
        }
    }



    static void processIntermediateEvents(Document doc, BPMNGraph graph) {
        // Verarbeite alle Intermediate Catch Events
        NodeList intermediateEventNodes = doc.getElementsByTagName("ns0:intermediateCatchEvent");
        for (int i = 0; i < intermediateEventNodes.getLength(); i++) {
            Node node = intermediateEventNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Extrahiere die ID und den Namen des Intermediate Events
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");

                // Falls kein Name vorhanden ist, setzen wir einen Standardnamen
                if (name.isEmpty()) {
                    name = "Intermediate Catch Event";
                }

                // Bestimme den Event-Typ (Catch oder Throw Event)
                String eventType = "IntermediateCatchEvent"; // Standardmäßig Catch Event
                String eventDefinitionRef = element.getAttribute("eventDefinitionRef");
                if (eventDefinitionRef.contains("Throw")) {
                    eventType = "IntermediateThrowEvent"; // Setze auf Throw Event, wenn "Throw" erkannt wird
                }

                // Bestimme den Sub-Typ (Timer, Message, etc.)
                String eventSubType = "Unknown"; // Standardwert
                NodeList childNodes = element.getChildNodes(); // Durchsuche den Scope
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        String localName = childNode.getLocalName();
                        if ("timerEventDefinition".equals(localName)) {
                            eventSubType = "Timer"; // Setze eventSubType auf "Timer", wenn timerEventDefinition gefunden wird
                        } else if ("messageEventDefinition".equals(localName)) {
                            eventSubType = "Message";
                        }
                    }
                }

                // Extrahiere Lane (falls notwendig)
                Lane lane = extractLane(element, graph);

                // Erstelle den IntermediateNode und füge ihn dem Graphen hinzu
                IntermediateNode intermediateNode = new IntermediateNode(id, name, lane, eventType, eventSubType);
                graph.addNode(intermediateNode);
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

                // Überprüfen, ob das Endevent ein EscalationEndEvent ist, basierend auf dem Vorhandensein von <escalationEventDefinition>
                boolean isEscalation = element.getElementsByTagName("escalationEventDefinition").getLength() > 0;

                // EventNode erstellen
                if (eventType.equals("StartEvent")) {
                    StartEventNode startEventNode = new StartEventNode(id, name, lane);
                    graph.addNode(startEventNode);
                } else if (eventType.equals("EndEvent")) {
                    EndEventNode endEventNode = new EndEventNode(id, name, lane, isEscalation);
                    graph.addNode(endEventNode);
                }
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
                TaskNode businessRuleTaskNode = new TaskNode(id, name, lane, "BusinessRuleTask");
                graph.addNode(businessRuleTaskNode);
            }
        }
    }

    static void processBoundaryEventNodes(Document doc, BPMNGraph graph) {
        NodeList boundaryEventNodes = doc.getElementsByTagName("ns0:boundaryEvent");
        for (int i = 0; i < boundaryEventNodes.getLength(); i++) {
            Node node = boundaryEventNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");

                // Name des Boundary Event extrahieren
                String name = element.getAttribute("name");

                // Falls der Name null oder leer ist, überspringe den Knoten
                if (name == null || name.trim().isEmpty()) {
                    continue;
                }

                // Hole die Lane des Boundary Events (falls vorhanden)
                Lane lane = extractLane(element, graph);  // Deine Logik für Lane extrahieren

                // Extrahiere die Bedingung des Boundary Events
                String condition = element.getAttribute("condition");  // Bedingung wie "Delivery Problems"

                // BoundaryEventNode erstellen und zum Graphen hinzufügen
                BoundaryEventNode boundaryEventNode = new BoundaryEventNode(id, name, lane);
                graph.addNode(boundaryEventNode);  // Füge den BoundaryEventNode zum Graphen hinzu
            }
        }
    }


    static void processParticipants(Document doc, BPMNGraph graph) {
        // Suche nach den Participant-Knoten im XML-Dokument
        NodeList participantNodes = doc.getElementsByTagName("ns0:participant");

        for (int i = 0; i < participantNodes.getLength(); i++) {
            Node node = participantNodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Extrahiere die ID und den Namen des Participants
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");

                // Falls kein Name vorhanden ist, setzen wir einen Standardnamen
                if (name == null || name.isEmpty()) {
                    name = "Participant";
                }

                // Extrahiere optionale Attribute, z. B. Lane, falls vorhanden
                Lane lane = extractLane(element, graph);

                // Erstelle den ParticipantNode und füge ihn dem Graphen hinzu
                ParticipantNode participantNode = new ParticipantNode(id, name, lane);
                graph.addNode(participantNode);
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
                TaskNode userTaskNode = new TaskNode(id, name, lane, activityType);
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
                TaskNode serviceTaskNode = new TaskNode(id, name, lane, activityType);
                graph.addNode(serviceTaskNode);
            }
        }
    }
}
