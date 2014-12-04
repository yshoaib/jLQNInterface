package ca.appsimulations.jlqninterface.lqns.entities;
import ca.appsimulations.jlqninterface.core.Model;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public abstract class EntryMakingCallType extends MakingCallType {
	protected float prob;

	public EntryMakingCallType(Model workspace, String strDestEntry, float prob) {
		super(workspace, strDestEntry);
		this.prob = prob;
	}

	public EntryMakingCallType(Model workspace, Entry destEntry, float prob) {
		super(workspace, destEntry);
		this.prob = prob;
	}

	public float getProb() {
		return prob;
	}

	public void setProb(float prob) {
		this.prob = prob;
	}
}
