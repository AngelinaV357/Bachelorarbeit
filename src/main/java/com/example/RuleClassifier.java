package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RuleClassifier {
    public static void main(String[] args) {
        File inputFile = new File("src/main/resources/output.txt"); // Pfad zur Eingabedatei
        List<String> rules = new ArrayList<>();

        // Einlesen der Datei und Verarbeiten der Regeln
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Die Zeilen im Format "Actor: [Name], Action: [Name], Object: [Name]" verarbeiten
                if (line.startsWith("Actor: ")) {
                    String[] parts = line.split(", ");
                    if (parts.length == 3) {
                        String actor = parts[0].replace("Actor: ", "").trim();
                        String action = parts[1].replace("Action: ", "").trim();
                        String object = parts[2].replace("Object: ", "").trim();

                        // Regel erstellen
                        String rule = "Transitive Verb: Regel beteiligt sich am Geschäftsprozess\n" +
                                "Actor: " + actor + "\n" +
                                "Action: " + action + "\n" +
                                "Object: " + object + "\n";
                        rules.add(rule);
                        System.out.println(rule); // Ausgabe in der Konsole
                    }
                }
            }

            // Hier kannst du auch die gesammelten Regeln weiterverarbeiten oder speichern

        } catch (IOException e) {
            System.out.println("Fehler beim Lesen der Datei: " + e.getMessage());
        }
    }
}
