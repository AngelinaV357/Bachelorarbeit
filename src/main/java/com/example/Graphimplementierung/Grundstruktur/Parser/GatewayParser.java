package com.example.Graphimplementierung.Grundstruktur.Parser;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Nodes.GatewayNode;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Lane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static com.example.Graphimplementierung.Grundstruktur.Parser.XMLParser.extractLane;

public class GatewayParser {

    static void processGateways(Document doc, BPMNGraph graph) {
        NodeList gatewayNodes = doc.getElementsByTagName("*");
        for (int i = 0; i < gatewayNodes.getLength(); i++) {
            Node node = gatewayNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String tagName = element.getTagName();
                String gatewayType = null;

                // Bestimme den Typ des Gateways basierend auf dem Tag-Namen
                if (tagName.equals("ns0:exclusiveGateway")) {
                    gatewayType = "Exclusive";
                } else if (tagName.equals("ns0:parallelGateway")) {
                    gatewayType = "Parallel";
                } else if (tagName.equals("ns0:eventBasedGateway")) {
                    gatewayType = "EventBased";
                }

                // Wenn es ein Gateway ist, erstellen wir einen neuen Knoten
                if (gatewayType != null) {
                    String id = element.getAttribute("id");
                    String name = element.getAttribute("name");

                    // Falls kein Name vorhanden ist, verwenden wir den Standardnamen
                    if (name == null || name.isEmpty()) {
                        name = gatewayType + " Gateway";
                    }

                    // Lane extrahieren und zuweisen
                    Lane lane = extractLane(element, graph);

                    // GatewayNode erstellen
                    GatewayNode gatewayNode = new GatewayNode(id, name, lane, gatewayType);
                    graph.addNode(gatewayNode);
                }
            }
        }
    }
}
