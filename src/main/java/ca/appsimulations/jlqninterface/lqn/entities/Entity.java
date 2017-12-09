package ca.appsimulations.jlqninterface.lqn.entities;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */

public abstract class Entity {
    protected int id = 0;
    protected LqnModel lqnModel;
    protected Result result;
    protected double bStrength = LqnConstants.UNDEF.getConstantValue();
    protected double sat = 0;

    public abstract Result getResult();

    public abstract String getInformation();

    public double getBStrength() {
        return bStrength;
    }

    public void setBStrength(double bStren) {
        bStrength = bStren;
    }

    public double getSat() {
        return sat;
    }

    public void setSat(double sat) {
        this.sat = sat;
    }

}
