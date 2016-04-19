package ca.appsimulations.jlqninterface.lqns.entities;
import ca.appsimulations.jlqninterface.core.Model;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public abstract class Entity {
	protected int id = 0;
	protected Model workspace;
	protected Result result;
	protected double bStrength = LQNConstants.UNDEF.getConstantValue();
	protected double sat = 0;

	public abstract Result getResult();

	public abstract String getInformation();

	public double getBStrength() {
		return bStrength;
	}

	public double getSat() {
		return sat;
	}

	public void setBStrength(double bStren) {
		bStrength = bStren;
	}

	public void setSat(double sat) {
		this.sat = sat;
	}

}