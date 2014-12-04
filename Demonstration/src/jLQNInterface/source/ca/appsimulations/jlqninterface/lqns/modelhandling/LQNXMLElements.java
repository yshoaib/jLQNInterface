package ca.appsimulations.jlqninterface.lqns.modelhandling;
/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public enum LQNXMLElements {
	RESULT_GENERAL("result-general"), PROCESSOR("processor"), RESULT_PROCESSOR("result-processor"), TASK("task"), RESULT_TASK("result-task"), ENTRY("entry"), RESULT_ENTRY("result-entry"), ENTRY_PHASE_ACTIVITIES(
			"entry-phase-activities"), ACTIVITY("activity"), PRECEDENCE("precedence"), PRE_OR("pre-OR"), POST("post"), POST_OR("post-OR"), RESULT_ACTIVITY("result-activity"), SYNCH_CALL(
			"synch-call"), TASK_ACTIVITIES("task-activities"), LQX("lqx");

	private String strValue;

	LQNXMLElements(String value) {
		this.strValue = value;
	}

	@Override
	public String toString() {
		return strValue;
	}

	public static LQNXMLElements getValue(String input) {
		for (LQNXMLElements eType : LQNXMLElements.values()) {
			if (eType.strValue.equalsIgnoreCase(input)) {
				return eType;
			}
		}
		return null;
	}

}
