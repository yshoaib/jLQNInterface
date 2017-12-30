package ca.appsimulations.jlqninterface.lqn.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Builder
@Data
@Accessors(fluent = true, chain = true)
public class SolverParams {
    private String comment;
    private double convergence;
    private int iterationLimit;
    private double underRelaxCoeff;
    private int printInterval;
}
