package ca.appsimulations.jlqninterface.lqn.entities;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */

@Getter
@Accessors(fluent = true, chain = true)
public enum TaskSchedulingType {
    FIFO("fcfs"),
    // First-in, first out (first-come, first-served). Tasks are
    // served in the order in which they arrive.
    PPR("ppr"),
    // Priority, preemptive resume. Tasks with priorities higher
    // than the task currently running on the processor will preempt
    // the running task.
    HOL("hol"),
    // Head-of-line priority. Tasks with higher priorities will be
    // served by the processor first. Tasks in the queue will not
    // preempt a task running on the processor even though the
    // running task may have a lower priority.
    REF("ref"),
    INF("inf");

    private String value;

    TaskSchedulingType(String value) {
        this.value = value;
    }

    public static TaskSchedulingType getValue(String input) {
        for (TaskSchedulingType tsk : TaskSchedulingType.values()) {
            if (tsk.value.equalsIgnoreCase(input)) {
                return tsk;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}