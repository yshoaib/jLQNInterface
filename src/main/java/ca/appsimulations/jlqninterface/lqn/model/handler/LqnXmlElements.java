package ca.appsimulations.jlqninterface.lqn.model.handler;

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
public enum LqnXmlElements {
    LQN_MODEL("lqn-model"),
    SOLVER_PARAMS("solver-params"),
    RESULT_GENERAL("result-general"),
    PROCESSOR("processor"),
    RESULT_PROCESSOR("result-processor"),
    TASK("task"),
    RESULT_TASK("result-task"),
    ENTRY("entry"),
    RESULT_ENTRY("result-entry"),
    ENTRY_PHASE_ACTIVITIES("entry-phase-activities"),
    FAN_IN("fan-in"),
    FAN_OUT("fan-out"),
    ACTIVITY("activity"),
    PRECEDENCE("precedence"),
    PRE_OR("pre-OR"),
    POST("post"),
    POST_OR("post-OR"),
    RESULT_ACTIVITY("result-activity"),
    SYNCH_CALL("synch-call"),
    TASK_ACTIVITIES("task-activities"),
    LQX("lqx");

    private String value;

    LqnXmlElements(String value) {
        this.value = value;
    }

    public static LqnXmlElements from(String input) {
        for (LqnXmlElements eType : LqnXmlElements.values()) {
            if (eType.value.equalsIgnoreCase(input)) {
                return eType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }

}
