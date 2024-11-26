package com.example;

import org.camunda.bpm.model.bpmn.instance.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class Hilfsmethoden {
    // --- Helper Methods ---
    static String getName(FlowNode node) {
        if (node instanceof Activity activity) {
            String name = activity.getName();
            if (name == null || name.isEmpty()) {
                System.out.println("Warnung: Aktivität ohne Namen gefunden.");
                return "Unbekannte Aktivität";
            }
            return name;
        } else if (node instanceof StartEvent) {
            return "Start";
        } else if (node instanceof EndEvent) {
            return "Ende";
        } else if (node instanceof ExclusiveGateway) {
            return "XOR-Gateway";
        } else if (node instanceof ParallelGateway) {
            return "UND-Gateway";
        } else {
            System.out.println("Warnung: Unbekannter Knotentyp: " + node.getClass().getSimpleName());
        }
        return "Unbekannter Knoten";
    }

//    public static String transformRoleToSBVR(String roleName) {
//        return roleName + " is a role.";
//    }

    static String getParticipantName(String nodeId, Map<String, String> participants) {
        // Hier wird angenommen, dass 'participants' eine Map ist, die nodeId -> participantName abbildet
        return participants.getOrDefault(nodeId, "unbekannter Teilnehmer");
    }

    static String getRoleForNode(FlowNode node, Collection<Lane> lanes) {
        for (Lane lane : lanes) {
            if (lane.getFlowNodeRefs().contains(node)) {
                return lane.getName();
            }
        }
        return "Unbekannte Rolle";
    }

    // Hilfsmethode zum Abrufen der Bedingung für einen Sequenzfluss
    static String getCondition(SequenceFlow flow) {
        ConditionExpression condition = flow.getConditionExpression();
        if (condition != null) {
            return condition.getTextContent(); // Rückgabe des Textinhalts der Bedingung
        }
        return "keine Bedingung"; // Falls keine Bedingung vorhanden ist
    }

}
