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
public enum LqnXmlAttributes {
    NAME("name"),
    DESCRIPTION("description"),
    XMLNS_XSI("xmlns:xsi"),
    SCHEMA_LOCATION("xsi:noNamespaceSchemaLocation"),
    COMMENT("comment"),
    CONVERGENCE_VALUE("conv_val"),
    ITERATION_LIMIT("it_limit"),
    UNDER_RELAX_COEFF("underrelax_coeff"),
    PRINT_INTERVAL("print_int"),
    TYPE("type"),
    SCHEDULING("scheduling"),
    UTILIZATION("utilization"),
    THROUGHPUT("throughput"),
    PROB("prob"),
    DEST("dest"),
    PHASE("phase"),
    MULTIPLICITY("multiplicity"),
    REPLICATION("replication"),
    FANIN("fanin"),
    FANOUT("fanout"),
    HOST_DEMAND_MEAN("host-demand-mean"),
    SERVICE_TIME("service-time"),
    SERVICE_TIME_VARIANCE("service-time-variance"),
    CALLS_MEAN("calls-mean"),
    CALL_ORDER("call-order"),
    PHASE1_UTILIZATION("phase1-utilization"),
    PHASE1_SERVICE_TIME("phase1-service-time"),
    PHASE2_SERVICE_TIME("phase2-service-time"),
    PHASE3_SERVICE_TIME("phase3-service-time"),
    PROC_UTILIZATION("proc-utilization"),
    PROC_WAITING("proc-waiting"),
    SQUARED_COEFF_VARIATION("squared-coeff-variation"),
    WAITING("waiting"),
    WAITING_VARIANCE("waiting-variance"),
    BOUND_TO_ENTRY("bound-to-entry"),
    VALID("valid");

    private String value;

    LqnXmlAttributes(String value) {
        this.value = value;
    }

    public static LqnXmlAttributes from(String input) {
        for (LqnXmlAttributes eType : LqnXmlAttributes.values()) {
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
