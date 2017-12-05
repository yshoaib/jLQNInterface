package ca.appsimulations.jlqninterface.lqn.entities;

import ca.appsimulations.jlqninterface.lqn.model.LqnModel;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * <p>
 * Some LQN classes and their members are outlined as UML class diagrams
 * in LQNS User Manual. For details regarding these LQN classes and
 * members refer to LQNS User Manual.
 */

public abstract class ProcessorType extends Entity {

    protected String name;
    protected double speedFactor = 1.0f;
    protected ProcessorSchedulingType scheduling = ProcessorSchedulingType.FIFO;
    protected int multiplicity = 1;
    protected int replication = 1;
    protected double quantum = 0.0f;

    public ProcessorType(LqnModel lqnModel, String name) {
        this.lqnModel = lqnModel;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSpeedFactor() {
        return speedFactor;
    }

    public void setSpeedFactor(double speedFactor) {
        this.speedFactor = speedFactor;
    }

    public ProcessorSchedulingType getScheduling() {
        return scheduling;
    }

    public void setScheduling(ProcessorSchedulingType scheduling) {
        this.scheduling = scheduling;
    }

    public int getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(int multiplicity) {
        this.multiplicity = multiplicity;
    }

    public String getMutiplicityString() {
        if (multiplicity == LqnConstants.INFINITY.getConstantValue()) {
            return LqnConstants.INFINITY.toString();
        }
        else {
            return Integer.toString(multiplicity);
        }
    }

    public int getReplication() {
        return replication;
    }

    public void setReplication(int replication) {
        this.replication = replication;
    }

    public double getQuantum() {
        return quantum;
    }

    public void setQuantum(double quantum) {
        this.quantum = quantum;
    }
}
