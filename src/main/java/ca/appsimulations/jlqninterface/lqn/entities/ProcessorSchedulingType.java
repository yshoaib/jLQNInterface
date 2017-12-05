package ca.appsimulations.jlqninterface.lqn.entities;

/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 * Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */

public enum ProcessorSchedulingType {
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
    PS("ps"),
    // Processor sharing. The processor runs all tasks
    // “simultaneously”. The rate of service by the processor is
    // inversely proportional to the number of executing tasks. For
    // lqsim, processor sharing is implemented as round-robin – a
    // quantum must be specified
    RAND("rand"),
    // Random scheduling. The processor selects a task at random.
    CFS("cfs"),
    // Completely fair scheduling [9]. Tasks are scheduled within
    // groups using round-robin scheduling and groups are scheduled
    // according to their share. A quantum must be specified. This
    // scheduling discipline is implemented on the simulator only at
    // present.
    INF("inf");

    private String strValue;

    ProcessorSchedulingType(String value) {
        this.strValue = value;
    }

    public static ProcessorSchedulingType getValue(String input) {
        for (ProcessorSchedulingType pst : ProcessorSchedulingType.values()) {
            if (pst.strValue.equalsIgnoreCase(input)) {
                return pst;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return strValue;
    }
}