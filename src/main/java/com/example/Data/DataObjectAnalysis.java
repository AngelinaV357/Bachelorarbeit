package com.example.Data;

import org.camunda.bpm.model.bpmn.instance.DataInput;
import org.camunda.bpm.model.bpmn.instance.DataObjectReference;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.util.Collection;

public class DataObjectAnalysis {

    public void analyzeDataObjects(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // 2. Alle DataObjectReferences finden
        Collection<DataObjectReference> dataObjectReferences = modelInstance.getModelElementsByType(DataObjectReference.class);
        for (DataObjectReference dataObjectReference : dataObjectReferences) {
            // Ausgabe nur des Namens ohne die ID
            sbvrOutput.append("Gefundene DataObjectReferences: ").append(dataObjectReference.getAttributeValue("name")).append("\n");
        }

        // 3. Alle DataInput-Elemente finden
        Collection<DataInput> dataInputs = modelInstance.getModelElementsByType(DataInput.class);
        for (DataInput dataInput : dataInputs) {
            // Ausgabe nur des Namens ohne die ID
            sbvrOutput.append("Gefundene DataInputs: ").append(dataInput.getAttributeValue("name")).append("\n");
        }

        // 4. Alle UserTasks oder ServiceTasks finden
        Collection<Task> tasks = modelInstance.getModelElementsByType(Task.class);
        sbvrOutput.append("Gefundene Aufgaben, die mit DataObjekten oder DataInputs verbunden sind:\n");
        for (Task task : tasks) {
            sbvrOutput.append("- Name: ").append(task.getName()).append("\n");

            // Überprüfen, ob DataObjectReferences mit dieser Aufgabe verbunden sind
            for (DataObjectReference dataObjectReference : dataObjectReferences) {
                // Wenn DataObjectReference (z.B. Work Equipment) verbunden ist
                if ("Work Equipment".equals(dataObjectReference.getAttributeValue("name"))) {
                    sbvrOutput.append("  - Work Equipment verbunden mit Aufgabe: ").append(task.getName()).append("\n");
                }
            }

            // Überprüfen, ob DataInputs mit dieser Aufgabe verbunden sind
            for (DataInput dataInput : dataInputs) {
                // Wenn DataInput mit dieser Aufgabe verbunden ist
                if ("Work Equipment".equals(dataInput.getAttributeValue("name"))) {
                    sbvrOutput.append("  - DataInput (Work Equipment) verbunden mit Aufgabe: ").append(task.getName()).append("\n");
                }
            }
        }
    }
    //  private List<String> findConnectedDataObjects(Task task, BpmnModelInstance modelInstance) {
//        List<String> connectedDataObjects = new ArrayList<>();
//
//        // Verbindungen über DataInputAssociations
//        connectedDataObjects.addAll(task.getDataInputAssociations().stream()
//                .map(dataInputAssociation -> {
//                    // DataObjectReference finden, das mit dieser DataInputAssociation verbunden ist
//                    DataObjectReference dataObjectReference = (DataObjectReference) dataInputAssociation.getSources();
//                    return dataObjectReference != null ? dataObjectReference.getAttributeValue("name") : null;
//                })
//                .toList());
//
//        // Verbindungen über DataOutputAssociations
//        connectedDataObjects.addAll(task.getDataOutputAssociations().stream()
//                .map(dataOutputAssociation -> {
//                    // DataObjectReference finden, das mit dieser DataOutputAssociation verbunden ist
//                    DataObjectReference dataObjectReference = (DataObjectReference) dataOutputAssociation.getTarget();
//                    return dataObjectReference != null ? dataObjectReference.getAttributeValue("name") : null;
//                })
//                .toList());
//
//        return connectedDataObjects;
//    }
}