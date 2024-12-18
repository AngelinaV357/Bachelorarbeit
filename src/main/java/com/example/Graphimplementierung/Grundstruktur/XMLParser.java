package com.example.Graphimplementierung.Grundstruktur;

import com.example.Graphimplementierung.Grundstruktur.Nodes.*;
import org.w3c.dom.*;
import org.w3c.dom.Node;

public class XMLParser {

    public BPMNGraph parseXML(Document doc, BPMNGraph graph) {
        try {
            // Beispielhafte Verarbeitung der XML-Daten
            processActivityNodes(doc, "ns0:task", "Task", graph);
            processActivityNodes(doc, "ns0:subProcess", "SubProcess", graph);
            processGateways(doc, graph);
            processStartEndEvents(doc, "ns0:startEvent", "StartEvent", graph);
            processStartEndEvents(doc, "ns0:endEvent", "EndEvent", graph);
            processDataObjects(doc, graph);
            processDataInputs(doc, graph);
            processUserTasks(doc, graph);
            processServiceTasks(doc, graph);
            processBusinessRuleTasks(doc, graph); // Hinzugefügte Methode für BusinessRuleTask
            processIntermediateEvents(doc, graph); // Hinzugefügte Methode für IntermediateEvents
            processAssociations(doc, graph);
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

                // Lane extrahieren und zuweisen
                Lane lane = extractLane(element, graph);

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
                String gatewayType = null;

                // Bestimme den Typ des Gateways basierend auf dem Tag-Namen
                if (tagName.equals("ns0:exclusiveGateway")) {
                    gatewayType = "ExclusiveGateway";
                } else if (tagName.equals("ns0:parallelGateway")) {
                    gatewayType = "ParallelGateway";
                } else if (tagName.equals("ns0:eventBasedGateway")) {
                    gatewayType = "EventBasedGateway";
                }

                // Wenn es ein Gateway ist, erstellen wir einen neuen Knoten
                if (gatewayType != null) {
                    String id = element.getAttribute("id");
                    String name = element.getAttribute("name");

                    // Falls kein Name vorhanden ist, verwenden wir den Standardnamen
                    if (name == null || name.isEmpty()) {
                        name = gatewayType + " Gateway";
                    }

                    // Entferne "Gateway" am Ende des Namens, falls es bereits vorhanden ist
                    if (name.endsWith("Gateway")) {
                        name = name.substring(0, name.length() - "Gateway".length()).trim();
                    }

                    // Lane extrahieren und zuweisen
                    Lane lane = extractLane(element, graph);

                    // GatewayNode erstellen
                    ActivityNode gatewayNode = new ActivityNode(id, name, lane, gatewayType);
                    graph.addNode(gatewayNode);
                }
            }
        }
    }

    private void processIntermediateEvents(Document doc, BPMNGraph graph) {
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

                // Lane extrahieren und zuweisen
                Lane lane = extractLane(element, graph);

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

                // Lane extrahieren und zuweisen
                Lane lane = extractLane(element, graph);

                // DataObjectNode erstellen
                ActivityNode dataObjectNode = new ActivityNode(id, name, lane, activityType);
                graph.addNode(dataObjectNode);
            }
        }
    }

    private void processBusinessRuleTasks(Document doc, BPMNGraph graph) {
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


    private void processDataInputs(Document doc, BPMNGraph graph) {
        NodeList dataInputNodes = doc.getElementsByTagName("ns0:dataInput");
        for (int i = 0; i < dataInputNodes.getLength(); i++) {
            Node node = dataInputNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");
                String name = element.getAttribute("name");
                String activityType = "DataInput";

                // Lane extrahieren und zuweisen
                Lane lane = extractLane(element, graph);

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

                // Lane extrahieren und zuweisen
                Lane lane = extractLane(element, graph);

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

                // Lane extrahieren und zuweisen
                Lane lane = extractLane(element, graph);

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

    private void processDataAssociations(Document doc, BPMNGraph graph) {
        // Verarbeite dataOutputAssociation
        NodeList dataOutputAssociations = doc.getElementsByTagName("ns0:dataOutputAssociation");
        for (int i = 0; i < dataOutputAssociations.getLength(); i++) {
            Node node = dataOutputAssociations.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Extrahiere die relevant Attribute wie id und sourceRef/targetRef
                String id = element.getAttribute("id");
                String sourceRef = element.getElementsByTagName("ns0:sourceRef").item(0).getTextContent();
                String targetRef = element.getElementsByTagName("ns0:targetRef").item(0).getTextContent();
                String condition = "";  // Keine Bedingung für DataOutputAssociations

                // Hole die Knoten für die Quelle und das Ziel
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);

                // Überprüfen, ob die Knoten existieren
                if (sourceNode != null && targetNode != null) {
                    // Kante erstellen und zum Graphen hinzufügen
                    Edge edge = new Edge(id, sourceNode, targetNode, condition);
                    graph.addEdge(edge);
                } else {
                    System.out.println("Warnung: Kante konnte nicht erstellt werden. Knoten nicht gefunden.");
                }
            }
        }

        // Verarbeite dataInputAssociation
        NodeList dataInputAssociations = doc.getElementsByTagName("ns0:dataInputAssociation");
        for (int i = 0; i < dataInputAssociations.getLength(); i++) {
            Node node = dataInputAssociations.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Extrahiere die relevant Attribute wie id und sourceRef/targetRef
                String id = element.getAttribute("id");
                String sourceRef = element.getElementsByTagName("ns0:sourceRef").item(0).getTextContent();
                String targetRef = element.getElementsByTagName("ns0:targetRef").item(0).getTextContent();
                String condition = "";  // Keine Bedingung für DataInputAssociations

                // Hole die Knoten für die Quelle und das Ziel
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);

                // Überprüfen, ob die Knoten existieren
                if (sourceNode != null && targetNode != null) {
                    // Kante erstellen und zum Graphen hinzufügen
                    Edge edge = new Edge(id, sourceNode, targetNode, condition);
                    graph.addEdge(edge);
                } else {
                    System.out.println("Warnung: Kante konnte nicht erstellt werden. Knoten nicht gefunden.");
                }
            }
        }
    }



    private void processAssociations(Document doc, BPMNGraph graph) {
        // Suche nach den DataInputAssociations
        NodeList dataInputAssociationNodes = doc.getElementsByTagName("ns0:dataInputAssociation");
        for (int i = 0; i < dataInputAssociationNodes.getLength(); i++) {
            Element associationElement = (Element) dataInputAssociationNodes.item(i);

            String sourceRef = associationElement.getAttribute("sourceRef");
            String targetRef = associationElement.getAttribute("targetRef");
            String condition = associationElement.getAttribute("condition");

            // Hole die Knoten aus dem Graphen
            com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
            com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);


            if (sourceNode != null && targetNode != null) {
                // Erstelle eine neue Kante zwischen den Knoten
                Edge edge = new Edge("dataInputAssociation_" + i, sourceNode, targetNode, condition);
                graph.addEdge(edge);
            }
        }

        // Suche nach den DataOutputAssociations
        NodeList dataOutputAssociationNodes = doc.getElementsByTagName("ns0:dataOutputAssociation");
        for (int i = 0; i < dataOutputAssociationNodes.getLength(); i++) {
            Element associationElement = (Element) dataOutputAssociationNodes.item(i);

            String sourceRef = associationElement.getAttribute("sourceRef");
            String targetRef = associationElement.getAttribute("targetRef");
            String condition = associationElement.getAttribute("condition");

            // Hole die Knoten aus dem Graphen
            com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
            com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);


            if (sourceNode != null && targetNode != null) {
                // Erstelle eine neue Kante zwischen den Knoten
                Edge edge = new Edge("dataOutputAssociation_" + i, sourceNode, targetNode, condition);
                graph.addEdge(edge);
            }
        }
    }


    private static Lane extractLane(Element element, BPMNGraph graph) {
        // Hole alle LaneSets aus dem Dokument
        NodeList laneSetNodes = element.getOwnerDocument().getElementsByTagName("ns0:laneSet");

        // Durchlaufe jedes LaneSet und suche nach der entsprechenden Lane
        for (int i = 0; i < laneSetNodes.getLength(); i++) {
            Element laneSetElement = (Element) laneSetNodes.item(i);
            NodeList laneNodes = laneSetElement.getElementsByTagName("ns0:lane");

            // Durchlaufe jede Lane innerhalb des LaneSets
            for (int j = 0; j < laneNodes.getLength(); j++) {
                Element laneElement = (Element) laneNodes.item(j);
                String laneId = laneElement.getAttribute("id");
                String laneName = laneElement.getAttribute("name");

                // Durchlaufe alle FlowNodeRefs der aktuellen Lane
                NodeList flowNodeRefs = laneElement.getElementsByTagName("ns0:flowNodeRef");

                // Prüfe, ob der aktuelle Knoten eine Referenz in der FlowNodeRef hat
                for (int k = 0; k < flowNodeRefs.getLength(); k++) {
                    Element flowNodeRefElement = (Element) flowNodeRefs.item(k);
                    String flowNodeId = flowNodeRefElement.getTextContent().trim(); // ID des FlowNodeRefs

                    // Wenn die ID des aktuellen Knotens mit einer FlowNodeRef übereinstimmt, gib die Lane zurück
                    if (element.getAttribute("id").equals(flowNodeId)) {
                        // Hier wird die passende Lane erstellt und zurückgegeben
                        return new Lane(laneId, laneName);
                    }
                }
            }
        }

        // Wenn keine Lane gefunden wurde, gib eine "Unbekannte Lane" zurück
        return new Lane("unknown", "Unknown Lane");
    }
    // Methode zum Entfernen von Zeilenumbrüchen und Tabulatoren
    private static String cleanText(String text) {
        // Entfernen von Zeilenumbrüchen, Carriage-Returns und Tabulatoren
        return text.replaceAll("[\\r\\n\\t]", " ").trim();
    }
}
