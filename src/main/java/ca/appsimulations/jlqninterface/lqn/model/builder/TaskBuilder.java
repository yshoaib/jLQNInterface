package ca.appsimulations.jlqninterface.lqn.model.builder;

import ca.appsimulations.jlqninterface.lqn.entities.*;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

public class TaskBuilder {

    public static Task build(String name,
                             LqnModel lqnModel,
                             Processor processor,
                             boolean refTask,
                             int threads) {
        Task task = new Task(lqnModel,
                             name,
                             processor);
        if (refTask) {
            task.setScheduling(TaskSchedulingType.REF);
            task.setMultiplicity(LqnConstants.INFINITY.getConstantValue());
        }
        else {
            task.setScheduling(TaskSchedulingType.FIFO);
            task.setMultiplicity(threads);
        }
        task.setReplication(LqnDefaults.TASK_REPLICATION.getValue());
        return task;
    }
}
