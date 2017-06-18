package ca.appsimulations.jlqninterface.core.lqns.entities;
import ca.appsimulations.jlqninterface.core.lqns.model.LqnModel;

/**
 * @author Yasir Shoaib (2011,2012) Contributors: Yasir Shoaib - Implementation
 * 
 *         Some LQN classes and their members are outlined as UML class diagrams
 *         in LQNS User Manual. For details regarding these LQN classes and
 *         members refer to LQNS User Manual.
 */

public abstract class EntryMakingCallType extends MakingCallType {
	protected float prob;

	public EntryMakingCallType(LqnModel lqnModel, String strDestEntry, float prob) {
		super(lqnModel, strDestEntry);
		this.prob = prob;
	}

	public EntryMakingCallType(LqnModel lqnModel, Entry destEntry, float prob) {
		super(lqnModel, destEntry);
		this.prob = prob;
	}

	public float getProb() {
		return prob;
	}

	public void setProb(float prob) {
		this.prob = prob;
	}
}
