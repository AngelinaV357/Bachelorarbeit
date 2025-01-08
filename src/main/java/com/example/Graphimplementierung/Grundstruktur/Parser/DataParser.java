package com.example.Graphimplementierung.Grundstruktur.Parser;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Nodes.DataNode;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Lane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

}
