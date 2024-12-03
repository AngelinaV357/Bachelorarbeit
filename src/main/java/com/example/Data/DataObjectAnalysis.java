package com.example.Data;

import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.util.Collection;

public class DataObjectAnalysis {

    public void analyzeDataObjects(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Data Associations analysieren
        Collection<DataAssociation> dataAssociations = modelInstance.getModelElementsByType(DataAssociation.class);

        for (DataAssociation dataAssociation : dataAssociations) {
            // Quell- und Ziel-Elemente abrufen
            Collection<ItemAwareElement> sources = dataAssociation.getSources();
            BaseElement target = dataAssociation.getTarget();

            // Quellen und Ziel analysieren
            for (BaseElement source : sources) {
                String sourceName = resolveNameFromElement(source);
                String targetName = resolveNameFromElement(target);

                // Quelle: Datenobjekt, Ziel: Aktivität
                if (isDataObject(source) && isActivity(target)) {
                    sbvrOutput.append("DATENOBJEKT ").append(sourceName)
                            .append(" ist mit AKTIVITÄT ").append(targetName)
                            .append(" verbunden.\n");
                }
                // Quelle: Aktivität, Ziel: Datenobjekt
                else if (isActivity(source) && isDataObject(target)) {
                    sbvrOutput.append("AKTIVITÄT ").append(sourceName)
                            .append(" ist mit DATENOBJEKT ").append(targetName)
                            .append(" verbunden.\n");
                }
                // Zusätzliche Fälle (optional)
                else {
                    sbvrOutput.append("Verbindung: ").append(sourceName)
                            .append(" -> ").append(targetName)
                            .append("\n");
                }
            }
        }
    }

    /**
     * Methode, um den Namen eines Elements abzurufen.
     * Falls kein Name vorhanden ist, wird die ID zurückgegeben.
     */
    private String resolveNameFromElement(BaseElement element) {
        if (element == null) {
            return "unbekannt";
        }

        // Name des Elements abrufen
        String name = element.getAttributeValue("name");
        return (name != null && !name.trim().isEmpty()) ? name : "ID: " + element.getId();
    }

    /**
     * Prüfen, ob ein Element ein Datenobjekt ist.
     */
    private boolean isDataObject(BaseElement element) {
        return element instanceof DataObject || element instanceof DataInput || element instanceof DataOutput;
    }

    /**
     * Prüfen, ob ein Element eine Aktivität ist.
     */
    private boolean isActivity(BaseElement element) {
        return element instanceof Task || element instanceof SubProcess || element instanceof CallActivity;
    }
}
