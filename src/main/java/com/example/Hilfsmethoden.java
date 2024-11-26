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

    public static String transformRoleToSBVR(String roleName) {
        return roleName + " is a role.";
    }

    static String getParticipantName(String nodeId, Map<String, String> participants) {
        // Hier wird angenommen, dass 'participants' eine Map ist, die nodeId -> participantName abbildet
        return participants.getOrDefault(nodeId, "unbekannter Teilnehmer");
    }

    static void writeToFile(String content, File file, String type) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
            System.out.println(type + " wurden erfolgreich in die Datei geschrieben: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Fehler beim Schreiben in die Datei: " + e.getMessage());
        }
    }

    // Hilfsmethode zur Klassifizierung von Actor, Action und Object
    static void classifyFlow(String actor, String action, String object) {
        // Klare Ausgabe für Actor, Action und Object
        System.out.println("Actor: " + actor + ", Action: " + action + ", Object: " + object);
    }

    static String getRoleForNode(FlowNode node, Collection<Lane> lanes) {
        for (Lane lane : lanes) {
            if (lane.getFlowNodeRefs().contains(node)) {
                return lane.getName();
            }
        }
        return "Unbekannte Rolle";
    }
}
