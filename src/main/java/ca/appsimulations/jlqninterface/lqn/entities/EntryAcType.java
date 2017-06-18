package ca.appsimulations.jlqninterface.lqn.entities;
/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public enum EntryAcType {
	PH1PH2("PH1PH2"), GRAPH("GRAPH"), NONE("NONE");

	private String strValue;

	EntryAcType(String value) {
		this.strValue = value;
	}

	@Override
	public String toString() {
		return strValue;
	}

	public static EntryAcType getValue(String input) {
		for (EntryAcType eat : EntryAcType.values()) {
			if (eat.strValue.equalsIgnoreCase(input)) {
				return eat;
			}
		}
		return null;
	}
}
