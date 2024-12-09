package com.example.Data;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.Collection;

public class TestAnnotationAnalysis {

    // Methode zur Analyse von TextAnnotations und deren Verbindungen
    public static void analyzeTextAnnotations(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Alle TextAnnotations im Modell finden
        Collection<TextAnnotation> textAnnotations = modelInstance.getModelElementsByType(TextAnnotation.class);

        // Über alle TextAnnotations iterieren
        for (TextAnnotation annotation : textAnnotations) {
            String text = annotation.getTextContent();

            // Entfernen des Teils '/glossary/...'
            String cleanedText = cleanTextAnnotationContent(text);

            // Erweiterungselemente analysieren
            ExtensionElements extensionElements = annotation.getExtensionElements();
//            if (extensionElements != null) {
//                for (ModelElementInstance element : extensionElements.getElements()) {
//                    sbvrOutput.append("TextAnnotation: '")
//                            .append(cleanedText)
//                            .append("\n");
//                }
//            } else {
//                sbvrOutput.append("TextAnnotation: '").append(cleanedText).append("', keine Erweiterungselemente gefunden.\n");
//            }

            // Analyse der Verbindungen der TextAnnotation
            analyzeTextAnnotationConnections(annotation, modelInstance, sbvrOutput);
        }
    }

    // Methode zur Bereinigung des Textes (Entfernen des '/glossary/...' Teils und des ersten Abschnitts)
    private static String cleanTextAnnotationContent(String text) {
        // Überprüfen, ob der Text das Muster '/glossary/... ' enthält
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


    // Methode zur Analyse der Verbindungen von TextAnnotations
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
                sbvrOutput.append("Es ist notwendig, dass dass Element '")
                        .append(sourceName)
                        .append("' mit der TextAnnotation '")
                        .append(cleanedText)
                        .append("' zugeordnet ist.\n");
            }
        }
        sbvrOutput.append("\n");
    }

    // Hilfsmethode, um den Namen eines Elements zu bekommen
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
