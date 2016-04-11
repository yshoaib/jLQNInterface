package ca.appsimulations.jlqninterface.lqns.entities;
/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 *   Yasir Shoaib - Implementation
 *   
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */
public enum LQNDefaults {
	PROCESSOR_MULTIPLICITY(1), TASK_MULTIPLICITY(1), PROCESSOR_REPLICATION(1), TASK_REPLICATION(1);

	int value;

	LQNDefaults(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
