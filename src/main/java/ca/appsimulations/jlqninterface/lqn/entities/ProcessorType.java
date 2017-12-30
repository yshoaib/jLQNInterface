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

    public ProcessorType setName(String name) {
        this.name = name;
        return this;
    }

    public double getSpeedFactor() {
        return speedFactor;
    }

    public ProcessorType setSpeedFactor(double speedFactor) {
        this.speedFactor = speedFactor;
        return this;
    }

    public ProcessorSchedulingType getScheduling() {
        return scheduling;
    }

    public ProcessorType setScheduling(ProcessorSchedulingType scheduling) {
        this.scheduling = scheduling;
        return this;
    }

    public int getMultiplicity() {
        return multiplicity;
    }

    public ProcessorType setMultiplicity(int multiplicity) {
        this.multiplicity = multiplicity;
        return this;
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

    public ProcessorType setReplication(int replication) {
        this.replication = replication;
        return this;
    }

    public double getQuantum() {
        return quantum;
    }

    public ProcessorType setQuantum(double quantum) {
        this.quantum = quantum;
        return this;
    }
}
