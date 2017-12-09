package ca.appsimulations.jlqninterface.lqn.entities;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */

public abstract class ActivityMakingCallType extends MakingCallType {
    protected double callsMean;

    public ActivityMakingCallType(LqnModel lqnModel, Entry dstEntry, double callsMean) {
        super(lqnModel, dstEntry);
        this.callsMean = callsMean;
    }

    public ActivityMakingCallType(LqnModel lqnModel, String strDstEntry, double callsMean) {
        super(lqnModel, strDstEntry);
        this.callsMean = callsMean;
    }

    public double getCallsMean() {
        return callsMean;
    }

    public void setCallsMean(double callsMean) {
        this.callsMean = callsMean;
    }
}
