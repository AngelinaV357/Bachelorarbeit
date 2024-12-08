package com.example.Data;

import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.util.Collection;

public class DataObjectAnalysis {

    // Methode zur Analyse der DataObjects im Modell
    public void analyzeDataObjects(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // 1. Alle Aktivitäten im Modell finden
        Collection<Activity> activities = modelInstance.getModelElementsByType(Activity.class);

        // 2. Über alle Aktivitäten iterieren
        for (Activity activity : activities) {
            String activityName = activity.getName();

            // ** Verarbeitung von DataInputs **
            // Eingangs-Dateninputs (z. B. explizit als DataInput angegeben)
            for (DataInputAssociation inputAssociation : activity.getDataInputAssociations()) {
                // Quelle der Verbindung (meistens ein DataObjectReference oder DataInput)
                BaseElement sourceElement = inputAssociation.getSources().iterator().next();

                // Wenn die Quelle ein DataObjectReference ist
                if (sourceElement instanceof DataObjectReference) {
                    DataObjectReference dataObjectRef = (DataObjectReference) sourceElement;
                    String dataObjectName = dataObjectRef.getAttributeValue("name");
                    sbvrOutput.append("Es ist notwendig, dass die Aktivität '")
                            .append(activityName)
                            .append("' das Datenobjekt '")
                            .append(dataObjectName)
                            .append("' als Eingabe verwendet.\n");
                }

                // Wenn die Quelle ein DataInput ist
                if (sourceElement instanceof DataInput) {
                    DataInput dataInput = (DataInput) sourceElement;
                    String dataInputName = dataInput.getAttributeValue("name");
                    sbvrOutput.append("Es ist notwendig, dass das DataInput '")
                            .append(dataInputName)
                            .append("' der Aktivität '")
                            .append(activityName)
                            .append("' zugeordnet ist.\n");
                }
            }

            // ** Verarbeitung von DataObjects als Outputs **
            // Ausgangs-Datenverbindungen analysieren
            for (DataOutputAssociation outputAssociation : activity.getDataOutputAssociations()) {
                BaseElement targetElement = outputAssociation.getTarget();

                // Wenn das Ziel ein DataObjectReference ist
                if (targetElement instanceof DataObjectReference) {
                    DataObjectReference dataObjectRef = (DataObjectReference) targetElement;
                    String dataObjectName = dataObjectRef.getAttributeValue("name");
                    sbvrOutput.append("Die Aktivität '")
                            .append(activityName)
                            .append("' erzeugt das Datenobjekt '")
                            .append(dataObjectName)
                            .append("', welches als Ausgabe dient.\n");
                }

                // Wenn das Ziel ein DataInput ist (eher selten, aber möglich)
                if (targetElement instanceof DataInput) {
                    DataInput dataInput = (DataInput) targetElement;
                    String dataInputName = dataInput.getAttributeValue("name");
                    sbvrOutput.append("Das DataInput '")
                            .append(dataInputName)
                            .append("' wird als Ausgabe von der Aktivität '")
                            .append(activityName)
                            .append("' bereitgestellt.\n");
                }
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
//        // 4. Alle DataOutputs und deren Verbindungen überprüfen
//        Collection<DataOutput> dataOutputs = modelInstance.getModelElementsByType(DataOutput.class);
//        for (DataOutput dataOutput : dataOutputs) {
//            String dataOutputName = dataOutput.getAttributeValue("name");
//            if (dataOutputName != null && !dataOutputName.isEmpty()) {
//                sbvrOutput.append("Gefundenes DataOutput: ").append(dataOutputName).append("\n");
//
//                // Überprüfen, welche Aufgaben dieses DataOutput verwenden
//                Collection<DataOutputAssociation> dataOutputAssociations = modelInstance.getModelElementsByType(DataOutputAssociation.class);
//                for (DataOutputAssociation dataOutputAssociation : dataOutputAssociations) {
//                    String sourceRef = dataOutputAssociation.getAttributeValue("sourceRef");
//
//                    // Wenn die sourceRef mit der ID des DataOutput übereinstimmt
//                    if (dataOutput.getId().equals(sourceRef)) {
//                        // Alle Tasks finden, die mit diesem DataOutput verbunden sind
//                        Collection<Task> tasks = modelInstance.getModelElementsByType(Task.class);
//                        for (Task task : tasks) {
//                            sbvrOutput.append("Es ist notwendig, dass die Aktivität ")
//                                    .append(task.getName())
//                                    .append(" das DataOutput ")
//                                    .append(dataOutputName)
//                                    .append(" produziert, wenn sie aufgerufen wird.\n");
//                        }
//                    }
//                }
//            }
//        }
//
//        // 5. Alle Tasks finden und prüfen, ob sie mit DataObjects, DataInputs oder DataOutputs verbunden sind
//        Collection<Task> tasks = modelInstance.getModelElementsByType(Task.class);
//        for (Task task : tasks) {
//            // Überprüfen, ob DataObjects mit dieser Aufgabe verbunden sind
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
//                            .append(" das DataInput ")
//                            .append(dataInputName)
//                            .append(" als Eingabe benötigt, wenn sie aufgerufen wird.\n");
//                }
//            }
//
//            // Überprüfen, ob DataOutputs mit dieser Aufgabe verbunden sind
//            for (DataOutput dataOutput : dataOutputs) {
//                String dataOutputName = dataOutput.getAttributeValue("name");
//                if (dataOutputName != null && !dataOutputName.isEmpty()) {
//                    sbvrOutput.append("Es ist notwendig, dass die Aktivität ")
//                            .append(task.getName())
//                            .append(" das DataOutput ")
//                            .append(dataOutputName)
//                            .append(" produziert, wenn sie aufgerufen wird.\n");
//                }
//            }
//        }
//    }
