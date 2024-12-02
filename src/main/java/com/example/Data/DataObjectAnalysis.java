package com.example.Data;

import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.util.Collection;

public class DataObjectAnalysis {

    public void analyzeDataObjects(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // 1. Alle DataObject-Elemente finden
        Collection<DataObject> dataObjects = modelInstance.getModelElementsByType(DataObject.class);

        // Ausgabe der DataObjects
        for (DataObject dataObject : dataObjects) {
            String dataObjectName = dataObject.getAttributeValue("name");
            sbvrOutput.append("DataObject: ").append(dataObjectName).append("\n");
        }

        // 2. Alle DataInputs finden
        Collection<DataInput> dataInputs = modelInstance.getModelElementsByType(DataInput.class);
        for (DataInput dataInput : dataInputs) {
            String dataInputName = dataInput.getAttributeValue("name");

            // Nur DataInputs mit einem gültigen Namen ausgeben
            if (dataInputName != null && !dataInputName.trim().isEmpty()) {
                sbvrOutput.append("DataInput: ").append(dataInputName).append("\n");
            }
        }
    }
}



//    public void analyzeDataObjects(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
//        // 1. Alle DataObject-Elemente finden
//        Collection<DataObject> dataObjects = modelInstance.getModelElementsByType(DataObject.class);
//
//        // Ausgabe der DataObjects
//        for (DataObject dataObject : dataObjects) {
//            String dataObjectName = dataObject.getAttributeValue("name");
//            sbvrOutput.append("Gefundenes DataObject: ").append(dataObjectName).append("\n");
//
//            // 2. Überprüfen, mit welchen Tasks dieses DataObject verbunden ist
//            Collection<DataObjectReference> dataObjectReferences = modelInstance.getModelElementsByType(DataObjectReference.class);
//            for (DataObjectReference dataObjectReference : dataObjectReferences) {
//                String dataObjectRefId = dataObjectReference.getAttributeValue("dataObjectRef");
//
//                // Wenn die ID des DataObjectReference mit der ID des DataObjects übereinstimmt
//                if (dataObject.getId().equals(dataObjectRefId)) {
//                    // Alle Tasks finden, die mit diesem DataObject verbunden sind
//                    Collection<Task> tasks = modelInstance.getModelElementsByType(Task.class);
//                    for (Task task : tasks) {
//                        sbvrOutput.append("Es ist notwendig, dass die Aktivität ")
//                                .append(task.getName())
//                                .append(" auf das DataObject ")
//                                .append(dataObjectName)
//                                .append(" zugreift, wenn sie aufgerufen wird.\n");
//                    }
//                }
//            }
//        }
//
//        // 3. Alle DataInputs und deren Verbindungen überprüfen
//        Collection<DataInput> dataInputs = modelInstance.getModelElementsByType(DataInput.class);
//        for (DataInput dataInput : dataInputs) {
//            String dataInputName = dataInput.getAttributeValue("name");
//            if (dataInputName != null && !dataInputName.isEmpty()) {
//                sbvrOutput.append("Gefundenes DataInput: ").append(dataInputName).append("\n");
//
//                // Überprüfen, welche Aufgaben dieses DataInput verwenden
//                Collection<DataInputAssociation> dataInputAssociations = modelInstance.getModelElementsByType(DataInputAssociation.class);
//                for (DataInputAssociation dataInputAssociation : dataInputAssociations) {
//                    String targetRef = dataInputAssociation.getAttributeValue("targetRef");
//
//                    // Wenn die targetRef mit der ID des DataInput übereinstimmt
//                    if (dataInput.getId().equals(targetRef)) {
//                        // Alle Tasks finden, die mit diesem DataInput verbunden sind
//                        Collection<Task> tasks = modelInstance.getModelElementsByType(Task.class);
//                        for (Task task : tasks) {
//                            sbvrOutput.append("Es ist notwendig, dass die Aktivität ")
//                                    .append(task.getName())
//                                    .append(" auf das DataInput ")
//                                    .append(dataInputName)
//                                    .append(" zugreift, wenn sie aufgerufen wird.\n");
//                        }
//                    }
//                }
//            }
//        }
//
//        // 4. Alle Tasks finden und prüfen, ob sie mit DataObjects oder DataInputs verbunden sind
//        Collection<Task> tasks = modelInstance.getModelElementsByType(Task.class);
//        for (Task task : tasks) {
//            // Überprüfen, ob DataObjects mit dieser Aufgabe verbunden sind
//            boolean hasDataObject = false;
//            for (DataObject dataObject : dataObjects) {
//                String dataObjectName = dataObject.getAttributeValue("name");
//
//                // Falls das DataObject mit der Aufgabe verbunden ist, Ausgabe erzeugen
//                Collection<DataObjectReference> dataObjectReferences = modelInstance.getModelElementsByType(DataObjectReference.class);
//                for (DataObjectReference dataObjectReference : dataObjectReferences) {
//                    if (dataObject.getId().equals(dataObjectReference.getAttributeValue("dataObjectRef"))) {
//                        sbvrOutput.append("Es ist notwendig, dass die Aktivität ")
//                                .append(task.getName())
//                                .append(" auf das DataObject ")
//                                .append(dataObjectName)
//                                .append(" zugreift, wenn sie aufgerufen wird.\n");
//                        hasDataObject = true; // Nur einmal das DataObject ausgeben
//                    }
//                }
//            }
//
//            // Überprüfen, ob DataInputs mit dieser Aufgabe verbunden sind
//            for (DataInput dataInput : dataInputs) {
//                String dataInputName = dataInput.getAttributeValue("name");
//                if (dataInputName != null && !dataInputName.isEmpty()) {
//                    sbvrOutput.append("Es ist notwendig, dass die Aktivität ")
//                            .append(task.getName())
//                            .append(" auf das DataInput ")
//                            .append(dataInputName)
//                            .append(" zugreift, wenn sie aufgerufen wird.\n");
//                }
//            }
//        }
//    }
//}