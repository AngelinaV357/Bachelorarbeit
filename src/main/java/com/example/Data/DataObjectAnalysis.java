package com.example.Data;

import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.util.Collection;

/**
 * Diese Klasse analysiert DataObjects in einem BPMN-Modell und generiert
 * eine textuelle Beschreibung im SBVR-Format basierend auf den gefundenen Verbindungen.
 */
public class DataObjectAnalysis {

    /**
     * Analysiert die DataObjects und DataAssociations in einem BPMN-Modell.
     * Die Methode untersucht alle Aktivitäten im Modell und prüft:
     * 1. Welche DataObjects als Eingaben (DataInputAssociation) verwendet werden.
     * 2. Welche DataObjects als Ausgaben (DataOutputAssociation) erzeugt werden.
     *
     * @param modelInstance Die BPMN-Modelldatei, die analysiert werden soll.
     * @param sbvrOutput    Der StringBuilder, in den die SBVR-Regeln geschrieben werden.
     */
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
                    sbvrOutput.append("Es ist notwendig, dass die Aktivität '")
                            .append(activityName)
                            .append("' das Datenobjekt '")
                            .append(dataObjectName)
                            .append("' erzeugt, welches als Ausgabe dient.\n");
                }

                // Wenn das Ziel ein DataInput ist (eher selten, aber möglich)
                if (targetElement instanceof DataInput) {
                    DataInput dataInput = (DataInput) targetElement;
                    String dataInputName = dataInput.getAttributeValue("name");
                    sbvrOutput.append("Es ist notwendig, dass das DataInput '")
                            .append(dataInputName)
                            .append("' als Ausgabe von der Aktivität '")
                            .append(activityName)
                            .append("' bereitgestellt wird.\n");
                }
            }
        }
        sbvrOutput.append("\n");
    }
}
