//package com.example.WorkflowPatterns;
//
//import java.io.File;
//import java.util.*;
//
//public class WorkflowPatternProcessor {
//
//    public static void main(String[] args) throws Exception {
//        File bpmnFile = new File("src/main/resources/May_combine_ingredients.bpmn");
//
//        // 1. BPMN-Datei einlesen und Graph erstellen
//        BpmnModelParser parser = new BpmnModelParser();
//        Map<String, List<String>> graph = parser.parseBpmnFileToGraph(bpmnFile);
//
//        // 2. Set für bereits verarbeitete Knoten (abgehackte Knoten)
//        Set<String> processedNodes = new HashSet<>();
//
//        // 4. XOR-Gateway-Patterns erkennen und bereits verarbeitete Knoten überspringen
//        XorGatewayPatternRecognizer xorRecognizer = new XorGatewayPatternRecognizer();
//        List<List<String>> xorPatterns = xorRecognizer.detectXorGatewayPatterns(graph, nodeTypes, processedNodes);
//
//        // 5. Ausgabe der Anzahl der XOR-Gateways nur einmal
//        if (!xorPatterns.isEmpty()) {
//            System.out.println("Anzahl der erkannten XOR-Gateways: " + xorPatterns.size());
//            for (List<String> pattern : xorPatterns) {
//                // Ausgabe der Pattern mit Namen der Aktivitäten
//                List<String> patternNames = new ArrayList<>();
//                for (String nodeId : pattern) {
//                    String nodeName = nodeNames.get(nodeId);  // Hole den Namen der Aktivität
//                    patternNames.add(nodeName != null ? nodeName : "Unbekannte Aktivität");
//                }
//                System.out.println("XOR Pattern: " + patternNames);
//
//                // Generiere und gebe die SBVR-Regel aus
//                String sbvrRule = xorRecognizer.generateSbvrRuleForXor(pattern, nodeNames, conditions);
//                System.out.println(sbvrRule); // Ausgabe der SBVR-Regel
//            }
//        }
//
//        // 6. Parallel-Gateway-Patterns erkennen und bereits verarbeitete Knoten überspringen
//        ParallelGatewayPatternRecognizer parallelRecognizer = new ParallelGatewayPatternRecognizer();
//        List<List<String>> parallelPatterns = parallelRecognizer.detectParallelGatewayPatterns(graph, nodeTypes, processedNodes);
//
//        // Ausgabe der Anzahl der Parallel-Gateways und Muster, nur wenn welche erkannt wurden
//        if (!parallelPatterns.isEmpty()) {
//            System.out.println("Anzahl der erkannten Parallel-Gateways: " + parallelPatterns.size());
//            for (List<String> pattern : parallelPatterns) {
//                // Ausgabe der Pattern mit Namen der Aktivitäten
//                List<String> patternNames = new ArrayList<>();
//                for (String nodeId : pattern) {
//                    String nodeName = nodeNames.get(nodeId);  // Hole den Namen der Aktivität
//                    patternNames.add(nodeName != null ? nodeName : "Unbekannte Aktivität");
//                }
//                System.out.println("Parallel Gateway Pattern: " + patternNames);
//
//                // Generiere und gebe die SBVR-Regel aus
//                parallelRecognizer.generateSBVRRuleForParallelGateway(parallelPatterns, nodeNames, nodeTypes, conditions);
//            }
//        }
//
//        // 9. Ausgabe der abgehackten Aktivitäten mit Rollen (falls vorhanden)
//        System.out.println("\nAbgehackte Aktivitäten (verarbeitete Knoten):");
//        for (String node : processedNodes) {
//            String activityName = nodeNames.get(node);  // Verwende nodeNames, um den Namen der Aktivität zu holen
//            if (activityName == null) {
//                activityName = "Unbekannte Aktivität"; // Fallback, falls der Name nicht gefunden wird
//            }
//            System.out.println(activityName); // Nur Aktivität ohne Rolle
//        }
//
//        // 10. Ausgabe der restlichen Aktivitäten über Sequence Flows mit Rollen (falls vorhanden)
//        System.out.println("\nRestliche Aktivitäten (durch Sequence Flows):");
//        StringBuilder sbvrOutput = new StringBuilder();
//        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
//            String sourceActivity = entry.getKey();
//            List<String> targetActivities = entry.getValue();
//
//            // Für jede Zielaktivität (Sequence Flow) den SBVR-Ausdruck generieren
//            for (String targetActivity : targetActivities) {
//                if (!processedNodes.contains(sourceActivity) && !processedNodes.contains(targetActivity)) {
//                    String sourceActivityName = nodeNames.get(sourceActivity);  // Verwende nodeNames für die Aktivitätsnamen
//                    if (sourceActivityName == null) {
//                        sourceActivityName = "Unbekannte Aktivität"; // Fallback
//                    }
//                    sbvrOutput.append(sourceActivityName).append(" -> ");
//                }
//            }
//        }
//        // Ausgabe des SBVR-Ausdrucks für die restlichen Aktivitäten
//        System.out.println(sbvrOutput);
//    }
//}
