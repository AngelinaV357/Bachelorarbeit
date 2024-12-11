//package com.example.Data;
//
//import org.camunda.bpm.model.bpmn.BpmnModelInstance;
//import org.camunda.bpm.model.bpmn.instance.*;
//
//import java.util.Collection;
//import java.util.Iterator;
//
//import static com.example.Hilfsmethoden.getMessageFlowParticipantName;
//
//public class analyzeMessageFlowsForActivities {
//
//    public static void analyzeActivities(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
//        // Alle Activities abrufen
//        Collection<FlowNode> flowNodes = modelInstance.getModelElementsByType(FlowNode.class);
//
//        // Alle MessageFlows und Teilnehmer abrufen
//        Collection<MessageFlow> messageFlows = modelInstance.getModelElementsByType(MessageFlow.class);
//        Collection<Participant> participants = modelInstance.getModelElementsByType(Participant.class);
//
//        // Durch alle FlowNodes iterieren
//        for (FlowNode node : flowNodes) {
//            if (node instanceof Activity) {
//                Activity activity = (Activity) node;
//
//                // Namen der Aktivität abrufen
//                String activityName = sanitizeName(activity.getName());
//
//                // Verknüpfte MessageFlows analysieren
//                for (MessageFlow messageFlow : messageFlows) {
//                    BaseElement source = (BaseElement) messageFlow.getSource();
//                    BaseElement target = (BaseElement) messageFlow.getTarget();
//
//                    // Wenn das Ziel des MessageFlows die Aktivität ist
//                    if (target.equals(activity)) {
//                        String sourceName = getMessageFlowParticipantName(source, participants);
//                        sbvrOutput.append("Es ist notwendig, dass die Aktivität '")
//                                .append(activityName)
//                                .append("' eine Nachricht von '")
//                                .append(sourceName)
//                                .append("' empfängt, bevor fortgeführt wird.\n");
//                    }
//                    // Wenn die Quelle des MessageFlows die Aktivität ist
//                    else if (source.equals(activity)) {
//                        String targetName = getMessageFlowParticipantName(target, participants);
//                        sbvrOutput.append("Es ist notwendig, dass die Aktivität '")
//                                .append(activityName)
//                                .append("' eine Nachricht an '")
//                                .append(targetName)
//                                .append("' sendet, bevor fortgeführt wird.\n");
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Bereinigt den Namen, entfernt unerwünschte Zeilenumbrüche oder Sonderzeichen.
//     */
//    private static String sanitizeName(String name) {
//        if (name == null) {
//            return "Unbenannt";
//        }
//        // Entfernt Zeilenumbrüche und überflüssige Leerzeichen
//        return name.replaceAll("[\\r\\n]+", " ").trim();
//    }
//}
