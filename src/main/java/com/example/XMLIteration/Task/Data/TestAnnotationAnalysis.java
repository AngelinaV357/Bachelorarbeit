package com.example.XMLIteration.Task.Data;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.Collection;

public class TestAnnotationAnalysis {

    /**
     * Analysiert alle TextAnnotations in einem BPMN-Modell und deren Verbindungen.
     * Diese Methode bereinigt den Text der TextAnnotations, entfernt unnötige Teile wie '/glossary/...',
     * und überprüft die Verbindungen zu anderen BPMN-Elementen über Assoziationen.
     *
     * @param modelInstance Das BPMN-Modell, das analysiert werden soll.
     * @param sbvrOutput    Der StringBuilder, in den die Ergebnisse der Analyse geschrieben werden.
     */
    public static void analyzeTextAnnotations(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Alle TextAnnotations im Modell finden
        Collection<TextAnnotation> textAnnotations = modelInstance.getModelElementsByType(TextAnnotation.class);

        // Über alle TextAnnotations iterieren
        for (TextAnnotation annotation : textAnnotations) {

            // Analyse der Verbindungen der TextAnnotation
            analyzeTextAnnotationConnections(annotation, modelInstance, sbvrOutput);
        }
    }

    /**
     * Bereinigt den Text einer TextAnnotation, indem der Teil '/glossary/...' und
     * unnötige Abschnitte entfernt werden.
     *
     * @param text Der ursprüngliche Text der TextAnnotation.
     * @return Der bereinigte Text ohne '/glossary/...' oder andere unnötige Teile.
     */
    private static String cleanTextAnnotationContent(String text) {
        // Überprüfen, ob der Text das Muster '/glossary/...' enthält
        if (text != null && text.contains("/glossary/")) {
            // Entfernen des Teils vor und einschließlich '/glossary/...' (alles bis zum ersten Leerzeichen nach der ID)
            int glossaryIndex = text.indexOf("/glossary/");
            if (glossaryIndex != -1) {
                String namePart = text.substring(glossaryIndex);
                int endOfIdIndex = namePart.indexOf(' ');  // Annahme: Name nach Leerzeichen
                if (endOfIdIndex != -1) {
                    return namePart.substring(endOfIdIndex).trim(); // Nur den Text nach der ID zurückgeben
                }
            }
        }
        return text; // Falls kein '/glossary/' vorhanden ist, gib den Text unverändert zurück
    }

    /**
     * Analysiert die Verbindungen (Assoziationen) einer TextAnnotation im BPMN-Modell.
     * Die Methode überprüft, ob die TextAnnotation eine Quelle oder ein Ziel einer Assoziation ist,
     * und erzeugt entsprechende SBVR-Ausgaben.
     *
     * @param textAnnotation Die zu analysierende TextAnnotation.
     * @param modelInstance  Das BPMN-Modell, das die TextAnnotation enthält.
     * @param sbvrOutput     Der StringBuilder, in den die Ergebnisse der Analyse geschrieben werden.
     */
    private static void analyzeTextAnnotationConnections(TextAnnotation textAnnotation, BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        String textAnnotationContent = textAnnotation.getTextContent();
        String cleanedText = cleanTextAnnotationContent(textAnnotationContent);  // Bereinigen des Textes

        // Alle Assoziationen im Modell finden
        Collection<Association> associations = modelInstance.getModelElementsByType(Association.class);
        for (Association association : associations) {
            BaseElement sourceElement = association.getSource();
            BaseElement targetElement = association.getTarget();

            // Prüfen, ob die TextAnnotation Quelle oder Ziel ist
            if (sourceElement.equals(textAnnotation)) {
                String targetName = getElementName(targetElement);
                sbvrOutput.append("Es ist notwendig, dass die TextAnnotation '")
                        .append(cleanedText)
                        .append("' der Aktivität '")
                        .append(targetName)
                        .append("' zugeordnet ist.\n");
            } else if (targetElement.equals(textAnnotation)) {
                String sourceName = getElementName(sourceElement);
                sbvrOutput.append("Es ist notwendig, dass die Aktivität '")
                        .append(sourceName)
                        .append("' mit der TextAnnotation '")
                        .append(cleanedText)
                        .append("' zugeordnet ist.\n");
            }
        }
        sbvrOutput.append("\n");
    }

    /**
     * Gibt den Namen eines BPMN-Elementes zurück.
     * Diese Methode unterstützt FlowNodes, DataObjectReferences und TextAnnotations.
     * Für andere Typen wird der Typname des Elements zurückgegeben.
     *
     * @param element Das BPMN-Element, dessen Name abgerufen werden soll.
     * @return Der Name des Elements, oder der Typname, falls kein Name verfügbar ist.
     */
    private static String getElementName(BaseElement element) {
        if (element instanceof FlowNode) {
            return ((FlowNode) element).getName();
        } else if (element instanceof DataObjectReference) {
            return ((DataObjectReference) element).getAttributeValue("name");
        } else if (element instanceof TextAnnotation) {
            return ((TextAnnotation) element).getTextContent();
        } else {
            return element.getElementType().getTypeName();
        }
    }
}
