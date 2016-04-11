package ca.appsimulations.jlqninterface.lqns.entities;
/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public enum LQNConstants {
	INFINITY(Integer.MAX_VALUE, "@infinity"), UNDEF(Integer.MIN_VALUE, "@undef");

	private int intValue;
	private String strValue;

	@Override
	public String toString() {
		return strValue;
	}

	LQNConstants(int intInput, String strInput) {
		this.intValue = intInput;
		this.strValue = strInput;
	}

	public int getConstantValue() {
		return intValue;
	}

	public static LQNConstants getValue(String input) {
		for (LQNConstants cType : LQNConstants.values()) {
			if (cType.strValue.equalsIgnoreCase(input)) {
				return cType;
			}
		}
		return null;
	}

	public static LQNConstants getValue(int input) {
		for (LQNConstants cType : LQNConstants.values()) {
			if (cType.intValue == input) {
				return cType;
			}
		}
		return null;
	}

}