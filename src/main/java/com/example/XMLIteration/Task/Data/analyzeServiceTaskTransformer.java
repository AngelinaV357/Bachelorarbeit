package com.example.XMLIteration.Task.Data;

import com.example.XMLIteration.Task.main.SBVRTransformerNEU;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;

import java.util.Collection;

import static com.example.XMLIteration.Task.Hilfsmethoden.*;

public class analyzeServiceTaskTransformer {
    public static void analyzeServiceTasks(BpmnModelInstance modelInstance, StringBuilder sbvrOutput) {
        // Alle ServiceTasks abrufen
        Collection<ServiceTask> serviceTasks = modelInstance.getModelElementsByType(ServiceTask.class);

        // Holt alle Lane-Elemente, die für die Zuweisung von Rollen verwendet werden
        Collection<Lane> lanes = modelInstance.getModelElementsByType(Lane.class);

        // Iteriere durch alle ServiceTasks
        for (ServiceTask serviceTask : serviceTasks) {

            String taskName = sanitizeName(serviceTask.getName());

            String sourceRole = getRoleForNode(serviceTask, lanes);

            String serviceTaskStatement = SBVRTransformerNEU.createServiceTaskStatement(taskName, sourceRole);

            sbvrOutput.append(serviceTaskStatement);
        }
    }

}
