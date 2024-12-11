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

    /**
     * Erstellt die Aussage für einen SendTask
     */
    public static String createSendTaskStatement(String taskName, String senderRole, String receiverRole) {
        return "Es ist notwendig, dass die Ressource mit der Rolle '" + senderRole + "' die Nachricht '" + taskName + "' an die Ressource mit der Rolle '" + receiverRole + "' sendet, bevor fortgeführt wird.\n";
    }

    /**
     * Generiert eine SBVR-Aussage für das AND-Gateway, das mehrere Bedingungen berücksichtigt.
     */
    public static String createANDGatewayStatement(String targetRole1, String targetName1, String targetRole2, String targetName2, String gatewayName, String condition1, String condition2) {
        // Generiere eine SBVR-Aussage, die beschreibt, dass beide Aktivitäten unter einer AND-Bedingung ausgeführt werden
        return "Es ist notwendig, dass '" + targetRole1 + "' '" + targetName1 + "' ausführt und '" + targetRole2 + "' '" + targetName2 + "' ausführt, wenn '" + gatewayName + "' mit der Bedingung '" + condition1 + "' und '" + condition2 + "' erfüllt ist.";
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