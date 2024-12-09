package com.example;

public class SBVRTransformerNEU {

    public static String createStartEventStatement(String startEventName) {
//        return "Es ist notwendig, dass " + targetRole + " " + targetName + " ausführt, wenn " + sourceRole + " das Startevent " + startEventName + " ausführt.";
        return "Es ist notwendig, dass der Prozess mit dem Startevent '" + startEventName + "' startet. ";
    }

//    public static String createStartEventStatement(String sourceRole, String targetRole, String targetName, String startEventName) {
//        return "Es ist notwendig, dass " + sourceRole + " das Ereignis '" + startEventName + "' ausführt, um " + targetRole + " für " + targetName + " zu starten.";
//    }

    public static String createFlowStatement(String sourceRole, String sourceName, String targetRole, String targetName, String condition) {
        return "Es ist notwendig, dass '" + targetRole + "' '" + targetName + "' ausführt, wenn '" + sourceRole + "' '" + sourceName + "' ausführt und '" + condition + "' gilt.\n";
    }

    public static String createBusinessRule(String sourceRole, String sourceName, String targetRole, String targetName, String condition) {
        return "Es ist notwendig, dass '" + targetRole + "' '" + targetName + "' ausführt, wenn '" + sourceRole + "' '" + sourceName + "' ausführt und alle Anforderungen geprüft wurden";
    }


    public static String createInclusiveStatement(String sourceRole, String sourceName, String targetRole, String targetName, String condition) {
        return "Es ist notwendig, dass '" + targetRole + "' '" + targetName + "' ausführt, wenn '" + sourceRole + "' '" + sourceName + "' ausführt und '" + condition + "' gilt.\n";
    }


    public static String createParallelStatement(String sourceName, String targetRole1, String targetName1, String targetRole2, String targetName2, String gatewayName) {
        // Gateway-Name wird nun korrekt in der SBVR-Aussage verwendet
        return "Es ist notwendig, dass '" + targetRole1 + "' '" + targetName1 + "' ausführt und '" + targetRole2 + "' '" + targetName2 + "' ausführt, wenn '" + gatewayName + "' ausführt.";
    }

    /**
     * Ein oder basiertes Gateway bedeutet, dass nur ein Ergebnis eintreffen muss, um den Prozess fortzusetzen
     */
    public static String createODEREventGatewayStatement(String activityName, String eventType) {
        // Formuliert die SBVR-Aussage basierend auf der Aktivität und dem Ereignis
        return "Es ist notwendig, dass die Aufgabe '" + activityName + "' startet, wenn das Datenereignis '" + eventType + "' eintritt.";
    }

    //Datenobjekte:
//    /**
//     * Erzeugt die SBVR-Regel für einen Data Input.
//     * @param dataName Name des Data Inputs
//     * @return SBVR-Regel für den Data Input
//     */
//    public static String createDataInputStatement(String dataName) {
//        return "Es ist notwendig, dass die Eingabedaten " + dataName + " einer Aktivität zur Verfügung gestellt werden.\n";
//    }

    public static String createDataInputStatementWithRoles(String dataName, String sourceId, String targetId, String sourceRole, String targetRole) {
        return String.format("DataInput '%s' wird von '%s' generiert und an '%s' übermittelt. Quelle: %s, Ziel: %s.",
                dataName, sourceRole, targetRole, sourceId, targetId);
    }

    /**
     * Erzeugt die SBVR-Regel für ein Data Object.
     * @param dataName Name des Data Objects
     * @return SBVR-Regel für das Data Object
     */
    public static String createDataObjectStatement(String dataName) {
        return "Es ist notwendig, dass das Datenobjekt '" + dataName + "' nach der Ausführung der Aktivität erstellt oder verändert wird.\n";
    }

    /**
     * Erzeugt die SBVR-Regel für einen User Task.
     * @param taskName Name der Aufgabe
     * @param role Die Rolle, die für diese Aufgabe verantwortlich ist
     * @return SBVR-Regel für den User Task
     */
    public static String createUserTaskStatement(String taskName, String role) {
        return "Es ist notwendig, dass die Aufgabe '" + taskName + "', einer Ressource mit der Rolle '" + role + "' zugewiesen wird.\n";
    }

    /**
     * Erzeugt die SBVR-Regel für einen Service Task.
     * @param taskName Name der Aufgabe
     * @return SBVR-Regel für den Service Task
     */
    public static String createServiceTaskStatement(String taskName, String sourceRole) {
        return "Es ist notwendig, dass die Aufgabe '" + taskName + "' automatisch durch das System '" + sourceRole + "' ausgeführt wird.\n";
    }

//    /**
//     * Erzeugt die SBVR-Regel für einen Send Task, wobei die sendende und empfangende Ressource berücksichtigt werden.
//     * @param taskName Name der Aufgabe
//     * @param senderRole Die Rolle, die die Nachricht sendet
//     * @param receiverRole Die Rolle, die die Nachricht empfängt
//     * @return SBVR-Regel für den Send Task
//     */
//    public static String createSendTaskStatement(String taskName, String senderRole, String receiverRole) {
//        return "Es ist notwendig, dass die Ressource mit der Rolle " + senderRole + " die Nachricht " + taskName + " an die Ressource mit der Rolle " + receiverRole + " sendet.\n";
//    }


    public static String createEndEventStatement(String sourceRole, String targetRole, String targetName, String endEventName) {
        // Wenn Ausgangsflüsse vorhanden sind, das ursprüngliche Statement zurückgeben
        if (targetName != null && !targetName.isEmpty()) {
            return "Es ist notwendig, dass '" + targetName + "' '" + targetRole + "' ausführt, wenn '" + sourceRole + "' das Endevent '" + endEventName + "' ausführt.";
        } else {
            // Wenn keine Ausgangsflüsse vorhanden sind, das Statement für das Ende des Prozesses zurückgeben
            return "Es ist notwendig, dass der Prozess mit dem Endevent '" + endEventName + "' endet.";
        }
    }
}