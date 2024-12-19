package com.example.Graphimplementierung.Grundstruktur.Parser;

import com.example.Graphimplementierung.Grundstruktur.Nodes.BPMNGraph;
import com.example.Graphimplementierung.Grundstruktur.Nodes.Lane;
import org.w3c.dom.*;

public class XMLParser {

    public BPMNGraph parseXML(Document doc, BPMNGraph graph) {
        try {
            //Verarbeitung der XML-Daten
            TaskParser.processActivityNodes(doc, "ns0:task", "Task", graph);
            TaskParser.processActivityNodes(doc, "ns0:subProcess", "SubProcess", graph);
            TaskParser.processStartEndEvents(doc, "ns0:startEvent", "StartEvent", graph);
            TaskParser.processStartEndEvents(doc, "ns0:endEvent", "EndEvent", graph);
            TaskParser.processUserTasks(doc, graph);
            TaskParser.processServiceTasks(doc, graph);
            TaskParser.processBusinessRuleTasks(doc, graph);
            TaskParser.processIntermediateEvents(doc, graph);

            GatewayParser.processGateways(doc, graph);

            DataParser.processDataObjects(doc, graph);
            DataParser.processDataInputs(doc, graph);

            FlowParser.processSequenceFlows(doc, graph);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return graph;
    }

    static Lane extractLane(Element element, BPMNGraph graph) {
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
}
