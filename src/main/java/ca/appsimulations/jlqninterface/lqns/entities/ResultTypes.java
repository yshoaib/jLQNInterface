/**
 * 
 */
package ca.appsimulations.jlqninterface.lqns.entities;

/**
 * @author Yasir Shoaib (2011,2012)
 * Contributors:
 *   Yasir Shoaib - Implementation
 *   
 * Some LQN classes and their members are outlined as UML class diagrams in LQNS User Manual.
 * For details regarding these LQN classes and members refer to LQNS User Manual.
 */
public enum ResultTypes {

	YES("YES"), // YES, for valid result.
	NO("NO");

	private String strValue;

	ResultTypes(String value) {
		this.strValue = value;
	}

	@Override
	public String toString() {
		return strValue;
	}

	public static ResultTypes getValue(String input) {
		for (ResultTypes tsk : ResultTypes.values()) {
			if (tsk.strValue.equalsIgnoreCase(input)) {
				return tsk;
			}
		}
		return null;
	}
}
