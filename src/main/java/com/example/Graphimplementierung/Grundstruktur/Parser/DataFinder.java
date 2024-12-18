package com.example.Graphimplementierung.Grundstruktur.Parser;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Lane;
import com.example.Graphimplementierung.Grundstruktur.Nodes.ActivityNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static com.example.Graphimplementierung.Grundstruktur.Parser.XMLParser.extractLane;

public class DataFinder {

    static void processDataObjects(Document doc, BPMNGraph graph) {
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


    static void processDataInputs(Document doc, BPMNGraph graph) {
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
}
