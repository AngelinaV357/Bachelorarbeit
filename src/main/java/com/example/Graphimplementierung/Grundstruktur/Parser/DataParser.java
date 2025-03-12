package com.example.Graphimplementierung.Grundstruktur.Parser;

import com.example.Graphimplementierung.Grundstruktur.Nodes.*;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Lane;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Collection;

import static com.example.Graphimplementierung.Grundstruktur.Parser.XMLParser.extractLane;

public class DataParser {

    static void processDataNodes(Document doc, String tagName, String dataType, BPMNGraph graph) {
        NodeList dataNodes = doc.getElementsByTagName(tagName);
        for (int i = 0; i < dataNodes.getLength(); i++) {
            Node node = dataNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String id = element.getAttribute("id");

                // Stelle sicher, dass der Name korrekt extrahiert wird
                String name = element.getAttribute("name");

                // Wenn der Name leer oder null ist, überspringe diese Node
                if (name == null || name.trim().isEmpty()) {
                    continue; // Gehe zum nächsten Element, ohne eine Node zu erstellen
                }

                // Lane extrahieren und zuweisen
                Lane lane = extractLane(element, graph);

                // DataNode erstellen und zum Graph hinzufügen
                DataNode dataNode = new DataNode(id, name, lane, dataType);
                graph.addNode(dataNode);
            }
        }
    }


    public static void parseData(Document doc, BPMNGraph graph) {
        // Verarbeitung von DataObjects
        DataParser.processDataNodes(doc, "ns0:dataObject", "DataObject", graph);

        // Verarbeitung von DataInputs
        DataParser.processDataNodes(doc, "ns0:dataInput", "DataInput", graph);
    }

    static void processTextAnnotationsAndAssociations(Document doc, BPMNGraph graph, BpmnModelInstance modelInstance) {
        // 1. Verarbeite die TextAnnotations und erstelle DataNodes
        NodeList textAnnotations = doc.getElementsByTagName("ns0:textAnnotation");
        for (int i = 0; i < textAnnotations.getLength(); i++) {
            Node node = textAnnotations.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                String id = element.getAttribute("id");
                NodeList textElements = element.getElementsByTagName("ns0:text");
                String name = textElements.getLength() > 0 ? textElements.item(0).getTextContent() : null;

                if (name == null || name.trim().isEmpty()) {
                    continue; // Falls leer, überspringen
                }

                // Lane extrahieren und zuweisen
                Lane lane = extractLane(element, graph);

                // DataNode für die TextAnnotation erstellen
                DataNode dataNode = new DataNode(id, name, lane, "TextAnnotation");
                graph.addNode(dataNode);
            }
        }

        // 2. Verarbeite die DataObject-Elemente
        NodeList dataObjects = doc.getElementsByTagName("ns0:dataObject");
        for (int i = 0; i < dataObjects.getLength(); i++) {
            Node node = dataObjects.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                String id = element.getAttribute("id");
                String name = element.getAttribute("name");

                // DataNode für das DataObject erstellen
                DataNode dataNode = new DataNode(id, name, null, "DataObject"); // Lane zuweisen, wenn benötigt
                graph.addNode(dataNode);
            }
        }

        // 3. Verarbeite die DataObjectReference-Elemente (Änderungen hier)
        NodeList dataObjectReferences = doc.getElementsByTagName("ns0:dataObjectReference");
        for (int i = 0; i < dataObjectReferences.getLength(); i++) {
            Node node = dataObjectReferences.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                String id = element.getAttribute("id");
                String dataObjectId = element.getAttribute("dataObjectRef");

                // Finde das eigentliche DataObject im Graph
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node dataObjectNode = graph.getNodeById(dataObjectId);
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node dataObjectRefNode = graph.getNodeById(id);

                // Falls die Referenz noch nicht vorhanden ist, füge sie hinzu
                if (dataObjectRefNode == null && dataObjectNode != null) {
                    dataObjectRefNode = new DataNode(id, dataObjectNode.getName(), null, "DataObjectReference");
                    graph.addNode(dataObjectRefNode);
                }

                // Verknüpfung zwischen DataObject und DataObjectReference, ohne Selbstreferenz
                if (dataObjectNode != null && dataObjectRefNode != null && !dataObjectNode.getId().equals(dataObjectRefNode.getId())) {
                    // Verhindere doppelte Kanten
                    if (!graph.hasEdge("reference_" + id, dataObjectRefNode, dataObjectNode)) {
                        graph.addEdge(new DataEdge("reference_" + id, dataObjectRefNode, dataObjectNode));
                    }
                }
            }
        }

        // 4. Verarbeite die Association-Elemente, um Source und Target zu finden
        NodeList associations = doc.getElementsByTagName("ns0:association");
        for (int i = 0; i < associations.getLength(); i++) {
            Node node = associations.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                String id = element.getAttribute("id");
                String sourceRef = element.getAttribute("sourceRef");
                String targetRef = element.getAttribute("targetRef");

                // Finde die Knoten im Graph basierend auf den IDs
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceRef);
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetRef);

                // Verhindere Selbstreferenzen und doppelte Kanten
                if (sourceNode != null && targetNode != null && !sourceNode.getId().equals(targetNode.getId())) {
                    if (!graph.hasEdge(id, sourceNode, targetNode)) {
                        graph.addEdge(new DataEdge(id, sourceNode, targetNode));
                    }
                }
            }
        }

        // 5. Verarbeite DataInputs und DataOutputs aus den Aktivitäten
        Collection<Activity> activities = modelInstance.getModelElementsByType(Activity.class);
        for (Activity activity : activities) {
            String activityId = activity.getId();
            com.example.Graphimplementierung.Grundstruktur.Nodes.Node activityNode = graph.getNodeById(activityId);

            if (activityNode == null) {
                continue; // Falls Aktivität nicht im Graph existiert, überspringen
            }

            // 5.1 Eingehende Datenverbindungen (DataInputAssociations)
            for (DataInputAssociation inputAssociation : activity.getDataInputAssociations()) {
                BaseElement sourceElement = inputAssociation.getSources().iterator().next();
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node sourceNode = graph.getNodeById(sourceElement.getId());

                // Verhindere doppelte Kanten
                if (sourceNode != null && !graph.hasEdge("input_" + sourceElement.getId(), sourceNode, activityNode)) {
                    graph.addEdge(new DataEdge("input_" + sourceElement.getId(), sourceNode, activityNode));
                }
            }

            // 5.2 Ausgehende Datenverbindungen (DataOutputAssociations)
            for (DataOutputAssociation outputAssociation : activity.getDataOutputAssociations()) {
                BaseElement targetElement = outputAssociation.getTarget();
                com.example.Graphimplementierung.Grundstruktur.Nodes.Node targetNode = graph.getNodeById(targetElement.getId());

                // Verhindere doppelte Kanten
                if (targetNode != null && !graph.hasEdge("output_" + targetElement.getId(), activityNode, targetNode)) {
                    graph.addEdge(new DataEdge("output_" + targetElement.getId(), activityNode, targetNode));
                }

                // Verarbeitung von DataObjects als Outputs
                if (targetElement instanceof DataObjectReference) {
                    DataObjectReference dataObjectRef = (DataObjectReference) targetElement;
                    String dataObjectName = dataObjectRef.getAttributeValue("name");

                    // Kante für DataObjectReference hinzufügen
                    com.example.Graphimplementierung.Grundstruktur.Nodes.Node dataObjectNode = graph.getNodeById(dataObjectRef.getId());
                    if (dataObjectNode != null && !graph.hasEdge("output_" + dataObjectRef.getId(), activityNode, dataObjectNode)) {
                        graph.addEdge(new DataEdge("output_" + dataObjectRef.getId(), activityNode, dataObjectNode));
                    }
                }

                // Wenn das Ziel ein DataInput ist
                if (targetElement instanceof DataInput) {
                    DataInput dataInput = (DataInput) targetElement;
                    com.example.Graphimplementierung.Grundstruktur.Nodes.Node dataInputNode = graph.getNodeById(dataInput.getId());

                    // Kante für DataInput hinzufügen
                    if (dataInputNode != null && !graph.hasEdge("output_" + dataInput.getId(), activityNode, dataInputNode)) {
                        graph.addEdge(new DataEdge("output_" + dataInput.getId(), activityNode, dataInputNode));
                    }
                }
            }
        }
    }

}