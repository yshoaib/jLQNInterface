package ca.appsimulations.jlqninterface.lqn.entities;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */

public abstract class EntryMakingCallType extends MakingCallType {
    protected double prob;

    public EntryMakingCallType(LqnModel lqnModel, String strDestEntry, double prob) {
        super(lqnModel, strDestEntry);
        this.prob = prob;
    }

    public EntryMakingCallType(LqnModel lqnModel, Entry destEntry, double prob) {
        super(lqnModel, destEntry);
        this.prob = prob;
    }

    public double getProb() {
        return prob;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }
}
