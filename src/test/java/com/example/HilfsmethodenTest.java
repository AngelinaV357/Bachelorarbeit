package com.example;

import com.example.XMLIteration.Task.Hilfsmethoden;
import com.example.XMLIteration.Task.main.BPMNProcessor;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collection;

class HilfsmethodenTest {

    @Test
    void getMissingCondition() {
        File file = new File("src/main/resources/Car Wash Process.bpmn");
        BpmnModelInstance modelInstance = BPMNProcessor.readBpmnFile(file);
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
        for (SequenceFlow sequenceFlow : sequenceFlows) {
            var testResult = Hilfsmethoden.getCondition(sequenceFlow);
            System.out.println(testResult);
            Assertions.assertEquals("keine Bedingung", testResult);
        }
    }

}