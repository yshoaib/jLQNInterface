package ca.appsimulations.jlqninterface.lqn.model.factory;

import ca.appsimulations.jlqninterface.lqn.entities.LqnDefaults;
import ca.appsimulations.jlqninterface.lqn.entities.Processor;
import ca.appsimulations.jlqninterface.lqn.entities.Task;
import ca.appsimulations.jlqninterface.lqn.entities.TaskSchedulingType;
import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

public class TaskFactory {

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
        }
        else {
            task.setScheduling(TaskSchedulingType.FIFO);
        }
        task.setMultiplicity(threads);
        task.setReplication(LqnDefaults.TASK_REPLICATION.getValue());
        return task;
    }

    public static Task build(String name,
                             LqnModel lqnModel,
                             Processor processor,
                             boolean refTask,
                             int threads,
                             int replicationCount) {
        Task task = new Task(lqnModel,
                             name,
                             processor);
        if (refTask) {
            task.setScheduling(TaskSchedulingType.REF);
        }
        else {
            task.setScheduling(TaskSchedulingType.FIFO);
        }
        task.setMultiplicity(threads);
        task.setReplication(replicationCount);
        return task;
    }
}
