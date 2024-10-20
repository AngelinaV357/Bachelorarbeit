package com.example;

import java.io.File;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;

public class XMLReader {
    public static void main(String[] args) {
        // Pfad zur XML-Datei
        String filePath = "C:\\Users\\pinki\\OneDrive\\Desktop\\Bachelor\\XML Dateien\\Car Wash Process.bpmn"; 
        // Lese die XML-Datei
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(filePath));

        // Beispiel: Zugriff auf Prozesse im Modell
        for (Process process : modelInstance.getModelElementsByType(Process.class)) {
            System.out.println("Prozessname: " + process.getName());
        }
    }
}