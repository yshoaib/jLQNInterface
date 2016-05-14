package ca.appsimulations.jlqninterface.lqns.entities;
import ca.appsimulations.jlqninterface.core.Model;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public abstract class ProcessorType extends Entity {

	protected String name;
	protected float speedfactor = 1.0f;
	protected ProcessorSchedulingType scheduling = ProcessorSchedulingType.FIFO;
	protected int multiplicity = 1;
	protected int replication = 1;
	protected float quantum = 0.0f;

	public ProcessorType(Model workspace, String name) {
		this.workspace = workspace;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getSpeedfactor() {
		return speedfactor;
	}

	public void setSpeedfactor(float speedfactor) {
		this.speedfactor = speedfactor;
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
		if (multiplicity == LQNConstants.INFINITY.getConstantValue()) {
			return LQNConstants.INFINITY.toString();
		} else {
			return Integer.toString(multiplicity);
		}
	}

	public int getReplication() {
		return replication;
	}

	public void setReplication(int replication) {
		this.replication = replication;
	}

	public float getQuantum() {
		return quantum;
	}

	public void setQuantum(float quantum) {
		this.quantum = quantum;
	}
}
